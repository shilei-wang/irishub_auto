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
import com.google.gson.Gson
import com.google.gson.JsonObject
import com.google.gson.JsonArray
import com.google.gson.JsonParser
import groovy.json.JsonSlurper
import com.kms.katalon.core.testobject.ResponseObject
import utils.CmdUtils
import rest.BaseTx
import rest.GetAddressByKey

public class AccountUtils {

	@Keyword
	public static String createNewAccount(String amount) {
		JsonObject HttpBody = new JsonObject()
		String nameNew = "user_"+CmdUtils.generateRandomID()
		HttpBody.addProperty("name",nameNew)
		HttpBody.addProperty("password","1234567890")
		HttpBody.addProperty("seed",generateSeeds())
		String jsonString = new Gson().toJson(HttpBody)
		System.out.println(jsonString)
		ResponseObject response = WS.sendRequest(findTestObject('rest/keys/ICS1_post_keys', [ ('HttpBody') : jsonString, ('lcdIP') : GlobalVariable.lcdIP]))
		System.out.println(response.responseBodyContent)
		WS.verifyEqual(response.responseBodyContent.contains(nameNew), true)

		if (amount != "0iris") {
			TestData faucet = findTestData('base/faucet')
			String name = faucet.getValue('name', 1)
			String password = faucet.getValue('password', 1)
			String[] BaseTxInfo = BaseTx.baseTxProduce(name, password)
			String address = GetAddressByKey.getAddressByKey(nameNew)
			BaseTx.transfer(BaseTxInfo[0], BaseTxInfo[1], address, amount, "0.04iris")
			sleep(5000)
		}
		return nameNew
	}

	@Keyword
	public static String generateSeeds() {
		ResponseObject response = WS.sendRequest(findTestObject('rest/keys/ICS1_get_keys_seed', [ ('lcdIP') : GlobalVariable.lcdIP]))
		WS.verifyResponseStatusCode(response, 200)
		JsonParser jsonParser = new JsonParser()
		JsonObject object = (JsonObject)jsonParser.parse(response.responseBodyContent)
		return object.get("seed").getAsString()
	}

	@Keyword
	public static int getLastestBlock() {
		ResponseObject response = WS.sendRequest(findTestObject('rest/tendermint/ICS0_get_blocks_latest', [ ('lcdIP') : GlobalVariable.lcdIP]))
		WS.verifyResponseStatusCode(response, 200)
		JsonParser jsonParser = new JsonParser()
		JsonObject object = (JsonObject)jsonParser.parse(response.responseBodyContent)
		//JsonSlurper slurper = new JsonSlurper()
		//JsonObject object = (JsonObject)slurper.parseText(response.responseBodyContent)
		//object = object.get("header").get("height")
		object = object.get("block_meta").getAsJsonObject()
		object = object.get("header").getAsJsonObject()
		return object.get("height").getAsInt()
	}
	
	@Keyword
	public static void waitUntilNextBlock(){
		int next_height = getLastestBlock() + 1
		int process
		while(true){
			process = getLastestBlock()
			if (process >= next_height){
				break
			}
			sleep(500)
		}
	}
}
