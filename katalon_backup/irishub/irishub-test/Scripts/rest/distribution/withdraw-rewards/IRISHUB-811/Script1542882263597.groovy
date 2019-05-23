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
import com.google.gson.Gson
import com.google.gson.JsonObject
import com.google.gson.JsonParser
import utils.StringUtils as StringUtils
import utils.CmdUtils
import rest.AccountUtils


String[] BaseTxInfo = new String[2] 
TestData faucet = findTestData('base/faucet')
String name = faucet.getValue('name', 1)
String password = faucet.getValue('password', 1)
TestData data = findTestData('distribution/withdraw-rewards/IRISHUB-811')
//System.out.println(AccountUtils.createNewAccount())

for (int i = 0; i < 6 ; i++) {
	BaseTxInfo = BaseTx.baseTxProduce(name, password);
	String HttpBody = BaseTx.HttpBodySwitch(BaseTxInfo[0],BaseTxInfo[1],i)
	if (i !=2)
	{
		response =  WS.sendRequest(findTestObject('rest/distribution/ICS24_post_distribution_delegatorAddr_withdrawReward', [ ('delegatorAddr') : BaseTxInfo[1], ('HttpBody') : HttpBody, ('lcdIP') : GlobalVariable.lcdIP]))
		System.out.println(response.responseBodyContent)
		WS.verifyEqual(StringUtils.stringContains(response.responseBodyContent,data.getValue('rest_result', i+1)), true)
	}
	AccountUtils.waitUntilNextBlock()
}


