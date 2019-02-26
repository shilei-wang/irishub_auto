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
respondData = findTestData('service/invocation/respond/IRISHUB-789')
responseData = findTestData('service/invocation/response/IRISHUB-791')
feesQueryData = findTestData('service/invocation/fees/IRISHUB-833')

ServiceUtils.serviceDefineCorrectEg(v0, password, serviceName, defineData)
ServiceUtils.serviceBindCorrectEg(v0, password, serviceName, bindData)
String[] BaseTxInfo =  BaseTx.baseTxProduce(v0, password)

// returned fee ， income fee都没有的情况下， 查询fees值
ServiceUtils.serviceFeesClean(v0, password, BaseTxInfo[1])

response = ServiceUtils.serviceFeesQuery(BaseTxInfo[1])
String amount = ServiceUtils.serviceFeesCaculate(response.responseBodyContent,"returned_fee")
actual = Double.valueOf(amount.replace("iris", ""))
expected = Double.valueOf(feesQueryData.getValue("returned_fee", 1).replace("iris", ""))
WS.verifyEqual(actual, expected)

response = ServiceUtils.serviceFeesQuery(BaseTxInfo[1])
amount = ServiceUtils.serviceFeesCaculate(response.responseBodyContent,"incoming_fee")
actual = Double.valueOf(amount.replace("iris", ""))
expected = Double.valueOf(feesQueryData.getValue("incoming_fee", 1).replace("iris", ""))
WS.verifyEqual(actual, expected)

//只有 returned fee ，没有 income fee的情况下， 查询fees值
ServiceUtils.serviceFeesClean(v0, password, BaseTxInfo[1])
request_id = ServiceUtils.serviceRequestCallEg(v0, password, serviceName, serviceCallData)
AccountUtils.waitUntilSeveralBlock(5)

response = ServiceUtils.serviceFeesQuery(BaseTxInfo[1])
amount = ServiceUtils.serviceFeesCaculate(response.responseBodyContent,"returned_fee")
actual = Double.valueOf(amount.replace("iris", ""))
expected = Double.valueOf(feesQueryData.getValue("returned_fee", 2).replace("iris", ""))
WS.verifyEqual(actual, expected)

response = ServiceUtils.serviceFeesQuery(BaseTxInfo[1])
amount = ServiceUtils.serviceFeesCaculate(response.responseBodyContent,"incoming_fee")
actual = Double.valueOf(amount.replace("iris", ""))
expected = Double.valueOf(feesQueryData.getValue("incoming_fee", 2).replace("iris", ""))
WS.verifyEqual(actual, expected)

// 只有 income fee ，没有returned fee的情况下， 查询fees值
ServiceUtils.serviceFeesClean(v0, password, BaseTxInfo[1])
request_id = ServiceUtils.serviceRequestCallEg(v0, password, serviceName, serviceCallData)
BaseTxInfo =  BaseTx.baseTxProduce(v0, password)
response = ServiceUtils.serviceRespond(BaseTxInfo[0], GlobalVariable.chainId, request_id, respondData.getValue("response-data",1), BaseTxInfo[1])
AccountUtils.waitUntilNextBlock()

response = ServiceUtils.serviceFeesQuery(BaseTxInfo[1])
amount = ServiceUtils.serviceFeesCaculate(response.responseBodyContent,"returned_fee")
actual = Double.valueOf(amount.replace("iris", ""))
expected = Double.valueOf(feesQueryData.getValue("returned_fee", 3).replace("iris", ""))
WS.verifyEqual(actual, expected)
println amount
println actual
println expected
response = ServiceUtils.serviceFeesQuery(BaseTxInfo[1])
amount = ServiceUtils.serviceFeesCaculate(response.responseBodyContent,"incoming_fee")
actual = Double.valueOf(amount.replace("iris", ""))
expected = Double.valueOf(CmdUtils.tax(feesQueryData.getValue("incoming_fee", 3), 0.02).replace("iris", ""))
WS.verifyEqual(actual, expected)
println amount
println actual
println expected

// returned fee和 income fee 都有的情况下， 查询fees值
ServiceUtils.serviceFeesClean(v0, password, BaseTxInfo[1])
ServiceUtils.serviceRequestCallEg(v0, password, serviceName, serviceCallData)
AccountUtils.waitUntilSeveralBlock(5)
request_id = ServiceUtils.serviceRequestCallEg(v0, password, serviceName, serviceCallData)
BaseTxInfo =  BaseTx.baseTxProduce(v0, password)
response = ServiceUtils.serviceRespond(BaseTxInfo[0], GlobalVariable.chainId, request_id, respondData.getValue("response-data",1), BaseTxInfo[1])
AccountUtils.waitUntilNextBlock()

response = ServiceUtils.serviceFeesQuery(BaseTxInfo[1])
amount = ServiceUtils.serviceFeesCaculate(response.responseBodyContent,"returned_fee")
actual = Double.valueOf(amount.replace("iris", ""))
expected = Double.valueOf(feesQueryData.getValue("returned_fee", 4).replace("iris", ""))
println amount
println actual
println expected
WS.verifyEqual(actual, expected)

response = ServiceUtils.serviceFeesQuery(BaseTxInfo[1])
amount = ServiceUtils.serviceFeesCaculate(response.responseBodyContent,"incoming_fee")
actual = Double.valueOf(amount.replace("iris", ""))
expected = Double.valueOf(CmdUtils.tax(feesQueryData.getValue("incoming_fee", 4), 0.02).replace("iris", ""))
println amount
println actual
println expected
WS.verifyEqual(actual, expected)
