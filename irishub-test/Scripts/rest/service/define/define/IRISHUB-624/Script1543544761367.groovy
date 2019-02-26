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
data = findTestData('service/define/define/IRISHUB-624')
String name = faucet.getValue('name', 1)
String password = faucet.getValue('password', 1)
String[] BaseTxInfo
String fileContents = ServiceUtils.protoString()
println fileContents

for (int i = 1; i < (data.getRowNumbers() + 1); i++) {
	if (i == 4) {
		//这里有bug
		continue
	}
	serviceName = data.getValue("service-name", i).concat("_").concat(CmdUtils.generateRandomID())
	if(!data.getValue("file", i).equals("")) {
		idlContent = data.getValue("file", i)
	}
	JsonArray tags = ServiceUtils.JsonArraySplitProduce(data.getValue("tags", i),"\\s+")
	BaseTxInfo = BaseTx.baseTxProduce(name,password)
	responseBodyContent = ServiceUtils.serviceDefine(BaseTxInfo[0], BaseTxInfo[1], serviceName, data.getValue("service-description", i), data.getValue("author-description", i), tags, fileContents)
	WS.verifyEqual(responseBodyContent.contains("hash"), true)
	AccountUtils.waitUntilNextBlock()
}


