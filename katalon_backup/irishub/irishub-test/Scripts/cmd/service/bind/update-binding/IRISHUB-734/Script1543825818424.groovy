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
import utils.CmdUtils as CmdUtils
import utils.StringUtils as StringUtils
import cmd.ServiceUtils as ServiceUtils
import com.google.gson.JsonObject as JsonObject;
	
Utils u = new Utils();

for (int i = 1; i <= u.td.getRowNumbers(); i++) {
	cmd = u.command
	
	//第4个case， 不加任何非必填字段。不做任何更新。所对比的结果和第三个用例一致
	if (i != 4){
		cmd = CmdUtils.generateCmd(cmd, u.td, i)
	}
	
	response = CmdUtils.sendRequest('cmd/CmdWithOneArgs', cmd, "wait")
	WS.verifyEqual(StringUtils.stringContains(response.responseBodyContent,"tx hash"), true)	

	//调用binding命令获取更新后的bind信息
	bindInfo = ServiceUtils.getBindingInfo(u.v0, u.serviceName) 
	re = CmdUtils.Parse(bindInfo).get("value").getAsJsonObject()
	
	//验证bind-type
	WS.verifyEqual(re.get("binding_type").getAsString(), u.td.getValue("bind-type", i))
	
	//验证deposit
	expect_deposit = CmdUtils.plusIris("1100iris", u.td.getValue("deposit", i))
	deposit_array = re.get("deposit").getAsJsonArray()	
	WS.verifyEqual(deposit_array.get(0).getAsJsonObject().get("amount").getAsString(), expect_deposit)
	
	//验证price	
	String[] priceArray = u.td.getValue("prices", i).split(",");
	expect_price_0 = CmdUtils.IrisToIrisatto(priceArray[0])
	expect_price_1 = CmdUtils.IrisToIrisatto(priceArray[1])
	price_array = re.get("price").getAsJsonArray()	
	WS.verifyEqual(price_array.get(0).getAsJsonObject().get("amount").getAsString(), expect_price_0)	
	WS.verifyEqual(price_array.get(1).getAsJsonObject().get("amount").getAsString(), expect_price_1)
	
	//验证level	
	level = re.get("level").getAsJsonObject()
	WS.verifyEqual(level.get("avg_rsp_time").getAsString(), u.td.getValue("avg-rsp-time", i))
	WS.verifyEqual(level.get("usable_time").getAsString(), u.td.getValue("usable-time", i))
	
	//验证available
	WS.verifyEqual(re.get("available").getAsString(), "true")

	//CmdUtils.pl(cmd)
}





/*
 * utils
 */

class Utils {
	public TestData td;
	public String command;
	public String serviceName;
	public String v0;
	
	public Utils(){
		td = findTestData('service/bind/update-binding/IRISHUB-734')
		TestData faucet = findTestData('base/faucet')
		v0 = faucet.getValue('name', 1)
		
		serviceName = ServiceUtils.defineService(v0)
		ServiceUtils.bindService(v0, serviceName)
		
		command = 'iriscli service update-binding'.concat(GlobalVariable.chainId).concat(GlobalVariable.defchainId).concat(GlobalVariable.node)
			.concat(' --from=').concat(v0)+" --service-name="+serviceName
		command = CmdUtils.addTxFee(command, findTestData('base/tx'), 1)
	}
}

