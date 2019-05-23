package rest

import static com.kms.katalon.core.checkpoint.CheckpointFactory.findCheckpoint
import static com.kms.katalon.core.testcase.TestCaseFactory.findTestCase
import static com.kms.katalon.core.testdata.TestDataFactory.findTestData
import static com.kms.katalon.core.testobject.ObjectRepository.findTestObject

import com.google.gson.Gson
import com.google.gson.GsonBuilder
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
import com.kms.katalon.core.testobject.RequestObject
import com.kms.katalon.core.testobject.TestObject
import com.kms.katalon.core.testobject.TestObjectProperty
import com.kms.katalon.core.testobject.ConditionType
import com.kms.katalon.core.webservice.keyword.WSBuiltInKeywords as WS
import com.kms.katalon.core.webui.keyword.WebUiBuiltInKeywords as WebUI

import internal.GlobalVariable
import rest.BaseTx
import utils.StringUtils as StringUtils
import rest.AccountUtils


public class GovUtils {

	@Keyword
	public static ResponseObject proposalsQuery(String voter, String depositer, String status, String limit){
		//ResponseObject response = WS.sendRequest(findTestObject('rest/governance/ICS22_get_gov_proposals', [('lcdIP') : GlobalVariable.lcdIP]))
		//System.out.println(response.responseBodyContent)
		RequestObject reqObj = findTestObject('rest/governance/ICS22_get_gov_proposals', [('lcdIP') : GlobalVariable.lcdIP])
		List params = new ArrayList<>()
		if(!voter.equals("nothing")) {
			params.add(new TestObjectProperty("voter", ConditionType.EQUALS, voter))
		}
		if(!limit.equals("nothing")) {
			params.add(new TestObjectProperty("limit", ConditionType.EQUALS, limit))
		}
		if(!depositer.equals("nothing")) {
			params.add(new TestObjectProperty("depositer", ConditionType.EQUALS, depositer))
		}
		if(!status.equals("nothing")) {
			params.add(new TestObjectProperty("status", ConditionType.EQUALS, status))
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

	@Keyword
	public static ResponseObject proposalsQueryByID(String proposalId){
		ResponseObject response = WS.sendRequest(findTestObject('rest/governance/ICS22_get_gov_proposals_proposalId', [ ('proposalId') : proposalId,('lcdIP') : GlobalVariable.lcdIP]))
		System.out.println(response.responseBodyContent)
		return response
	}

	@Keyword
	public static String getProposalId(String response){
		JsonParser jsonParser = new JsonParser()
		JsonObject object = (JsonObject)jsonParser.parse(response)
		JsonArray tags = object.get("deliver_tx").getAsJsonObject().get("tags").getAsJsonArray()
		JsonObject tag
		String decodeKey
		String proposalId
		for (int i = 0; i < tags.size(); i++){
			tag = tags.get(i).getAsJsonObject()
			decodeKey = BaseEncode.base64Decode(tag.get("key").getAsString())
			if (decodeKey.equals("proposal-id")) {
				proposalId = BaseEncode.base64Decode(tag.get("value").getAsString())
				break
			}
		}
		return proposalId
	}

	@Keyword
	public static String getInfoFromSubmitProposalTag(String response, String typeName){
		JsonParser jsonParser = new JsonParser()
		JsonObject object = (JsonObject)jsonParser.parse(response)
		JsonArray tags = object.get("deliver_tx").getAsJsonObject().get("tags").getAsJsonArray()
		JsonObject tag
		String decodeKey
		String info
		for (int i = 0; i < tags.size(); i++){
			tag = tags.get(i).getAsJsonObject()
			decodeKey = BaseEncode.base64Decode(tag.get("key").getAsString())
			if (decodeKey.equals(typeName)) {
				info = BaseEncode.base64Decode(tag.get("value").getAsString())
				break
			}
		}
		return info
	}
	/*
	@Keyword
	public static String getProposalStatusFromResponse(String response){
		JsonParser jsonParser = new JsonParser()
		JsonObject object = (JsonObject)jsonParser.parse(response)
		String status= object.get("proposal_status").getAsString()
		println status
		return status
	}
	*/
	@Keyword
	public static String getProposalStatusByProposalId(String proposalId){
		ResponseObject response = GovUtils.proposalsQueryByID(proposalId)
		JsonParser jsonParser = new JsonParser()
		JsonObject object = (JsonObject)jsonParser.parse(response.responseBodyContent)
		String status= object.get("value").getAsJsonObject().get("BasicProposal").getAsJsonObject().get("proposal_status").getAsString()
		println status
		return status
	}

	@Keyword
	public static ResponseObject proposalSubmit(String account, String password, String title, String description, String proposal_type, String initial_deposit, String percent, String usage, String param){
		String[] BaseTxInfo = BaseTx.baseTxProduce(account, password)
		JsonParser jsonParser = new JsonParser()
		JsonObject object = (JsonObject)jsonParser.parse(BaseTxInfo[0])
		JsonObject HttpBodyJson = new JsonObject()
		HttpBodyJson.add("base_tx", object)
		HttpBodyJson.addProperty("title", title)
		HttpBodyJson.addProperty("proposal_type", proposal_type)
		HttpBodyJson.addProperty("proposer", BaseTxInfo[1])
		HttpBodyJson.addProperty("initial_deposit", initial_deposit)
		HttpBodyJson.addProperty("description", description)
		if (percent != "nothing"){
			HttpBodyJson.addProperty("percent", percent)
		}
		if (usage != "nothing"){
			HttpBodyJson.addProperty("usage", usage)
		}
		if (param != "nothing"){
			JsonArray params = new JsonArray()
			JsonObject paramObject = SplitParamString(param)
			params.add(paramObject)
			HttpBodyJson.add("param", params)
		}
		
		String jsonString = new GsonBuilder().disableHtmlEscaping().create().toJson(HttpBodyJson)
		println jsonString
		ResponseObject response = WS.sendRequest(findTestObject('rest/governance/ICS22_post_gov_proposals', [ ('HttpBody') : jsonString,('lcdIP') : GlobalVariable.lcdIP]))
		System.out.println(response.responseBodyContent)
		AccountUtils.waitUntilNextBlock()
		return response
	}
	
	@Keyword
	public static ResponseObject proposalBurnSubmitEx(String account, String password, String initial_deposit){
		String[] BaseTxInfo = BaseTx.baseTxProduce(account, password)
		JsonParser jsonParser = new JsonParser()
		JsonObject object = (JsonObject)jsonParser.parse(BaseTxInfo[0])
		JsonObject HttpBodyJson = new JsonObject()
		HttpBodyJson.add("base_tx", object)
		HttpBodyJson.addProperty("title", "t")
		HttpBodyJson.addProperty("proposal_type", "TxTaxUsage")
		HttpBodyJson.addProperty("proposer", BaseTxInfo[1])
		HttpBodyJson.addProperty("initial_deposit", initial_deposit)
		HttpBodyJson.addProperty("description", "t")
		HttpBodyJson.addProperty("percent", "0.0000000001")
		HttpBodyJson.addProperty("usage", "Burn")
		String jsonString = new GsonBuilder().disableHtmlEscaping().create().toJson(HttpBodyJson)
		println jsonString
		ResponseObject response = WS.sendRequest(findTestObject('rest/governance/ICS22_post_gov_proposals', [ ('HttpBody') : jsonString,('lcdIP') : GlobalVariable.lcdIP]))
		System.out.println(response.responseBodyContent)
		AccountUtils.waitUntilNextBlock()
		return response
	}
	
	@Keyword
	public static JsonObject SplitParamString(String param) {
		JsonObject paramObject = new JsonObject()
		String[] paramArray = param.split("=")
		String[] temp = paramArray[0].split("/")
		paramObject.addProperty("subspace", temp[0])
		paramObject.addProperty("key", temp[1])
		paramObject.addProperty("value", paramArray[1])
		return paramObject
	}
/*
	@Keyword
	public static ResponseObject UpdateProposalSubmit(String account, String password, String title, String description, String proposal_type, String initial_deposit, String version, String software, String switchHeight){
		String[] BaseTxInfo = BaseTx.baseTxProduce(account, password)
		JsonParser jsonParser = new JsonParser()
		JsonObject object = (JsonObject)jsonParser.parse(BaseTxInfo[0])
		JsonObject HttpBodyJson = new JsonObject()
		HttpBodyJson.add("base_tx", object)
		HttpBodyJson.addProperty("title", title)
		HttpBodyJson.addProperty("proposal_type", proposal_type)
		HttpBodyJson.addProperty("proposer", BaseTxInfo[1])
		HttpBodyJson.addProperty("initial_deposit", initial_deposit)
		HttpBodyJson.addProperty("description", description)
		if (version != "nothing"){
			HttpBodyJson.addProperty("version", version)
		}
		if (software != "nothing"){
			HttpBodyJson.addProperty("software", software)
		}
		if (switchHeight != "nothing"){
			HttpBodyJson.addProperty("switch-height", switchHeight)
		}
		String jsonString = new GsonBuilder().disableHtmlEscaping().create().toJson(HttpBodyJson)
		println jsonString
		ResponseObject response = WS.sendRequest(findTestObject('rest/governance/ICS22_post_gov_proposals', [ ('HttpBody') : jsonString,('lcdIP') : GlobalVariable.lcdIP]))
		System.out.println(response.responseBodyContent)
		AccountUtils.waitUntilNextBlock()
		return response
	}
*/
	@Keyword
	public static ResponseObject depositSubmit(String account, String password, String proposalId, String amount){
		String[] BaseTxInfo = BaseTx.baseTxProduce(account, password)
		JsonParser jsonParser = new JsonParser()
		JsonObject object = (JsonObject)jsonParser.parse(BaseTxInfo[0])
		JsonObject HttpBodyJson = new JsonObject()
		HttpBodyJson.add("base_tx", object)
		HttpBodyJson.addProperty("depositor", BaseTxInfo[1])
		HttpBodyJson.addProperty("amount", amount)
		String jsonString = new Gson().toJson(HttpBodyJson)
		println jsonString
		ResponseObject response = WS.sendRequest(findTestObject('rest/governance/ICS22_post_gov_proposals_proposalId_deposits', [ ('HttpBody') : jsonString, ("proposalId") : proposalId,('lcdIP') : GlobalVariable.lcdIP]))
		System.out.println(response.responseBodyContent)
		AccountUtils.waitUntilNextBlock()
		return response
	}

	@Keyword
	public static ResponseObject depositsQueryById(String proposalId){
		ResponseObject response = WS.sendRequest(findTestObject('rest/governance/ICS22_get_gov_proposals_proposalId_deposits', [('proposalId') : proposalId,('lcdIP') : GlobalVariable.lcdIP]))
		System.out.println(response.responseBodyContent)
		return response
	}

	@Keyword
	public static ResponseObject depositQueryByAddrId(String proposalId, String depositer){
		ResponseObject response = WS.sendRequest(findTestObject('rest/governance/ICS22_get_gov_proposals_proposalId_deposits_depositer', [('proposalId') : proposalId, ('depositer') : depositer, ('lcdIP') : GlobalVariable.lcdIP]))
		System.out.println(response.responseBodyContent)
		return response
	}

	@Keyword
	public static ResponseObject proposalVoteSubmit(String account, String password, String proposalId, String option){
		String[] BaseTxInfo = BaseTx.baseTxProduce(account, password)
		JsonParser jsonParser = new JsonParser()
		JsonObject object = (JsonObject)jsonParser.parse(BaseTxInfo[0])
		JsonObject HttpBodyJson = new JsonObject()
		HttpBodyJson.add("base_tx", object)
		HttpBodyJson.addProperty("voter", BaseTxInfo[1])
		HttpBodyJson.addProperty("proposalId", proposalId)
		HttpBodyJson.addProperty("option", option)
		String jsonString = new Gson().toJson(HttpBodyJson)
		println jsonString
		ResponseObject response = WS.sendRequest(findTestObject('rest/governance/ICS22_post_gov_proposals_proposalId_votes', [ ('HttpBody') : jsonString, ('proposalId'): proposalId , ('lcdIP') : GlobalVariable.lcdIP]))
		System.out.println(response.responseBodyContent)
		AccountUtils.waitUntilNextBlock()
		return response
	}

	@Keyword
	public static ResponseObject votesQueryById(String proposalId){
		ResponseObject response = WS.sendRequest(findTestObject('rest/governance/ICS22_get_gov_proposals_proposalId_votes', [('proposalId') : proposalId,('lcdIP') : GlobalVariable.lcdIP]))
		System.out.println(response.responseBodyContent)
		return response
	}

	@Keyword
	public static ResponseObject voteQueryByAddrId(String proposalId, String voter){
		ResponseObject response = WS.sendRequest(findTestObject('rest/governance/ICS22_get_gov_proposals_proposalId_votes_voter', [('proposalId') : proposalId, ('voter') : voter, ('lcdIP') : GlobalVariable.lcdIP]))
		System.out.println(response.responseBodyContent)
		return response
	}

	@Keyword
	public static ResponseObject tallyResultQueryById(String proposalId){
		ResponseObject response = WS.sendRequest(findTestObject('rest/governance/ICS22_get_gov_proposals_proposalId_tally', [('proposalId') : proposalId,('lcdIP') : GlobalVariable.lcdIP]))
		System.out.println(response.responseBodyContent)
		return response
	}

	@Keyword
	public static void checkPower(String[] votes_result, JsonObject re, String type){
		Double power = Double.valueOf(re.get(type).getAsString())
		if (StringUtils.arrayContains(votes_result, type)){
			WS.verifyEqual((power>0), true)
		} else {
			WS.verifyEqual((power==0), true)
		}
	}
}
