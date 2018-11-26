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
import rest.GetValJson
import utils.StringUtils

String validatorAddr
TestData data = findTestData('distribution/delegation-distr-info/IRISHUB-812')

for (def index : (1..data.getRowNumbers())) {
	validatorAddr = data.getValue("address-validator", index)
	if (validatorAddr.equals("default")) {
		validatorAddr = GetValJson.getFirstValAddress();
	}
	response = WS.sendRequest(findTestObject('rest/distribution/ICS24_get_distribution_delegatorAddr_distrInfo_validatorAddr', [ ('delegatorAddr') : data.getValue("address-delegator", index), ('validatorAddr') : validatorAddr, ('lcdIP') : GlobalVariable.lcdIP]))
	System.out.println(response.responseBodyContent)
	if (index == 1) {
		WS.verifyResponseStatusCode(response, 204)
	}
	else {
		WS.verifyEqual(StringUtils.stringContains(response.responseBodyContent,data.getValue("rest_result", index)), true)
	}
	sleep(500)
}