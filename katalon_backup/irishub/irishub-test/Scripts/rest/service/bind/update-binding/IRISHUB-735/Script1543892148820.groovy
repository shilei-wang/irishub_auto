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
import rest.ServiceUtils
import rest.BaseTx
import rest.AccountUtils
import rest.GetAddressByKey
import utils.StringUtils
import com.google.gson.JsonArray
import com.google.gson.JsonObject

faucet = findTestData('base/faucet')
v0 = faucet.getValue('name', 1)
v1 = faucet.getValue('name', 2)
password = faucet.getValue('password', 1)
defineData = findTestData('service/define/define/IRISHUB-624')
bindData = findTestData('service/bind/bind/IRISHUB-726')
refundData = findTestData('service/bind/refund-deposit/IRISHUB-737')
serviceName = defineData.getValue("service-name", 1).concat("_").concat(CmdUtils.generateRandomID())

ServiceUtils.serviceDefineCorrectEg(v0, password, serviceName, defineData)
ServiceUtils.serviceBindCorrectEg(v0, password, serviceName, bindData)

updateBindingData = findTestData('service/bind/update-binding/IRISHUB-735')

String requestServiceName

for (int i = 1; i <= updateBindingData.getRowNumbers(); i++) {
	//第1个case
	if (i == 1){
		requestServiceName = "test"
	} else {
		requestServiceName = serviceName
	}
	BaseTxInfo = BaseTx.baseTxProduce(v0, password)
	prices = ServiceUtils.JsonArraySplitProduce(updateBindingData.getValue("prices", i),",")
	response = ServiceUtils.serviceBindingUpdate(BaseTxInfo[0], BaseTxInfo[1], requestServiceName, GlobalVariable.chainId, updateBindingData.getValue("bind-type", i), prices, updateBindingData.getValue("deposit", i), updateBindingData.getValue("avg-rsp-time", i), updateBindingData.getValue("usable-time", i))
	WS.verifyEqual(StringUtils.stringContains(response.responseBodyContent,updateBindingData.getValue("cmd_result", i)), true)
	AccountUtils.waitUntilNextBlock()
}

