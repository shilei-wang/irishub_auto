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
import rest.StakeUtils
import java.util.ArrayList

public class AccountUtils {
	
	@Keyword
	public static String getKeysList(){
		ResponseObject response = WS.sendRequest(findTestObject('rest/keys/ICS1_get_keys_list', [ ('lcdIP') : GlobalVariable.lcdIP]))
		System.out.println(response.responseBodyContent)
		JsonSlurper slurper = new JsonSlurper()
		def parsedJson = (ArrayList<JsonObject>)slurper.parseText(response.responseBodyContent)
		String keyName
		String keyAddress
		Map map = new HashMap()
		for(int i = 0; i< parsedJson.size(); i++) {
			keyName = parsedJson[i].get("name")
			keyAddress = parsedJson[i].get("address")
			map.put(keyName, keyAddress)
		}
		return map
	}
	
	@Keyword
	public static String getAddrByName(String name, String property){
		if((property == "acc") || (property == "val") || (property == "cons")) {
			ResponseObject response = WS.sendRequest(findTestObject('rest/keys/ICS1_get_keys_name', [ ('name') : name, ('property'): property, ('lcdIP') : GlobalVariable.lcdIP]))
			JsonSlurper slurper = new JsonSlurper()
			Map parsedJson = slurper.parseText(response.responseBodyContent)
			String addresss = parsedJson.get("address")
			return addresss
		} else {
			return ""
		}
	}

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
			waitUntilNextBlock()
		}
		return nameNew
	}
	
	@Keyword
	public static String addNewKey(String nameNew, String password) {
		JsonObject HttpBody = new JsonObject()
		HttpBody.addProperty("name",nameNew)
		HttpBody.addProperty("password", password)
		HttpBody.addProperty("seed",generateSeeds())
		String jsonString = new Gson().toJson(HttpBody)
		System.out.println(jsonString)
		ResponseObject response = WS.sendRequest(findTestObject('rest/keys/ICS1_post_keys', [ ('HttpBody') : jsonString, ('lcdIP') : GlobalVariable.lcdIP]))
		System.out.println(response.responseBodyContent)
		WS.verifyEqual(response.responseBodyContent.contains(nameNew), true)
		return nameNew
	}
	

	@Keyword
	public static String creNewAccBuildTwoDelegation(String amount) {
		String newAccount = createNewAccount(amount)
		TestData faucet = findTestData('base/faucet')
		String name1 = faucet.getValue('name', 1)
		String name2 = faucet.getValue('name', 2)
		String password = faucet.getValue('password', 1)
		String[] BaseTxInfo
		if(amount.contains("iris")) {
			int amountDele = Integer.parseInt(amount.replace("iris", ""))/2
			amount = amountDele.toString() + "iris"
		}
		Vector validatorList = StakeUtils.getValidatorAddrList()
		println validatorList.size()
		if(validatorList.size() < 2) {
			System.out.println("only one validator")
		}
		else{
			Map validatorMap = StakeUtils.getValidatorList()
			BaseTxInfo = BaseTx.baseTxProduce(newAccount, password)
			String HttpBody = StakeUtils.buildDelegation(BaseTxInfo[0], validatorMap.get(name1), amount)
			ResponseObject response = WS.sendRequest(findTestObject('rest/stake/delegators/ICS21_post_stake_delegators_delegatorAddr_delegate', [ ('delegatorAddr') : BaseTxInfo[1], ('HttpBody') : HttpBody,('lcdIP') : GlobalVariable.lcdIP]))
			System.out.println(response.responseBodyContent)
			waitUntilNextBlock()
			BaseTxInfo = BaseTx.baseTxProduce(newAccount, password)
			HttpBody = StakeUtils.buildDelegation(BaseTxInfo[0], validatorMap.get(name2), amount)
			response = WS.sendRequest(findTestObject('rest/stake/delegators/ICS21_post_stake_delegators_delegatorAddr_delegate', [ ('delegatorAddr') : BaseTxInfo[1], ('HttpBody') : HttpBody,('lcdIP') : GlobalVariable.lcdIP]))
			System.out.println(response.responseBodyContent)
			waitUntilNextBlock()
		}
		return newAccount
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
		int process = 0
		while(true){
			process = getLastestBlock()
			if (process >= next_height){
				break
			}
			sleep(500)
		}
	}

	@Keyword
	public static void waitUntilSeveralBlock(int num){
		int next_height = getLastestBlock() + num
		int process = 0
		while(true){
			process = getLastestBlock()
			if (process >= next_height){
				break
			}
			sleep(500)
		}
	}
}
