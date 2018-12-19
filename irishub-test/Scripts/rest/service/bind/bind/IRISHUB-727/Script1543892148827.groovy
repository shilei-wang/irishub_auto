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
import com.kms.katalon.core.annotation.Keyword
import com.kms.katalon.core.checkpoint.Checkpoint as Checkpoint

import internal.GlobalVariable as GlobalVariable
import rest.ServiceUtils
import rest.BaseTx
import rest.AccountUtils
import utils.CmdUtils as CmdUtils

import com.google.gson.JsonArray
import com.google.gson.JsonObject
	

defineData = findTestData('service/define/define/IRISHUB-624')
bindData = findTestData('service/bind/bind/IRISHUB-726')
errorData = findTestData('service/bind/bind/IRISHUB-727')
faucet = findTestData('base/faucet')
String name = faucet.getValue('name', 1)
String password = faucet.getValue('password', 1)
String fileContents = ServiceUtils.protoString()
JsonArray prices

serviceName = defineData.getValue("service-name", 1).concat("_").concat(CmdUtils.generateRandomID())
ServiceUtils.serviceDefineCorrectEg(name, password, serviceName, defineData)
ServiceUtils.serviceBindCorrectEg(name, password, serviceName, bindData)

String[] BaseTxInfo
//repeat bind error
responseBodyContent = ServiceUtils.serviceBindCorrectEg(name, password, serviceName, bindData)
WS.verifyEqual(responseBodyContent.contains(errorData.getValue("cmd_result", 1)), true)

//bind a service that is not existed error
BaseTxInfo = BaseTx.baseTxProduce(name,password)
responseBodyContent = ServiceUtils.serviceBindCorrectEg(name, password, "test_bind", bindData)
WS.verifyEqual(responseBodyContent.contains(errorData.getValue("cmd_result", 2)), true)

serviceName = defineData.getValue("service-name", 1).concat("_").concat(CmdUtils.generateRandomID())
ServiceUtils.serviceDefineCorrectEg(name, password, serviceName, defineData)
for (int i = 3; i <= errorData.getRowNumbers(); i++) {
	//repeat bind error
	prices = ServiceUtils.JsonArraySplitProduce(errorData.getValue("prices", i),",")
	BaseTxInfo = BaseTx.baseTxProduce(name, password)
	//(String BaseTx, String provider, String service_name, String def_chain_id, String binding_type, JsonArray prices, String deposit, String avg_rsp_time, String usable_time)
	String responseBodyContent = ServiceUtils.serviceBind(BaseTxInfo[0], BaseTxInfo[1], serviceName, GlobalVariable.chainId, errorData.getValue("bind-type", i), prices, errorData.getValue("deposit", i), errorData.getValue("avg-rsp-time", i), errorData.getValue("usable-time", i))
	AccountUtils.waitUntilNextBlock()
	WS.verifyEqual(responseBodyContent.contains(errorData.getValue("rest_result", i)), true)
}


