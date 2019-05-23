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
import rest.ServiceUtils
import rest.BaseTx
import rest.AccountUtils
import rest.GetAddressByKey
import utils.CmdUtils as CmdUtils
import utils.StringUtils
import com.google.gson.JsonArray
import com.google.gson.JsonObject

faucet = findTestData('base/faucet')
v0 = faucet.getValue('name', 1)
password = faucet.getValue('password', 1)

defineData = findTestData('service/define/define/IRISHUB-624')
bindData = findTestData('service/bind/bind/IRISHUB-726')
serviceName = defineData.getValue("service-name", 1).concat("_").concat(CmdUtils.generateRandomID())
serviceCallData = findTestData('service/invocation/call/IRISHUB-785')

ServiceUtils.serviceDefineCorrectEg(v0, password, serviceName, defineData)
ServiceUtils.serviceBindCorrectEg(v0, password, serviceName, bindData)

prices = ServiceUtils.JsonArraySplitProduce("2iris,0iris",",")
BaseTxInfo = BaseTx.baseTxProduce(v0, password)
//serviceBindingUpdateEg(String BaseTx, String provider, String service_name, String def_chain_id, JsonArray prices, String deposit){
response = ServiceUtils.serviceBindingUpdateEg(BaseTxInfo[0], BaseTxInfo[1], serviceName, GlobalVariable.chainId, prices, "1000iris")
WS.verifyEqual(StringUtils.stringContains(response.responseBodyContent,"hash"), true)
AccountUtils.waitUntilNextBlock()

providerAddress = GetAddressByKey.getAddressByKey(v0)

Vector methodAll = new Vector()
JsonObject method
for (int i = 1; i <= serviceCallData.getRowNumbers(); i++) {
	balance_before = BaseTx.getAccountBalance(providerAddress)
	//String BaseTx, String provider, String service_name, String def_chain_id, String bind_chain_id, String method_id, String service_fee, String data, String consumer
	BaseTxInfo = BaseTx.baseTxProduce(v0, password)
	ServiceUtils.serviceRequestOneCall(BaseTxInfo[0], BaseTxInfo[1], serviceName, GlobalVariable.chainId, GlobalVariable.chainId, Integer.parseInt(serviceCallData.getValue("method-id",i)), serviceCallData.getValue("service-fee",i), serviceCallData.getValue("request-data",i), BaseTxInfo[1])
	WS.verifyEqual(StringUtils.stringContains(response.responseBodyContent,"hash"), true)
	AccountUtils.waitUntilNextBlock()
	balance_after = BaseTx.getAccountBalance(providerAddress)
	println balance_before
	println balance_after
	actual = balance_before - balance_after
	expected = Double.valueOf(serviceCallData.getValue("cmd_result", i).replace("iris", ""))
	WS.verifyEqual(CmdUtils.compareIgnoreFee(actual, expected), true)
	
	method = ServiceUtils.requestsJsonObjectProduce(BaseTxInfo[1] ,serviceName, GlobalVariable.chainId, GlobalVariable.chainId, Integer.parseInt(serviceCallData.getValue("method-id",i)), serviceCallData.getValue("service-fee",i), serviceCallData.getValue("request-data",i), BaseTxInfo[1])
	methodAll.add(method)
}

//4 method request call

JsonArray requestAll = ServiceUtils.requestsJsonArrayProduce(methodAll)
balance_before = BaseTx.getAccountBalance(providerAddress)
//String BaseTx, String provider, String service_name, String def_chain_id, String bind_chain_id, String method_id, String service_fee, String data, String consumer
BaseTxInfo = BaseTx.baseTxProduce(v0, password)
ServiceUtils.serviceRequestListCall(BaseTxInfo[0], requestAll)
WS.verifyEqual(StringUtils.stringContains(response.responseBodyContent,"hash"), true)
AccountUtils.waitUntilNextBlock()
balance_after = BaseTx.getAccountBalance(providerAddress)
println balance_before
println balance_after
actual = balance_before - balance_after
expected = Double.valueOf("4")
WS.verifyEqual(CmdUtils.compareIgnoreFee(actual, expected), true)

//BaseTxInfo = BaseTx.baseTxProduce(v0,password)
//response = ServiceUtils.serviceRequestsQuery(BaseTxInfo[1], serviceName, GlobalVariable.chainId, GlobalVariable.chainId)

