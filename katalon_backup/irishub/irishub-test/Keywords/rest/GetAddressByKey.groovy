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
import com.google.gson.JsonObject
import groovy.json.JsonSlurper
import com.kms.katalon.core.testobject.ResponseObject

import internal.GlobalVariable

public class GetAddressByKey {

	@Keyword
	public static String getAddressByKey(String keyName) {

		ResponseObject response = WS.sendRequest(findTestObject('rest/keys/ICS1_get_keys_name', [ ('name') : keyName, ('property') : "acc", ('lcdIP') : GlobalVariable.lcdIP]))
		JsonSlurper slurper = new JsonSlurper()
		Map parsedJson = slurper.parseText(response.responseBodyContent)
		String addresss = parsedJson.get("address")
		return addresss
	}
}
