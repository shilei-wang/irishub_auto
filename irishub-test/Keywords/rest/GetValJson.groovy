package rest

import static com.kms.katalon.core.checkpoint.CheckpointFactory.findCheckpoint
import static com.kms.katalon.core.testcase.TestCaseFactory.findTestCase
import static com.kms.katalon.core.testdata.TestDataFactory.findTestData
import static com.kms.katalon.core.testobject.ObjectRepository.findTestObject

import com.kms.katalon.core.annotation.Keyword
import com.kms.katalon.core.checkpoint.Checkpoint
import com.kms.katalon.core.cucumber.keyword.CucumberBuiltinKeywords as CucumberKW
import com.kms.katalon.core.mobile.keyword.MobileBuiltInKeywords as Mobile
import com.kms.katalon.core.model.FailureHandling
import com.kms.katalon.core.testcase.TestCase
import com.kms.katalon.core.testdata.TestData
import com.kms.katalon.core.testobject.TestObject
import com.kms.katalon.core.webservice.keyword.WSBuiltInKeywords as WS
import com.kms.katalon.core.webui.keyword.WebUiBuiltInKeywords as WebUI
import internal.GlobalVariable

import com.google.gson.JsonObject
import groovy.json.JsonSlurper
import com.kms.katalon.core.testobject.ResponseObject



public class GetValJson {
	@Keyword
	public static String getFirstValAddress() {

		ResponseObject response = WS.sendRequest(findTestObject('rest/stake/validators/ICS21_get_stake_validators', [ ('lcdIP') : GlobalVariable.lcdIP]))
		JsonSlurper slurper = new JsonSlurper()
		def parsedJson = slurper.parseText(response.responseBodyContent)
		System.out.println(parsedJson[0])
		Map valInfo = parsedJson[0]
		String valAdresss = valInfo.get("operator_address")
		return valAdresss
	}
}
