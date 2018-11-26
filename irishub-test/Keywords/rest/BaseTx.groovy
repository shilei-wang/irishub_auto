package rest

import static com.kms.katalon.core.checkpoint.CheckpointFactory.findCheckpoint
import static com.kms.katalon.core.testcase.TestCaseFactory.findTestCase
import static com.kms.katalon.core.testdata.TestDataFactory.findTestData
import static com.kms.katalon.core.testobject.ObjectRepository.findTestObject

import com.google.gson.Gson
import com.google.gson.JsonObject
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

public class BaseTx {

	@Keyword
	public static String[] baseTxProduce(String name, String password) {
		Map map = new HashMap()
		TestData data = findTestData('base/tx')
		data.getColumnNames().each{columnName ->
			if (!utils.StringUtils.isNullOrEmpty(data.getValue(columnName, 1))) {
				map.put(columnName,data.getValue(columnName, 1))
			}
		}
		String address = GetAddressByKey.getAddressByKey(name)
		map.put('name',name)
		map.put('password',password)
		map.put('chain_id',GlobalVariable.chainId)
		System.out.println(address)
		ResponseObject response = WS.sendRequest(findTestObject('rest/keys/ICS1_get_auth_accounts_address', [ ('address') : address, ('lcdIP') : GlobalVariable.lcdIP]))
		JsonSlurper slurper = new JsonSlurper()
		Map parsedJson = slurper.parseText(response.responseBodyContent)
		System.out.println(parsedJson)
		String sequence = parsedJson.get("sequence")
		String account_number = parsedJson.get("account_number")
		System.out.println(sequence)
		map.put("sequence",sequence)
		map.put("account_number",account_number)
		String jsonString = new Gson().toJson(map)
		System.out.println(jsonString)
		String[] returnString = new String[2]
		returnString[0] = jsonString
		returnString[1] = address
		return returnString
	}
}
