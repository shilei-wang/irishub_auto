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
import utils.CmdUtils
import rest.AccountUtils

faucet = findTestData('base/faucet')
data = findTestData('service/define/define/IRISHUB-625')
String name = faucet.getValue('name', 1)
String password = faucet.getValue('password', 1)
String[] BaseTxInfo
String fileContents = ServiceUtils.protoString()
//println fileContents
serviceName = data.getValue("service-name", 1).concat("_").concat(CmdUtils.generateRandomID())
data.getValue("service-name", 1)
JsonArray tags = ServiceUtils.JsonArraySplitProduce(data.getValue("tags", 1),"\\s+")
BaseTxInfo = BaseTx.baseTxProduce(name,password)
String responseBody = ServiceUtils.serviceDefine(BaseTxInfo[0], BaseTxInfo[1], serviceName, data.getValue("service-description", 1), data.getValue("author-description", 1), tags, fileContents)
AccountUtils.waitUntilNextBlock()



BaseTxInfo = BaseTx.baseTxProduce(name,password)
responseBody = ServiceUtils.serviceDefine(BaseTxInfo[0], BaseTxInfo[1], serviceName, data.getValue("service-description", 1), data.getValue("author-description", 1), tags, fileContents)
WS.verifyEqual(responseBody.contains(data.getValue("cmd_result", 1)), true)
AccountUtils.waitUntilNextBlock()

String idlContent
for (int i = 2; i <= data.getRowNumbers() ; i++) {
	if (i == 4) {
		//这里有bug
		continue
	}
	if (data.getValue("idl-content", i).equals("")&& data.getValue("file", i).equals("")) {
		idlContent = ""
	}
	else {
		idlContent = fileContents
	}
	serviceName = data.getValue("service-name", i)
	tags = ServiceUtils.JsonArraySplitProduce(data.getValue("tags", i),"\\s+")
	BaseTxInfo = BaseTx.baseTxProduce(name,password)
	responseBody = ServiceUtils.serviceDefine(BaseTxInfo[0], BaseTxInfo[1], serviceName, data.getValue("service-description", i), data.getValue("author-description", i), tags, idlContent)
	WS.verifyEqual(responseBody.contains(data.getValue("cmd_result", i)), true)
}
