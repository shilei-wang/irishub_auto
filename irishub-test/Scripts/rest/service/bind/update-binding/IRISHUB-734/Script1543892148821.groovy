import com.kms.katalon.core.webui.keyword.WebUiBuiltInKeywords as WebUI
import com.kms.katalon.core.mobile.keyword.MobileBuiltInKeywords as Mobile
import com.kms.katalon.core.cucumber.keyword.CucumberBuiltinKeywords as CucumberKW
import com.kms.katalon.core.webservice.keyword.WSBuiltInKeywords as WS
import static com.kms.katalon.core.testobject.ObjectRepository.findTestObject
import static com.kms.katalon.core.testdata.TestDataFactory.findTestData
import static com.kms.katalon.core.testcase.TestCaseFactory.findTestCase
import static com.kms.katalon.core.checkpoint.CheckpointFactory.findCheckpoint
import com.kms.katalon.core.model.FailureHandling as FailureHandling
import com.kms.katalon.core.testcase.TestCase as TestCase
import com.kms.katalon.core.testdata.TestData as TestData
import com.kms.katalon.core.testobject.ResponseObject as ResponseObject
import com.kms.katalon.core.testobject.TestObject as TestObject
import com.kms.katalon.core.checkpoint.Checkpoint as Checkpoint

import internal.GlobalVariable as GlobalVariable

import rest.ServiceUtils
import rest.BaseTx
import rest.AccountUtils
import rest.GetAddressByKey
import utils.CmdUtils as CmdUtils
import utils.StringUtils
import com.google.gson.JsonArray
import com.google.gson.JsonObject
	

updateBindingData = findTestData('service/bind/update-binding/IRISHUB-734')

faucet = findTestData('base/faucet')
v0 = faucet.getValue('name', 1)
password = faucet.getValue('password', 1)
defineData = findTestData('service/define/define/IRISHUB-624')
bindData = findTestData('service/bind/bind/IRISHUB-726')
serviceName = defineData.getValue("service-name", 1).concat("_").concat(CmdUtils.generateRandomID())

ServiceUtils.serviceDefineCorrectEg(v0, password, serviceName, defineData)
ServiceUtils.serviceBindCorrectEg(v0, password, serviceName, bindData)
String[] BaseTxInfo
JsonArray prices

for (int i = 1; i <= updateBindingData.getRowNumbers(); i++) {
	BaseTxInfo = BaseTx.baseTxProduce(v0, password)
	
	//第4个case， 不加任何非必填字段。不做任何更新。所对比的结果和第三个用例一致
	if (i != 4) {
		prices = ServiceUtils.JsonArraySplitProduce(updateBindingData.getValue("prices", i),",")
		response = ServiceUtils.serviceBindingUpdate(BaseTxInfo[0], BaseTxInfo[1], serviceName, GlobalVariable.chainId, updateBindingData.getValue("bind-type", i), prices, updateBindingData.getValue("deposit", i), updateBindingData.getValue("avg-rsp-time", i), updateBindingData.getValue("usable-time", i))
		WS.verifyEqual(StringUtils.stringContains(response.responseBodyContent,"hash"), true)
	} 
	else {
		response = ServiceUtils.serviceBindingUpdateWithoutParams(BaseTxInfo[0], BaseTxInfo[1], serviceName, GlobalVariable.chainId)
		WS.verifyEqual(StringUtils.stringContains(response.responseBodyContent,"hash"), true)
	}
	AccountUtils.waitUntilNextBlock()
	//调用binding命令获取更新后的bind信息 (String serviceName, String defChainId, String bindChainId, String provider)
	response = ServiceUtils.getServiceBinding(serviceName, GlobalVariable.chainId, GlobalVariable.chainId, BaseTxInfo[1]) 
	re = CmdUtils.Parse(response.responseBodyContent).getAsJsonObject()
	
	//验证bind-type
	WS.verifyEqual(re.get("binding_type").getAsString(), updateBindingData.getValue("bind-type", i))
	
	//验证deposit
	expect_deposit = CmdUtils.plusIris("1000iris", updateBindingData.getValue("deposit", i))
	deposit_array = re.get("deposit").getAsJsonArray()	
	WS.verifyEqual(deposit_array.get(0).getAsJsonObject().get("amount").getAsString(), expect_deposit)
	
	//验证price	
	String[] priceArray = updateBindingData.getValue("prices", i).split(",");
	expect_price_0 = CmdUtils.IrisToIrisatto(priceArray[0])
	expect_price_1 = CmdUtils.IrisToIrisatto(priceArray[1])
	price_array = re.get("price").getAsJsonArray()	
	WS.verifyEqual(price_array.get(0).getAsJsonObject().get("amount").getAsString(), expect_price_0)	
	WS.verifyEqual(price_array.get(1).getAsJsonObject().get("amount").getAsString(), expect_price_1)
	
	//验证level	
	level = re.get("level").getAsJsonObject()
	WS.verifyEqual(level.get("avg_rsp_time").getAsString(), updateBindingData.getValue("avg-rsp-time", i))
	WS.verifyEqual(level.get("usable_time").getAsString(), updateBindingData.getValue("usable-time", i))
	
	//验证available
	WS.verifyEqual(re.get("available").getAsString(), "true")
}


