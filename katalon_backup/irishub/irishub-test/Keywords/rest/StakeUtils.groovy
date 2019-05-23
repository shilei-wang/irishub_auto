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
import rest.BaseTx
import utils.StringUtils as StringUtils
import rest.AccountUtils
//import java.util.ArrayList

public class StakeUtils {

	@Keyword
	public static Map getValidatorList() {
		TestData faucet = findTestData('base/faucet')
		String name1 = faucet.getValue('name', 1)
		String name2 = faucet.getValue('name', 2)
		String password = faucet.getValue('password', 1)
		Map map = new HashMap()
		map.put(name1, AccountUtils.getAddrByName(name1, "val"))
		map.put(name2, AccountUtils.getAddrByName(name2, "val"))
		return map
	}

	@Keyword
	public static ResponseObject getValidatorInfoByValAddr(String validatorAddr) {
		ResponseObject response = WS.sendRequest(findTestObject('rest/stake/validators/ICS21_get_stake_validators_validatorAddr', [("validatorAddr") : validatorAddr, ('lcdIP') : GlobalVariable.lcdIP]))
		System.out.println(response.responseBodyContent)
		return response
	}

	@Keyword
	public static Vector getValidatorAddrList() {
		ResponseObject response = WS.sendRequest(findTestObject('rest/stake/validators/ICS21_get_stake_validators', [ ('lcdIP') : GlobalVariable.lcdIP]))
		System.out.println(response.responseBodyContent)
		JsonSlurper slurper = new JsonSlurper()
		def parsedJson = (ArrayList<JsonObject>)slurper.parseText(response.responseBodyContent)
		Vector valAddr = new Vector()
		if(parsedJson.size() < 2) {
			System.out.println("only one validator")
		}
		for(int i = 0; i< parsedJson.size(); i++) {
			valAddr.add(parsedJson[i].get("operator_address"))
		}
		return valAddr
	}

	/*	@Keyword
	 public static String getAnotherValidatorAddr(String name) {
	 ResponseObject response = WS.sendRequest(findTestObject('rest/stake/validators/ICS21_get_stake_validators', [ ('lcdIP') : GlobalVariable.lcdIP]))
	 System.out.println(response.responseBodyContent)
	 JsonSlurper slurper = new JsonSlurper()
	 def parsedJson = (ArrayList<JsonObject>)slurper.parseText(response.responseBodyContent)
	 String valAddr
	 String moniker
	 if(parsedJson.size() <= 1) {
	 System.out.println("only one Validator'")
	 }
	 for(int i = 0; i< 2; i++) {
	 valAddr = parsedJson[i].get("operator_address")
	 moniker = parsedJson[i].get("description").get("moniker")
	 if (moniker != name){
	 break
	 }
	 }
	 return valAddr
	 }
	 */
	@Keyword
	public static String buildDelegation(String BaseTx, String valAddr, String amount) {
		JsonParser jsonParser = new JsonParser()
		JsonObject object = (JsonObject)jsonParser.parse(BaseTx)
		JsonObject HttpBodyJson = new JsonObject()
		HttpBodyJson.add("base_tx", object)
		HttpBodyJson.add("delegate", delegationProduce(valAddr, amount))
		String jsonString = new Gson().toJson(HttpBodyJson)
		return jsonString
	}

	@Keyword
	public static JsonObject delegationProduce(String valAddr, String delegationAmount) {
		JsonObject delegationBody = new JsonObject()
		delegationBody.addProperty("validator_addr", valAddr)
		delegationBody.addProperty("delegation", delegationAmount)
		return delegationBody
	}

	@Keyword
	public static ResponseObject delegationSubmit(String account, String password, String valAddr, String amount){
		String[] BaseTxInfo = BaseTx.baseTxProduce(account, password)
		String HttpBody = buildDelegation(BaseTxInfo[0], valAddr, amount)
		ResponseObject response = WS.sendRequest(findTestObject('rest/stake/delegators/ICS21_post_stake_delegators_delegatorAddr_delegate', [ ('delegatorAddr') : BaseTxInfo[1], ('HttpBody') : HttpBody,('lcdIP') : GlobalVariable.lcdIP]))
		System.out.println(response.responseBodyContent)
		AccountUtils.waitUntilNextBlock()
		return response
	}

	@Keyword
	public static ResponseObject redelegationSubmit(String account, String password, String validator_src_addr, String validator_dst_addr, String shares){
		String[] BaseTxInfo = BaseTx.baseTxProduce(account, password)
		JsonParser jsonParser = new JsonParser()
		JsonObject object = (JsonObject)jsonParser.parse(BaseTxInfo[0])
		JsonObject redelegation = new JsonObject()
		redelegation.addProperty("validator_src_addr", validator_src_addr)
		redelegation.addProperty("validator_dst_addr", validator_dst_addr)
		redelegation.addProperty("shares", shares)
		JsonObject HttpBodyJson = new JsonObject()
		HttpBodyJson.add("base_tx", object)
		HttpBodyJson.add("redelegate", redelegation)
		String jsonString = new Gson().toJson(HttpBodyJson)
		println jsonString
		ResponseObject response = WS.sendRequest(findTestObject('rest/stake/delegators/ICS21_post_stake_delegators_delegatorAddr_redelegate', [ ('delegatorAddr') : BaseTxInfo[1], ('HttpBody') : jsonString,('lcdIP') : GlobalVariable.lcdIP]))
		System.out.println(response.responseBodyContent)
		AccountUtils.waitUntilNextBlock()
		return response
	}

	@Keyword
	public static ResponseObject unbondSubmit(String account, String password, String validator_addr, String shares){
		String[] BaseTxInfo = BaseTx.baseTxProduce(account, password)
		JsonParser jsonParser = new JsonParser()
		JsonObject object = (JsonObject)jsonParser.parse(BaseTxInfo[0])
		JsonObject unbond = new JsonObject()
		unbond.addProperty("validator_addr", validator_addr)
		unbond.addProperty("shares", shares)
		JsonObject HttpBodyJson = new JsonObject()
		HttpBodyJson.add("base_tx", object)
		HttpBodyJson.add("unbond", unbond)
		String jsonString = new Gson().toJson(HttpBodyJson)
		println jsonString
		ResponseObject response = WS.sendRequest(findTestObject('rest/stake/delegators/ICS21_post_stake_delegators_delegatorAddr_unbond', [ ('delegatorAddr') : BaseTxInfo[1], ('HttpBody') : jsonString,('lcdIP') : GlobalVariable.lcdIP]))
		System.out.println(response.responseBodyContent)
		AccountUtils.waitUntilNextBlock()
		return response
	}

	@Keyword
	public static ResponseObject delegationQueryByDelAddr(String delegatorAddr) {
		ResponseObject response = WS.sendRequest(findTestObject('rest/stake/delegators/ICS21_get_stake_delegators_delegatorAddr_delegations', [ ('delegatorAddr') : delegatorAddr, ('lcdIP') : GlobalVariable.lcdIP]))
		System.out.println(response.responseBodyContent)
		return response
	}

	@Keyword
	public static ResponseObject delegationQueryByValAddr(String validatorAddr) {
		ResponseObject response = WS.sendRequest(findTestObject('rest/stake/validators/ICS21_get_stake_validators_validatorAddr_delegations', [ ('validatorAddr') : validatorAddr, ('lcdIP') : GlobalVariable.lcdIP]))
		System.out.println(response.responseBodyContent)
		return response
	}

	@Keyword
	public static ResponseObject unbodingDelegationQueryByDelValAddr(String delegatorAddr, String validatorAddr) {
		ResponseObject response = WS.sendRequest(findTestObject('rest/stake/delegators/ICS21_get_stake_delegators_delegatorAddr_unbonding_delegations_validatorAddr', [ ('delegatorAddr') : delegatorAddr, ('validatorAddr') : validatorAddr, ('lcdIP') : GlobalVariable.lcdIP]))
		System.out.println(response.responseBodyContent)
		return response
	}

	@Keyword
	public static Double getExpectedBalanceFromUnbonding(String delegatorAddr, String validatorAddr){
		ResponseObject response = unbodingDelegationQueryByDelValAddr(delegatorAddr, validatorAddr)
		return  getExpectedBalanceFromUnbondingMsg(response.responseBodyContent)
	}

	@Keyword
	public static Double getExpectedBalanceFromUnbondingMsg(String msg){
		if (msg.contains("no unbonding-delegation")){
			return 0
		}
		JsonParser jsonParser = new JsonParser()
		JsonObject object = (JsonObject)jsonParser.parse(msg)

		return  Double.valueOf(object.get("balance").getAsString().replace("iris", ""))
	}

	@Keyword
	public static ResponseObject unbodingDelegationQueryByDel(String delegatorAddr) {
		ResponseObject response = WS.sendRequest(findTestObject('rest/stake/delegators/ICS21_get_stake_delegators_delegatorAddr_unbonding_delegations', [ ('delegatorAddr') : delegatorAddr, ('lcdIP') : GlobalVariable.lcdIP]))
		System.out.println(response.responseBodyContent)
		return response
	}

	@Keyword
	public static ResponseObject unbodingDelegationQueryByVal(String validatorAddr) {
		ResponseObject response = WS.sendRequest(findTestObject('rest/stake/validators/ICS21_get_stake_validators_validatorAddr_unbonding_delegations', [ ('validatorAddr') : validatorAddr, ('lcdIP') : GlobalVariable.lcdIP]))
		System.out.println(response.responseBodyContent)
		return response
	}

	@Keyword
	public static ResponseObject delegationQueryByDelValAddr(String delegatorAddr, String validatorAddr) {
		ResponseObject response = WS.sendRequest(findTestObject('rest/stake/delegators/ICS21_get_stake_delegators_delegatorAddr_delegations_validatorAddr', [ ('delegatorAddr') : delegatorAddr, ('validatorAddr') : validatorAddr, ('lcdIP') : GlobalVariable.lcdIP]))
		System.out.println(response.responseBodyContent)
		return response
	}

	@Keyword
	public static ResponseObject redelegationsQueryByDelAddr(String delegatorAddr) {
		ResponseObject response = WS.sendRequest(findTestObject('rest/stake/delegators/ICS21_get_stake_delegators_delegatorAddr_redelegations', [ ('delegatorAddr') : delegatorAddr, ('lcdIP') : GlobalVariable.lcdIP]))
		System.out.println(response.responseBodyContent)
		return response
	}

	@Keyword
	public static ResponseObject redelegationsQueryByValAddr(String validatorAddr) {
		ResponseObject response = WS.sendRequest(findTestObject('rest/stake/validators/ICS21_get_stake_validators_validatorAddr_redelegations', [ ('validatorAddr') : validatorAddr, ('lcdIP') : GlobalVariable.lcdIP]))
		System.out.println(response.responseBodyContent)
		return response
	}

	@Keyword
	public static Double getSharesFromDelegation(String msg){
		JsonParser jsonParser = new JsonParser()
		JsonObject object = (JsonObject)jsonParser.parse(msg)
		return  Double.valueOf(object.get("shares").getAsString())
	}

	@Keyword
	public static Double getSpecifiedDelegationShares(String delegatorAddr, String validatorAddr){
		ResponseObject response = delegationQueryByDelValAddr(delegatorAddr, validatorAddr)
		if (response.responseBodyContent.contains("no delegation")){
			return 0
		}
		return getSharesFromDelegation(response.responseBodyContent)
	}

	@Keyword
	public static int findItemIndexInDelegations(JsonArray array, String validator_addr){
		for (int i = 0; i < array.size() ; i++) {
			JsonObject item = array.get(i).getAsJsonObject()
			if (item.get("validator_addr").getAsString() == validator_addr){
				return i
			}
		}
		return -1
	}

	@Keyword
	public static int findItemIndexInDelegationsByDelAddr(JsonArray array, String delegator_addr){
		for (int i = 0; i < array.size() ; i++) {
			JsonObject item = array.get(i).getAsJsonObject()
			if (item.get("delegator_addr").getAsString() == delegator_addr){
				return i
			}
		}
		return -1
	}

	@Keyword
	public static ResponseObject stakingTxsQuery(String delegator_addr){
		ResponseObject response = WS.sendRequest(findTestObject('rest/stake/delegators/ICS21_get_stake_delegators_delegatorAddr_txs', [ ('delegatorAddr') : delegator_addr, ('lcdIP') : GlobalVariable.lcdIP]))
		System.out.println(response.responseBodyContent)
		return response
	}

}
