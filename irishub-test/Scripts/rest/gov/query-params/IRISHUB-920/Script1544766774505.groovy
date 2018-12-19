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
import rest.GovUtils
import utils.CmdUtils as CmdUtils
import utils.StringUtils as StringUtils
import rest.AccountUtils
import com.google.gson.JsonObject as JsonObject
	

data = findTestData('gov/query-params/IRISHUB-920')

ResponseObject response = WS.sendRequest(findTestObject('rest/governance/ICS22_get_gov_params', [ ('lcdIP') : GlobalVariable.lcdIP]))
System.out.println(response.responseBodyContent)
return response

WS.verifyEqual(response.responseBodyContent.contains(data.getValue("key", 1)), true)
WS.verifyEqual(response.responseBodyContent.contains(data.getValue("key", 2)), true)
WS.verifyEqual(response.responseBodyContent.contains(data.getValue("key", 3)), true)
