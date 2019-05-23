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
import com.kms.katalon.core.testobject.RequestObject
import com.kms.katalon.core.testobject.TestObjectProperty
import com.kms.katalon.core.testobject.ConditionType
import utils.CmdUtils
import rest.BaseTx
import rest.GetAddressByKey
import rest.StakeUtils
import java.util.ArrayList

public class BankUtils {

	@Keyword
	public static ResponseObject getAccountBalances(String address) {
		ResponseObject response = WS.sendRequest(findTestObject('rest/transactions/ICS20_get_bank_balances_address', [ ('address') : address, ('lcdIP') : GlobalVariable.lcdIP]))
		System.out.println(response.responseBodyContent)
		return response
	}

	@Keyword
	public static ResponseObject coinType(String coin) {
		ResponseObject response = WS.sendRequest(findTestObject('rest/transactions/ICS20_get_bank_coin_cointype', [ ('coin-type') : coin, ('lcdIP') : GlobalVariable.lcdIP]))
		System.out.println(response.responseBodyContent)
		return response
	}

	@Keyword
	public static Map setUnits(String msg){
		Map<String, Integer> units_map = new HashMap<String, Integer>()
		JsonArray units_array = CmdUtils.Parse(msg).get("units").getAsJsonArray()
		WS.verifyEqual(units_array.size(), 7)
		for (int i = 0; i < units_array.size() ; i++) {
			JsonObject item = units_array.get(i).getAsJsonObject()
			units_map.put(item.get("denom").getAsString(), Integer.parseInt(item.get("decimal").getAsString()))
		}
		return units_map
	}

	@Keyword
	public static ResponseObject transactionSign(String name, String password, JsonObject txMsg){
		JsonSlurper slurper = new JsonSlurper()
		Map baseTxInfo = (Map)slurper.parseText(BaseTx.baseTxProduce(name, password)[0])
		JsonObject txBody = new JsonObject()
		txBody.add("tx", txMsg)
		txBody.addProperty("name", name)
		txBody.addProperty("password", password)
		txBody.addProperty("chain_id", GlobalVariable.chainId)
		txBody.addProperty("account_number", baseTxInfo.get("account_number"))
		txBody.addProperty("append_sig", true)
		txBody.addProperty("sequence", baseTxInfo.get("sequence"))
		String jsonString = new Gson().toJson(txBody)
		System.out.println(jsonString)
		ResponseObject response = WS.sendRequest(findTestObject('rest/transactions/ICS20_post_tx_sign', [ ('HttpBody') : jsonString, ('lcdIP') : GlobalVariable.lcdIP]))
		println response.responseBodyContent
		return response
	}

	@Keyword
	public static JsonObject getTxValue(String rawTx){
		JsonParser jsonParser = new JsonParser()
		JsonObject object = (JsonObject)jsonParser.parse(rawTx)
		return object.get("value").getAsJsonObject()
	}

	@Keyword
	public static ResponseObject transactionBroadcast(JsonObject txMsg){
		JsonObject txBody = new JsonObject()
		txBody.add("tx", txMsg)
		String jsonString = new Gson().toJson(txBody)
		System.out.println(jsonString)
		ResponseObject response = WS.sendRequest(findTestObject('rest/transactions/ICS20_post_tx_broadcast', [ ('HttpBody') : jsonString, ('lcdIP') : GlobalVariable.lcdIP]))
		println response.responseBodyContent
		return response
	}

	@Keyword
	public static ResponseObject transactionBroadcastWithParams(JsonObject txMsg, String async, String commit, String simulate){
		JsonObject txBody = new JsonObject()
		txBody.add("tx", txMsg)
		String jsonString = new Gson().toJson(txBody)
		System.out.println(jsonString)

		RequestObject reqObj = findTestObject('rest/transactions/ICS20_post_tx_broadcast', [ ('HttpBody') : jsonString, ('lcdIP') : GlobalVariable.lcdIP])
		List params = new ArrayList<>()
		if(async.equals("true")) {
			params.add(new TestObjectProperty("async", ConditionType.EQUALS, async))
		} else if(async.equals("false")) {
			params.add(new TestObjectProperty("async", ConditionType.EQUALS, async))
		}
		if(commit.equals("true")) {
			params.add(new TestObjectProperty("commit", ConditionType.EQUALS, commit))
		} else if(commit.equals("false")) {
			params.add(new TestObjectProperty("commit", ConditionType.EQUALS, commit))
		}
		if(simulate.equals("true")) {
			params.add(new TestObjectProperty("simulate", ConditionType.EQUALS, simulate))
		} else if(simulate.equals("false")) {
			params.add(new TestObjectProperty("simulate", ConditionType.EQUALS, simulate))
		}
		reqObj.setRestParameters(params)
		List paramsGet = reqObj.getRestParameters()
		for (int i; i < paramsGet.size(); i++) {
			TestObjectProperty a = paramsGet.get(i)
			println a.getValue()
		}
		ResponseObject response = WS.sendRequest(reqObj)
		System.out.println(response.responseBodyContent)
		return response
	}
}
