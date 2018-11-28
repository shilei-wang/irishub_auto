package rest

import static com.kms.katalon.core.checkpoint.CheckpointFactory.findCheckpoint
import static com.kms.katalon.core.testcase.TestCaseFactory.findTestCase
import static com.kms.katalon.core.testdata.TestDataFactory.findTestData
import static com.kms.katalon.core.testobject.ObjectRepository.findTestObject

import com.google.gson.Gson
import com.google.gson.JsonObject
import com.google.gson.JsonArray
import com.google.gson.JsonParser
import groovy.json.JsonSlurper
import com.kms.katalon.core.annotation.Keyword
import com.kms.katalon.core.checkpoint.Checkpoint
import com.kms.katalon.core.cucumber.keyword.CucumberBuiltinKeywords as CucumberKW
import com.kms.katalon.core.mobile.keyword.MobileBuiltInKeywords as Mobile
import com.kms.katalon.core.model.FailureHandling
import com.kms.katalon.core.testcase.TestCase
import com.kms.katalon.core.testdata.TestData
import com.kms.katalon.core.testobject.ResponseObject
import com.kms.katalon.core.testobject.TestObject
import com.kms.katalon.core.webservice.keyword.WSBuiltInKeywords as WS
import com.kms.katalon.core.webui.keyword.WebUiBuiltInKeywords as WebUI

import internal.GlobalVariable
import rest.GetAddressByKey
import utils.StringUtils as StringUtils
import rest.AccountUtils

public class StakeUtils {

	@Keyword
	public static Map getValidatorList() {
		ResponseObject response = WS.sendRequest(findTestObject('rest/stake/validators/ICS21_get_stake_validators', [ ('lcdIP') : GlobalVariable.lcdIP]))
		System.out.println(response.responseBodyContent)
		JsonSlurper slurper = new JsonSlurper()
		def parsedJson = (ArrayList<JsonObject>)slurper.parseText(response.responseBodyContent)
		String valAddr
		String moniker
		Map map = new HashMap()
		for(int i = 0; i< parsedJson.size(); i++) {
			valAddr = parsedJson[i].get("operator_address")
			moniker = parsedJson[i].get("description").get("moniker")
			map.put(moniker, valAddr)
		}
		return map
	}
}
