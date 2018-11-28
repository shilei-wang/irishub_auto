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
import rest.BaseTx
import utils.StringUtils
import rest.AccountUtils


String[] BaseTxInfo
TestData data = findTestData('distribution/set-withdraw-addr/IRISHUB-784')
TestData faucet = findTestData('base/faucet')
String name = faucet.getValue('name', 1)
String password = faucet.getValue('password', 1)

for (def index : (1..data.getRowNumbers())) {
	BaseTxInfo = BaseTx.baseTxProduce(name,password);
	response = WS.sendRequest(findTestObject('rest/distribution/ICS24_post_distribution_delegatorAddr_withdrawAddress', [ ('delegatorAddr') : BaseTxInfo[1],('basetx') : BaseTxInfo[0],('withdraw_address') : data.getValue("set-withdraw-addr", index), ('lcdIP') : GlobalVariable.lcdIP]))
	WS.verifyResponseStatusCode(response, 400)
	System.out.println(response.responseBodyContent)
	WS.verifyEqual(StringUtils.stringContains(response.responseBodyContent,data.getValue("rest_result", index)), true)
	AccountUtils.waitUntilNextBlock()
}


