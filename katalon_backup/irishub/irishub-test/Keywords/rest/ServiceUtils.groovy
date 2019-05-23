package rest

import static com.kms.katalon.core.checkpoint.CheckpointFactory.findCheckpoint
import static com.kms.katalon.core.testcase.TestCaseFactory.findTestCase
import static com.kms.katalon.core.testdata.TestDataFactory.findTestData
import static com.kms.katalon.core.testobject.ObjectRepository.findTestObject

import org.junit.After

import com.google.gson.Gson
import com.google.gson.JsonObject
import com.google.gson.JsonArray
import com.google.gson.JsonParser
import com.google.gson.GsonBuilder
import groovy.json.JsonSlurper
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
import com.kms.katalon.core.testobject.ResponseObject

import rest.GetAddressByKey
import internal.GlobalVariable
import rest.AccountUtils
import utils.CmdUtils
import rest.BaseEncode

public class ServiceUtils {

	@Keyword
	public static String serviceDefine(String BaseTx, String authorAddr, String service_name, String service_description, String author_description, JsonArray tags, String idl_content){
		JsonParser jsonParser = new JsonParser()
		JsonObject object = (JsonObject)jsonParser.parse(BaseTx)
		JsonObject HttpBody = new JsonObject()
		HttpBody.add("base_tx",object)
		HttpBody.add("tags",tags)
		HttpBody.addProperty("author_addr",authorAddr)
		HttpBody.addProperty("service_name",service_name)
		HttpBody.addProperty("service_description",service_description)
		HttpBody.addProperty("author_description",author_description)
		HttpBody.addProperty("idl_content",idl_content)
		Gson gson = new GsonBuilder().disableHtmlEscaping().create();
		String jsonString = gson.toJson(HttpBody)
		System.out.println(jsonString)
		ResponseObject response = WS.sendRequest(findTestObject('rest/service/post_service_define', [ ('HttpBody') : jsonString, ('lcdIP') : GlobalVariable.lcdIP]))
		System.out.println(response.responseBodyContent)
		return response.responseBodyContent
	}


	@Keyword
	public static String serviceBind(String BaseTx, String provider, String service_name, String def_chain_id, String binding_type, JsonArray prices, String deposit, String avg_rsp_time, String usable_time){
		JsonParser jsonParser = new JsonParser()
		JsonObject object = (JsonObject)jsonParser.parse(BaseTx)
		JsonObject level = new JsonObject()
		level.addProperty("avg_rsp_time",avg_rsp_time)
		level.addProperty("usable_time",usable_time)
		JsonObject HttpBody = new JsonObject()
		HttpBody.add("base_tx",object)
		HttpBody.add("prices",prices)
		HttpBody.add("level",level)
		HttpBody.addProperty("provider",provider)
		HttpBody.addProperty("def_chain_id",def_chain_id)
		HttpBody.addProperty("service_name",service_name)
		HttpBody.addProperty("binding_type",binding_type)
		HttpBody.addProperty("deposit",deposit)
		Gson gson = new GsonBuilder().disableHtmlEscaping().create();
		String jsonString = gson.toJson(HttpBody)
		System.out.println(jsonString)
		ResponseObject response = WS.sendRequest(findTestObject('rest/service/post_service_bind', [ ('HttpBody') : jsonString, ('lcdIP') : GlobalVariable.lcdIP]))
		System.out.println(response.responseBodyContent)
		return response.responseBodyContent
	}

	@Keyword
	public static ResponseObject serviceBindingUpdate(String BaseTx, String provider, String service_name, String def_chain_id, String binding_type, JsonArray prices, String deposit, String avg_rsp_time, String usable_time){
		JsonParser jsonParser = new JsonParser()
		JsonObject object = (JsonObject)jsonParser.parse(BaseTx)
		JsonObject level = new JsonObject()
		level.addProperty("avg_rsp_time",avg_rsp_time)
		level.addProperty("usable_time",usable_time)
		JsonObject HttpBody = new JsonObject()
		HttpBody.add("base_tx",object)
		HttpBody.add("prices",prices)
		HttpBody.add("level",level)
		HttpBody.addProperty("def_chain_id",def_chain_id)
		HttpBody.addProperty("service_name",service_name)
		HttpBody.addProperty("binding_type",binding_type)
		HttpBody.addProperty("deposit",deposit)
		Gson gson = new GsonBuilder().disableHtmlEscaping().create();
		String jsonString = gson.toJson(HttpBody)
		System.out.println(jsonString)
		ResponseObject response = WS.sendRequest(findTestObject('rest/service/put_service_binding_update', [ ('HttpBody') : jsonString, ('serviceName'): service_name, ('provider') : provider, ('defChainId'): def_chain_id, ('serviceName'): service_name, ('lcdIP') : GlobalVariable.lcdIP]))
		System.out.println(response.responseBodyContent)
		return response
	}


	@Keyword
	public static ResponseObject serviceRespond(String BaseTx, String req_chain_id, String request_id, String data, String provider){
		JsonParser jsonParser = new JsonParser()
		JsonObject object = (JsonObject)jsonParser.parse(BaseTx)
		JsonObject level = new JsonObject()
		JsonObject HttpBody = new JsonObject()
		HttpBody.add("base_tx",object)
		HttpBody.addProperty("provider",provider)
		HttpBody.addProperty("req_chain_id",req_chain_id)
		HttpBody.addProperty("request_id",request_id)
		HttpBody.addProperty("data",data)
		Gson gson = new GsonBuilder().disableHtmlEscaping().create();
		String jsonString = gson.toJson(HttpBody)
		System.out.println(jsonString)
		ResponseObject response = WS.sendRequest(findTestObject('rest/service/invocation/post_service_response', [ ('HttpBody') : jsonString, ('lcdIP') : GlobalVariable.lcdIP]))
		System.out.println(response.responseBodyContent)
		return response
	}


	@Keyword
	public static ResponseObject serviceBindingUpdateEg(String BaseTx, String provider, String service_name, String def_chain_id, JsonArray prices, String deposit){
		JsonParser jsonParser = new JsonParser()
		JsonObject object = (JsonObject)jsonParser.parse(BaseTx)
		JsonObject HttpBody = new JsonObject()
		HttpBody.add("base_tx",object)
		HttpBody.add("prices",prices)
		HttpBody.addProperty("def_chain_id",def_chain_id)
		HttpBody.addProperty("service_name",service_name)
		HttpBody.addProperty("deposit",deposit)
		Gson gson = new GsonBuilder().disableHtmlEscaping().create();
		String jsonString = gson.toJson(HttpBody)
		System.out.println(jsonString)
		ResponseObject response = WS.sendRequest(findTestObject('rest/service/put_service_binding_update', [ ('HttpBody') : jsonString, ('serviceName'): service_name, ('provider') : provider, ('defChainId'): def_chain_id, ('serviceName'): service_name, ('lcdIP') : GlobalVariable.lcdIP]))
		System.out.println(response.responseBodyContent)
		return response
	}

	@Keyword
	public static ResponseObject serviceBindingUpdateWithoutParams(String BaseTx, String provider, String service_name, String def_chain_id){
		JsonParser jsonParser = new JsonParser()
		JsonObject object = (JsonObject)jsonParser.parse(BaseTx)
		JsonObject HttpBody = new JsonObject()
		HttpBody.add("base_tx",object)
		HttpBody.addProperty("def_chain_id",def_chain_id)
		HttpBody.addProperty("service_name",service_name)
		Gson gson = new GsonBuilder().disableHtmlEscaping().create();
		String jsonString = gson.toJson(HttpBody)
		System.out.println(jsonString)
		ResponseObject response = WS.sendRequest(findTestObject('rest/service/put_service_binding_update', [ ('HttpBody') : jsonString, ('serviceName'): service_name, ('provider') : provider, ('defChainId'): def_chain_id, ('serviceName'): service_name, ('lcdIP') : GlobalVariable.lcdIP]))
		System.out.println(response.responseBodyContent)
		return response
	}

	@Keyword
	public static String serviceDefineCorrectEg(String accountName, String password, String serviceName, TestData defineData){
		String fileContents = protoString()
		JsonArray tags

		tags= JsonArraySplitProduce(defineData.getValue("tags", 1),"\\s+")
		String[] BaseTxInfo = BaseTx.baseTxProduce(accountName, password)
		String responseBodyContent = serviceDefine(BaseTxInfo[0], BaseTxInfo[1], serviceName, defineData.getValue("service-description", 1), defineData.getValue("author-description", 1), tags, fileContents)
		WS.verifyEqual(responseBodyContent.contains("hash"), true)
		AccountUtils.waitUntilNextBlock()
		return responseBodyContent
	}

	@Keyword
	public static String serviceBindCorrectEg(String accountName, String password, String serviceName, TestData bindData ){
		JsonArray prices
		//repeat bind error
		prices = JsonArraySplitProduce(bindData.getValue("prices", 1),",")
		String[] BaseTxInfo = BaseTx.baseTxProduce(accountName,password)
		//(String BaseTx, String provider, String service_name, String def_chain_id, String binding_type, JsonArray prices, String deposit, String avg_rsp_time, String usable_time)
		String responseBodyContent = serviceBind(BaseTxInfo[0], BaseTxInfo[1], serviceName, GlobalVariable.chainId, bindData.getValue("bind-type", 1), prices, bindData.getValue("deposit", 1), bindData.getValue("avg-rsp-time", 1), bindData.getValue("usable-time", 1))
		AccountUtils.waitUntilNextBlock()
		return responseBodyContent
	}

	@Keyword
	public static String serviceRequestCallEg(String accountName, String password, String serviceName, TestData callData){
		String[] BaseTxInfo = BaseTx.baseTxProduce(accountName,password)
		JsonParser jsonParser = new JsonParser()
		JsonObject object = (JsonObject)jsonParser.parse(BaseTxInfo[0])
		JsonObject request = new JsonObject()
		request.addProperty("def_chain_id", GlobalVariable.chainId)
		request.addProperty("service_name", serviceName)
		request.addProperty("bind_chain_id", GlobalVariable.chainId)
		request.addProperty("method_id", Integer.parseInt(callData.getValue("method-id",1)))
		request.addProperty("service_fee", callData.getValue("service-fee",1))
		request.addProperty("data", callData.getValue("request-data",1))
		request.addProperty("provider", BaseTxInfo[1])
		request.addProperty("consumer", BaseTxInfo[1])
		JsonArray requests = new JsonArray()
		requests.add(request)
		JsonObject HttpBody = new JsonObject()
		HttpBody.add("base_tx",object)
		HttpBody.add("requests",requests)
		Gson gson = new GsonBuilder().disableHtmlEscaping().create();
		String jsonString = gson.toJson(HttpBody)
		System.out.println(jsonString)
		ResponseObject response = WS.sendRequest(findTestObject('rest/service/invocation/post_service_request_call', [ ('HttpBody') : jsonString, ('lcdIP') : GlobalVariable.lcdIP]))
		System.out.println(response.responseBodyContent)
		String requestId = getRequestId(response.responseBodyContent)
		System.out.println(requestId)
		AccountUtils.waitUntilNextBlock()
		return requestId
	}

	@Keyword
	public static String getRequestId(String response){
		JsonParser jsonParser = new JsonParser()
		JsonObject object = (JsonObject)jsonParser.parse(response)
		JsonArray tags = object.get("deliver_tx").getAsJsonObject().get("tags").getAsJsonArray()
		JsonObject tag
		String decodeKey
		String requestId
		for (int i = 0; i < tags.size(); i++){
			tag = tags.get(i).getAsJsonObject()
			decodeKey = BaseEncode.base64Decode(tag.get("key").getAsString())
			if (decodeKey.equals("request-id")) {
				requestId = BaseEncode.base64Decode(tag.get("value").getAsString())
				break
			}
		}
		return requestId
	}

	//split a string by splitCode and produce a JsonArray
	@Keyword
	public static JsonArray JsonArraySplitProduce(String splitArray, String splitCode){
		String[] tagsArray = splitArray.split(splitCode)
		JsonArray tagsJsonArray = new JsonArray()
		for(int i = 0; i < tagsArray.size(); i++) {
			tagsJsonArray.add(tagsArray[i])
		}
		return tagsJsonArray
	}

	@Keyword
	public static ResponseObject serviceDefinition(String serviceName,String chainId){
		ResponseObject response = WS.sendRequest(findTestObject('rest/service/get_service_definition', [ ('chain-id') : chainId, ('service-name') : serviceName, ('lcdIP') : GlobalVariable.lcdIP]))
		System.out.println(response.responseBodyContent)
		return response
	}

	@Keyword
	public static ResponseObject getServiceBinding(String serviceName, String defChainId, String bindChainId, String provider){
		ResponseObject response = WS.sendRequest(findTestObject('rest/service/get_service_binding', [ ('defChainId') : defChainId, ('bindChainId') : bindChainId, ('serviceName') : serviceName, ('provider') : provider, ('lcdIP') : GlobalVariable.lcdIP]))
		System.out.println(response.responseBodyContent)
		return response
	}

	@Keyword
	public static ResponseObject getServiceBindings(String serviceName, String defChainId){
		ResponseObject response = WS.sendRequest(findTestObject('rest/service/get_service_bindings', [ ('defChainId') : defChainId, ('serviceName') : serviceName, ('lcdIP') : GlobalVariable.lcdIP]))
		System.out.println(response.responseBodyContent)
		return response
	}

	@Keyword
	public static ResponseObject serviceBindingDisable(String BaseTx, String provider, String service_name, String def_chain_id){
		JsonParser jsonParser = new JsonParser()
		JsonObject object = (JsonObject)jsonParser.parse(BaseTx)
		JsonObject HttpBody = new JsonObject()
		HttpBody.add("base_tx",object)
		Gson gson = new GsonBuilder().disableHtmlEscaping().create();
		String jsonString = gson.toJson(HttpBody)
		System.out.println(jsonString)
		ResponseObject response = WS.sendRequest(findTestObject('rest/service/put_service_binding_disable', [ ('HttpBody') : jsonString, ('serviceName'): service_name, ('provider') : provider, ('defChainId'): def_chain_id, ('lcdIP') : GlobalVariable.lcdIP]))
		System.out.println(response.responseBodyContent)
		return response
	}


	@Keyword
	public static ResponseObject serviceBindingEnable(String BaseTx, String provider, String service_name, String def_chain_id, String deposit){
		JsonParser jsonParser = new JsonParser()
		JsonObject object = (JsonObject)jsonParser.parse(BaseTx)
		JsonObject HttpBody = new JsonObject()
		HttpBody.add("base_tx",object)
		HttpBody.addProperty("deposit",deposit)
		Gson gson = new GsonBuilder().disableHtmlEscaping().create();
		String jsonString = gson.toJson(HttpBody)
		System.out.println(jsonString)
		ResponseObject response = WS.sendRequest(findTestObject('rest/service/put_service_binding_enable', [ ('HttpBody') : jsonString, ('serviceName'): service_name, ('provider') : provider, ('defChainId'): def_chain_id, ('lcdIP') : GlobalVariable.lcdIP]))
		System.out.println(response.responseBodyContent)
		return response
	}

	@Keyword
	public static ResponseObject serviceBindingDepositRefund(String BaseTx, String provider, String service_name, String def_chain_id){
		JsonParser jsonParser = new JsonParser()
		JsonObject object = (JsonObject)jsonParser.parse(BaseTx)
		JsonObject HttpBody = new JsonObject()
		HttpBody.add("base_tx",object)
		Gson gson = new GsonBuilder().disableHtmlEscaping().create();
		String jsonString = gson.toJson(HttpBody)
		System.out.println(jsonString)
		ResponseObject response = WS.sendRequest(findTestObject('rest/service/put_service_binding_deposit_refund', [ ('HttpBody') : jsonString, ('serviceName'): service_name, ('provider') : provider, ('defChainId'): def_chain_id, ('lcdIP') : GlobalVariable.lcdIP]))
		System.out.println(response.responseBodyContent)
		return response
	}

	@Keyword
	public static JsonObject requestsJsonObjectProduce(String provider, String service_name, String def_chain_id, String bind_chain_id, int method_id, String service_fee, String data, String consumer){
		JsonObject requests = new JsonObject()
		requests.addProperty("def_chain_id", def_chain_id)
		requests.addProperty("service_name", service_name)
		requests.addProperty("bind_chain_id", bind_chain_id)
		requests.addProperty("method_id", method_id)
		requests.addProperty("service_fee", service_fee)
		requests.addProperty("data", data)
		requests.addProperty("provider", provider)
		requests.addProperty("consumer", consumer)
		return requests
	}

	@Keyword
	public static JsonArray requestsJsonArrayProduce(Vector<JsonObject> methods){
		JsonArray requests = new JsonArray()
		println methods.size()
		for (int i = 0; i < methods.size(); i++) {
			requests.add(methods.get(i))
		}
		return requests
	}

	//String provider, String service_name, String def_chain_id, String bind_chain_id, String method_id, String service_fee, String data, String consumer
	@Keyword
	public static ResponseObject serviceRequestListCall(String BaseTx, JsonArray requests){
		JsonParser jsonParser = new JsonParser()
		JsonObject object = (JsonObject)jsonParser.parse(BaseTx)
		JsonObject HttpBody = new JsonObject()
		HttpBody.add("base_tx",object)
		HttpBody.add("requests",requests)
		Gson gson = new GsonBuilder().disableHtmlEscaping().create();
		String jsonString = gson.toJson(HttpBody)
		System.out.println(jsonString)
		ResponseObject response = WS.sendRequest(findTestObject('rest/service/invocation/post_service_request_call', [ ('HttpBody') : jsonString, ('lcdIP') : GlobalVariable.lcdIP]))
		System.out.println(response.responseBodyContent)
		return response
	}

	@Keyword
	public static ResponseObject serviceRequestOneCall(String BaseTx, String provider, String service_name, String def_chain_id, String bind_chain_id, int method_id, String service_fee, String data, String consumer){
		JsonParser jsonParser = new JsonParser()
		JsonObject object = (JsonObject)jsonParser.parse(BaseTx)
		JsonObject request = new JsonObject()
		request.addProperty("def_chain_id", def_chain_id)
		request.addProperty("service_name", service_name)
		request.addProperty("bind_chain_id", bind_chain_id)
		request.addProperty("method_id", method_id)
		request.addProperty("service_fee", service_fee)
		request.addProperty("data", data)
		request.addProperty("provider", provider)
		request.addProperty("consumer", consumer)
		JsonArray requests = new JsonArray()
		requests.add(request)
		JsonObject HttpBody = new JsonObject()
		HttpBody.add("base_tx",object)
		HttpBody.add("requests",requests)
		Gson gson = new GsonBuilder().disableHtmlEscaping().create();
		String jsonString = gson.toJson(HttpBody)
		System.out.println(jsonString)
		ResponseObject response = WS.sendRequest(findTestObject('rest/service/invocation/post_service_request_call', [ ('HttpBody') : jsonString, ('lcdIP') : GlobalVariable.lcdIP]))
		System.out.println(response.responseBodyContent)
		return response
	}

	@Keyword
	public static ResponseObject serviceRequestsQuery(String provider, String serviceName, String defChainId, String bindChainId){
		//${defChainId}/${serviceName}/${bindChainId}/${provider}
		ResponseObject response = WS.sendRequest(findTestObject('rest/service/invocation/get_service_requests', [ ('defChainId') : defChainId, ('bindChainId') : bindChainId, ('provider') : provider, ('serviceName') : serviceName, ('lcdIP') : GlobalVariable.lcdIP]))
		System.out.println(response.responseBodyContent)
		return response
	}

	@Keyword
	public static ResponseObject serviceResponseQuery(String requestId, String reqChainId){
		ResponseObject response = WS.sendRequest(findTestObject('rest/service/invocation/get_service_response', [ ('reqId') : requestId, ('reqChainId') : reqChainId, ('lcdIP') : GlobalVariable.lcdIP]))
		System.out.println(response.responseBodyContent)
		return response
	}

	@Keyword
	public static ResponseObject serviceFeesWithdraw(String BaseTx, String address){
		JsonParser jsonParser = new JsonParser()
		JsonObject object = (JsonObject)jsonParser.parse(BaseTx)
		JsonObject HttpBody = new JsonObject()
		HttpBody.add("base_tx",object)
		Gson gson = new GsonBuilder().disableHtmlEscaping().create();
		String jsonString = gson.toJson(HttpBody)
		System.out.println(jsonString)
		ResponseObject response = WS.sendRequest(findTestObject('rest/service/invocation/post_service_fees_withdraw', [ ('HttpBody') : jsonString, ('address') : address ,('lcdIP') : GlobalVariable.lcdIP]))
		System.out.println(response.responseBodyContent)
		return response
	}

	@Keyword
	public static ResponseObject serviceFeesRefund(String BaseTx, String address){
		JsonParser jsonParser = new JsonParser()
		JsonObject object = (JsonObject)jsonParser.parse(BaseTx)
		JsonObject HttpBody = new JsonObject()
		HttpBody.add("base_tx",object)
		Gson gson = new GsonBuilder().disableHtmlEscaping().create();
		String jsonString = gson.toJson(HttpBody)
		System.out.println(jsonString)
		ResponseObject response = WS.sendRequest(findTestObject('rest/service/invocation/post_service_fees_refund', [ ('HttpBody') : jsonString, ('address') : address ,('lcdIP') : GlobalVariable.lcdIP]))
		System.out.println(response.responseBodyContent)
		return response
	}

	@Keyword
	public static ResponseObject serviceFeesQuery(String address){
		ResponseObject response = WS.sendRequest(findTestObject('rest/service/invocation/get_service_fees_query', [ ('address') : address, ('lcdIP') : GlobalVariable.lcdIP]))
		System.out.println(response.responseBodyContent)
		return response
	}


	@Keyword
	public static String serviceFeesCaculate(String queryResponse ,String type){
		if (CmdUtils.Parse(queryResponse).get(type).jsonNull) {
			return CmdUtils.IrisattoToIris("0")
		}

		JsonArray array = CmdUtils.Parse(queryResponse).get(type).getAsJsonArray()
		String amount = array.get(0).getAsJsonObject().get("amount").getAsString()
		return CmdUtils.IrisattoToIris(amount)
	}

	@Keyword
	public static String serviceFeesClean(String accountName, String password, String address){
		String[] BaseTxInfo = BaseTx.baseTxProduce(accountName,password)
		serviceFeesRefund(BaseTxInfo[0], address)
		AccountUtils.waitUntilNextBlock()
		BaseTxInfo = BaseTx.baseTxProduce(accountName,password)
		serviceFeesWithdraw(BaseTxInfo[0], address)
		AccountUtils.waitUntilNextBlock()
	}

	@Keyword
	public static String protoString(){
		return "syntax = \"proto3\";\n\npackage helloworld;\n\n// The greeting service definition.\nservice Greeter {\n    //@Attribute description: sayHello\n    //@Attribute output_privacy: NoPrivacy\n    //@Attribute output_cached: NoCached\n    rpc SayHello (HelloRequest) returns (HelloReply) {}\n\n    //@Attribute description: sayGoodbye\n    //@Attribute output_privacy: NoPrivacy\n    //@Attribute output_cached: NoCached\n    rpc sayGoodbye (GoodbyeRequest) returns (GoodbyeReply) {}\n}\n\n// The request message containing the user's name.\nmessage HelloRequest {\n    string a = 1;\n    string b = 2;\n}\n\nmessage GoodbyeRequest {\n    string a = 1;\n    string b = 2;\n}\n\n\n// The response message containing the greetings\nmessage HelloReply {\n    string c = 3;\n    string d = 4;\n}\n\nmessage GoodbyeReply {\n    string c = 3;\n    string d = 4;\n}\n\n"
	}

}
