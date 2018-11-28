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
		System.out.println(response.responseBodyContent)
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

	@Keyword
	public static String transfer(String BaseTx, String senderAddr, String withdrawAddress, String amount, String fee) {
		JsonParser jsonParser = new JsonParser()
		JsonObject object = (JsonObject)jsonParser.parse(BaseTx)
		if (fee.contains("iris")){
			object.remove("fee")
			object.addProperty("fee",fee)
		}
		String BaseTxNew = new Gson().toJson(object)
		JsonObject HttpBody = new JsonObject()
		HttpBody.add("base_tx",object);
		HttpBody.addProperty("sender",senderAddr)
		HttpBody.addProperty("amount",amount)
		String jsonString = new Gson().toJson(HttpBody)
		System.out.println(jsonString)
		ResponseObject response = WS.sendRequest(findTestObject('rest/transactions/ICS20_post_bank_accounts_address_transfers', [ ('HttpBody') : jsonString, ('address') : withdrawAddress, ('lcdIP') : GlobalVariable.lcdIP]))
		System.out.println(response.responseBodyContent)
		WS.verifyEqual(StringUtils.stringContains(response.responseBodyContent,"hash"), true)
		return BaseTxNew
	}


	@Keyword
	public static Double getAccountBalance(String address) {

		ResponseObject response = WS.sendRequest(findTestObject('rest/keys/ICS1_get_auth_accounts_address', [ ('address') : address, ('lcdIP') : GlobalVariable.lcdIP]))
		System.out.println(response.responseBodyContent)
		WS.verifyEqual(StringUtils.stringContains(response.responseBodyContent,"coins"), true)
		JsonParser jsonParser = new JsonParser();
		JsonObject object = (JsonObject)jsonParser.parse(response.responseBodyContent);
		JsonArray coins = object.getAsJsonArray("coins")
		String iriscoins = coins.get(0).getAsString()
		String re = iriscoins.replace("iris", "")
		return Double.valueOf(re)
	}

	//ifIsValAddr 指是否加上 后面的true或者false
	//ifValAddr 指是否加上后面的valAddr

	@Keyword
	public static String withdrawRewardBodyProduce(String delegatorAddr, String BaseTx, Boolean ifIsValAddr, Boolean isVal, Boolean ifValAddr, String valAddress) {
		JsonParser jsonParser = new JsonParser()
		JsonObject object = (JsonObject)jsonParser.parse(BaseTx)
		JsonObject HttpBodyJson = new JsonObject()
		HttpBodyJson.add("base_tx", object)
		if (ifIsValAddr == true) {
			HttpBodyJson.addProperty("is_validator", isVal)
		}
		if (ifValAddr == true) {
			HttpBodyJson.addProperty("validator_address", valAddress)
		}
		String jsonString = new Gson().toJson(HttpBodyJson)
		return jsonString
	}

	@Keyword
	public static String withdrawRewardBodyProduceisVal(String delegatorAddr, String BaseTx, Boolean ifIsValAddr, String isVal, Boolean ifValAddr, String valAddress) {
		JsonParser jsonParser = new JsonParser()
		JsonObject object = (JsonObject)jsonParser.parse(BaseTx)
		JsonObject HttpBodyJson = new JsonObject()
		HttpBodyJson.add("base_tx", object)
		if (ifIsValAddr == true) {
			HttpBodyJson.addProperty("is_validator", isVal)
		}
		if (ifValAddr == true) {
			HttpBodyJson.addProperty("validator_address", valAddress)
		}
		String jsonString = new Gson().toJson(HttpBodyJson)
		return jsonString
	}

	@Keyword
	public static String HttpBodySwitch(String BaseTx, String address, int index){
		TestData data = findTestData('distribution/withdraw-rewards/IRISHUB-811')
		switch (index){
			case 0:
			//1）指定--only-from-validator为一个不存在委托关系的fva账户，执行withdraw-rewards。（不指定--is-validator）
				return withdrawRewardBodyProduce(address, BaseTx, false, false, true, data.getValue('data', 1))
				break
			case 1:
			//2）指定--only-from-validator为一个faa账户，执行withdraw-rewards。（不指定--is-validator）
				return withdrawRewardBodyProduce(address, BaseTx, false, false, true, data.getValue('data', 2))
				break
			case 2:
			//3） 指定一个非validator账户且--is-validator设置为true，执行withdraw-rewards。  （不指定--only-from-validator）
				String name = AccountUtils.createNewAccount("1iris")
				String[] BaseTxInfo = baseTxProduce(name, "1234567890")
				String HttpBody = withdrawRewardBodyProduce(BaseTxInfo[1], BaseTxInfo[0], true, true, false, "")
				ResponseObject response =  WS.sendRequest(findTestObject('rest/distribution/ICS24_post_distribution_delegatorAddr_withdrawReward', [ ('delegatorAddr') : BaseTxInfo[1], ('HttpBody') : HttpBody, ('lcdIP') : GlobalVariable.lcdIP]))
				System.out.println(response.responseBodyContent)
				WS.verifyEqual(StringUtils.stringContains(response.responseBodyContent,data.getValue('rest_result', 3)), true)
				break
			case 3:
			//4）指定--is-validator为空，执行withdraw-rewards。  （不指定--only-from-validator）
				return withdrawRewardBodyProduceisVal(address, BaseTx, true, "", false, "")
				break
			case 4:
			//5）指定--is-validator为test，执行withdraw-rewards。  （不指定--only-from-validator）
				return withdrawRewardBodyProduceisVal(address, BaseTx, true, "test", false, "")
				break
			case 5:
			//6）同时指定--is-validator指定为true和--only-from-validator
				return withdrawRewardBodyProduce(address, BaseTx, true, true, true, data.getValue('data', 6))
				break
		}

		//user = CmdUtils.createNewAccount(u.name,"1iris")
		//cmdArray[2] = " --is-validator="+u.td.getValue('data', 3)+" --from="+user

	}

}
