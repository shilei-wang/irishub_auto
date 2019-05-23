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
import utils.CmdUtils as CmdUtils

import com.google.gson.JsonArray
import com.google.gson.JsonObject
	

defineData = findTestData('service/define/define/IRISHUB-624')
faucet = findTestData('base/faucet')
String name = faucet.getValue('name', 1)
String password = faucet.getValue('password', 1)
String fileContents = ServiceUtils.protoString()
JsonArray tags
JsonArray prices
data = findTestData('service/bind/bind/IRISHUB-726')

for (int i = 1; i <= data.getRowNumbers(); i++) {
	//define service
	tags= ServiceUtils.JsonArraySplitProduce(defineData.getValue("tags", 1),"\\s+")
	BaseTxInfo = BaseTx.baseTxProduce(name,password)
	serviceName = defineData.getValue("service-name", 1).concat("_").concat(CmdUtils.generateRandomID())
	responseBodyContent = ServiceUtils.serviceDefine(BaseTxInfo[0], BaseTxInfo[1], serviceName, defineData.getValue("service-description", 1), defineData.getValue("author-description", 1), tags, fileContents)
	WS.verifyEqual(responseBodyContent.contains("hash"), true)
	responseBodyContent = null
	println responseBodyContent
	AccountUtils.waitUntilNextBlock()
	
	//bind service
	prices = ServiceUtils.JsonArraySplitProduce(data.getValue("prices", i),",")
	BaseTxInfo = BaseTx.baseTxProduce(name,password)
	//(String BaseTx, String provider, String service_name, String def_chain_id, String binding_type, JsonArray prices, String deposit, String avg_rsp_time, String usable_time)
	responseBodyContent = ServiceUtils.serviceBind(BaseTxInfo[0], BaseTxInfo[1], serviceName, GlobalVariable.chainId, data.getValue("bind-type", i), prices, data.getValue("deposit", i), data.getValue("avg-rsp-time", i), data.getValue("usable-time", i))
	WS.verifyEqual(responseBodyContent.contains("hash"), true)
	AccountUtils.waitUntilNextBlock()
}



