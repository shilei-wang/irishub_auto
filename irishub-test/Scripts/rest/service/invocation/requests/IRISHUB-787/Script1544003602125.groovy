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
requestsQueryData = findTestData('service/invocation/requests/IRISHUB-787')

ServiceUtils.serviceDefineCorrectEg(v0, password, serviceName, defineData)
ServiceUtils.serviceBindCorrectEg(v0, password, serviceName, bindData)
ServiceUtils.serviceRequestCallEg(v0, password, serviceName, serviceCallData)

//在有调用请求的情况下，服务提供者查询自己绑定的某一服务的请求列表.
//(String provider, String serviceName, String defChainId, String bindChainId)
String[] BaseTxInfo = BaseTx.baseTxProduce(v0,password)
response = ServiceUtils.serviceRequestsQuery(BaseTxInfo[1], serviceName, GlobalVariable.chainId, GlobalVariable.chainId)

binding_array = CmdUtils.ParseArray(response.responseBodyContent)

binding_item = binding_array.get(0).getAsJsonObject()
WS.verifyEqual(binding_item.get("def_name").getAsString(), serviceName)
WS.verifyEqual(binding_item.get("def_chain_id").getAsString(), GlobalVariable.chainId)
WS.verifyEqual(binding_item.get("bind_chain_id").getAsString(), GlobalVariable.chainId)
WS.verifyEqual(binding_item.get("req_chain_id").getAsString(), GlobalVariable.chainId)
WS.verifyEqual(binding_item.get("method_id").toString(), requestsQueryData.getValue("method_id", 1))
WS.verifyEqual(binding_item.get("provider").getAsString(), BaseTxInfo[1])
WS.verifyEqual(binding_item.get("consumer").getAsString(), BaseTxInfo[1])
WS.verifyEqual(binding_item.get("input").getAsString(), requestsQueryData.getValue("input", 1))
deposit_array = binding_item.get("service_fee").getAsJsonArray()
WS.verifyEqual(deposit_array.get(0).getAsJsonObject().get("amount").getAsString(), requestsQueryData.getValue("service_fee", 1))


//在最大请求时间已经过期的情况下，查询该请求是否会从列表里删除, 目前设置5个块
AccountUtils.waitUntilSeveralBlock(5)

response = ServiceUtils.serviceRequestsQuery(BaseTxInfo[1], serviceName, GlobalVariable.chainId, GlobalVariable.chainId)
WS.verifyEqual(response.getStatusCode(), 204)


