import static com.kms.katalon.core.checkpoint.CheckpointFactory.findCheckpoint
import static com.kms.katalon.core.testcase.TestCaseFactory.findTestCase
import static com.kms.katalon.core.testdata.TestDataFactory.findTestData
import static com.kms.katalon.core.testobject.ObjectRepository.findTestObject
import com.kms.katalon.core.checkpoint.Checkpoint as Checkpoint
import com.kms.katalon.core.cucumber.keyword.CucumberBuiltinKeywords as CucumberKW
import com.kms.katalon.core.mobile.keyword.MobileBuiltInKeywords as Mobile
import com.kms.katalon.core.model.FailureHandling as FailureHandling
import com.kms.katalon.core.testcase.TestCase as TestCase
import com.kms.katalon.core.testdata.TestData as TestData
import com.kms.katalon.core.testobject.TestObject as TestObject
import com.kms.katalon.core.webservice.keyword.WSBuiltInKeywords as WS
import com.kms.katalon.core.webui.keyword.WebUiBuiltInKeywords as WebUI
import internal.GlobalVariable as GlobalVariable
import rest.ServiceUtils
import rest.BaseTx
import com.google.gson.JsonArray
import com.google.gson.JsonObject
import utils.CmdUtils
import rest.AccountUtils

faucet = findTestData('base/faucet')
data = findTestData('service/define/definition/IRISHUB-728')
String name = faucet.getValue('name', 1)
String password = faucet.getValue('password', 1)
String[] BaseTxInfo
String fileContents = ServiceUtils.protoString()
//println fileContents
serviceName = data.getValue("service-name", 1).concat("_").concat(CmdUtils.generateRandomID())
data.getValue("service-name", 1)
JsonArray tags = ServiceUtils.JsonArraySplitProduce(data.getValue("tags", 1),",")
BaseTxInfo = BaseTx.baseTxProduce(name,password)
String responseBody = ServiceUtils.serviceDefine(BaseTxInfo[0], BaseTxInfo[1], serviceName, data.getValue("service-description", 1), data.getValue("author-description", 1), tags, fileContents)
AccountUtils.waitUntilNextBlock()

response = ServiceUtils.serviceDefinition(serviceName, GlobalVariable.chainId)
re = CmdUtils.Parse(response.responseBodyContent).get("definition").getAsJsonObject()
WS.verifyEqual(re.get("name").getAsString(), serviceName)

//查询请求列表时，-def-chain-id、-service-name分别设置为空
response = ServiceUtils.serviceDefinition(serviceName, "")
WS.verifyEqual(response.responseBodyContent.contains("404 page not found"), true)

response = ServiceUtils.serviceDefinition("", GlobalVariable.chainId)
WS.verifyEqual(response.responseBodyContent.contains("404 page not found"), true)

//查询一个未定义的service name
response = ServiceUtils.serviceDefinition("ABC", GlobalVariable.chainId)
WS.verifyResponseStatusCode(response, 204)

//指定一个不存在的def-chain-id进行查询
response = ServiceUtils.serviceDefinition(serviceName, "ABC")
WS.verifyResponseStatusCode(response, 204)

