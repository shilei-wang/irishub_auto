package cmd

import static com.kms.katalon.core.checkpoint.CheckpointFactory.findCheckpoint
import static com.kms.katalon.core.testcase.TestCaseFactory.findTestCase
import static com.kms.katalon.core.testdata.TestDataFactory.findTestData
import static com.kms.katalon.core.testobject.ObjectRepository.findTestObject

import com.google.gson.JsonObject
import com.kms.katalon.core.annotation.Keyword
import com.kms.katalon.core.checkpoint.Checkpoint
import com.kms.katalon.core.cucumber.keyword.CucumberBuiltinKeywords as CucumberKW
import com.kms.katalon.core.mobile.keyword.MobileBuiltInKeywords as Mobile
import com.kms.katalon.core.model.FailureHandling
import com.kms.katalon.core.testcase.TestCase
import com.kms.katalon.core.testdata.TestData
import com.kms.katalon.core.testobject.TestObject
import com.kms.katalon.core.testobject.ResponseObject as ResponseObject
import com.kms.katalon.core.webservice.keyword.WSBuiltInKeywords as WS
import com.kms.katalon.core.webui.keyword.WebUiBuiltInKeywords as WebUI

import internal.GlobalVariable
import utils.CmdUtils as CmdUtils
import utils.StringUtils as StringUtils
import com.google.gson.JsonArray as JsonArray;



public class ServiceUtils {
	@Keyword
	public static String defineService(String account){
		String serviceName = generateRandomServiceName()
		String cmd = 'iriscli service define'.concat(GlobalVariable.chainId).concat(GlobalVariable.node)
			.concat(' --from='+account)
		cmd = CmdUtils.addTxFee(cmd, findTestData('base/tx'), 1)
		cmd = CmdUtils.generateCmd(cmd, findTestData('service/define/define/IRISHUB-624'), 1)
		cmd = cmd.replace("001_service", serviceName)

		ResponseObject response = CmdUtils.sendRequest('cmd/CmdWithOneArgs', cmd, "wait")
		//CmdUtils.pl(response.responseBodyContent)
		WS.verifyEqual(StringUtils.stringContains(response.responseBodyContent,"tx hash"), true)
		return serviceName
	}

	@Keyword
	public static bindService(String account, String serviceName){
		//bind金额多放了100iris 避免未响应处罚扣款后小于1000。
		String cmd = 'iriscli service bind'.concat(GlobalVariable.chainId).concat(GlobalVariable.defchainId).concat(GlobalVariable.node)
				.concat(GlobalVariable.node).concat(' --from='+account)
		cmd = CmdUtils.generateCmd(cmd, findTestData('service/bind/bind/IRISHUB-726'), 1)
		cmd = CmdUtils.addTxFee(cmd, findTestData('base/tx'), 1)
		cmd = cmd.concat(' --service-name=').concat(serviceName)

		ResponseObject response = CmdUtils.sendRequest('cmd/CmdWithOneArgs',cmd, "wait")
		WS.verifyEqual(StringUtils.stringContains(response.responseBodyContent,"tx hash"), true)
	}

	@Keyword
	public static String call(String account, String provider_account, String serviceName){
		String provider = CmdUtils.getAddressFromName(provider_account, "faa")
		String cmd = 'iriscli service call'.concat(GlobalVariable.chainId).concat(GlobalVariable.defchainId).concat(GlobalVariable.bindchainId).concat(GlobalVariable.node)
			.concat(' --from=').concat(account)+" --service-name="+serviceName+" --provider="+provider
		cmd = CmdUtils.generateCmd(cmd, findTestData('service/invocation/call/IRISHUB-785'), 1)
		cmd = CmdUtils.addTxFee(cmd, findTestData('base/tx'), 1)

		ResponseObject response = CmdUtils.sendRequest('cmd/CmdWithOneArgs',cmd, "wait")
		WS.verifyEqual(StringUtils.stringContains(response.responseBodyContent,"tx hash"), true)

		return CmdUtils.getValueFromTag(response.responseBodyContent, "request-id")
	}



	@Keyword
	public static respond(String account, String request_id){
		String cmd = 'iriscli service respond'.concat(GlobalVariable.chainId).concat(GlobalVariable.reqchainId).concat(GlobalVariable.node)
			.concat(' --from=').concat(account)+" --request-id="+request_id
		cmd = CmdUtils.generateCmd(cmd, findTestData('service/invocation/respond/IRISHUB-789'), 1)
		cmd = CmdUtils.addTxFee(cmd, findTestData('base/tx'), 1)

		ResponseObject response = CmdUtils.sendRequest('cmd/CmdWithOneArgs',cmd, "wait")
		WS.verifyEqual(StringUtils.stringContains(response.responseBodyContent,"tx hash"), true)
	}

	@Keyword
	public static String fees(String account, String type){
		String address = CmdUtils.getAddressFromName(account, "faa")
		String cmd = 'iriscli service fees'.concat(GlobalVariable.node)+" "+address
		ResponseObject response = CmdUtils.sendRequest('cmd/CmdWithOneArgs',cmd, "wait")

		if (CmdUtils.Parse(response.responseBodyContent).get(type).jsonNull) {
			return CmdUtils.IrisattoToIris("0")
		}

		//CmdUtils.pl(response.responseBodyContent)
		JsonArray array = CmdUtils.Parse(response.responseBodyContent).get(type).getAsJsonArray()
		String amount = array.get(0).getAsJsonObject().get("amount").getAsString()
		return CmdUtils.IrisattoToIris(amount)
	}

	@Keyword
	public static cleanFees(String account){
		String cmd = 'iriscli service refund-fees'.concat(GlobalVariable.chainId).concat(GlobalVariable.node)
				.concat(' --from=').concat(account)
		cmd = CmdUtils.addTxFee(cmd, findTestData('base/tx'), 1)
		ResponseObject response = CmdUtils.sendRequest('cmd/CmdWithOneArgs',cmd, "wait")

		cmd = 'iriscli service withdraw-fees'.concat(GlobalVariable.chainId).concat(GlobalVariable.node)
				.concat(' --from=').concat(account)
		cmd = CmdUtils.addTxFee(cmd, findTestData('base/tx'), 1)
		response = CmdUtils.sendRequest('cmd/CmdWithOneArgs',cmd, "wait")
	}

	@Keyword
	public static disableService(String account, String serviceName){
		String cmd = 'iriscli service disable'.concat(GlobalVariable.chainId).concat(GlobalVariable.defchainId).concat(GlobalVariable.node)
				.concat(' --from=').concat(account)+" --service-name="+serviceName
		cmd = CmdUtils.addTxFee(cmd, findTestData('base/tx'), 1)

		ResponseObject response = CmdUtils.sendRequest('cmd/CmdWithOneArgs',cmd, "wait")
		WS.verifyEqual(StringUtils.stringContains(response.responseBodyContent,"tx hash"), true)
	}

	@Keyword
	public static updateBinding(String account, String serviceName, String prices, String deposit){
		String cmd = 'iriscli service update-binding'.concat(GlobalVariable.chainId).concat(GlobalVariable.defchainId).concat(GlobalVariable.node)
				.concat(' --from='+account)+" --service-name="+serviceName+" --prices="+prices+" --deposit="+deposit
		cmd = CmdUtils.addTxFee(cmd, findTestData('base/tx'), 1)

		ResponseObject response = CmdUtils.sendRequest('cmd/CmdWithOneArgs',cmd, "wait")
		WS.verifyEqual(StringUtils.stringContains(response.responseBodyContent,"tx hash"), true)
	}

	@Keyword
	public static refundDeposit(String account, String serviceName){
		String cmd = 'iriscli service refund-deposit'.concat(GlobalVariable.chainId).concat(GlobalVariable.defchainId).concat(GlobalVariable.node)
		.concat(' --from='+account)+" --service-name="+serviceName
		cmd = CmdUtils.addTxFee(cmd, findTestData('base/tx'), 1)

		ResponseObject response = CmdUtils.sendRequest('cmd/CmdWithOneArgs',cmd, "wait")
		CmdUtils.pl(response.responseBodyContent)
		WS.verifyEqual(StringUtils.stringContains(response.responseBodyContent,"tx hash"), true)
	}

	@Keyword
	public static String generateRandomServiceName(){
		String RandomID =  CmdUtils.generateRandomID()
		String serviceName =  "001_service_"+RandomID
	}

	@Keyword
	public static String getBindingInfo(String provider, String serviceName){
		String provider_fva = CmdUtils.getAddressFromName(provider, "faa")
		String cmd = 'iriscli service binding'.concat(GlobalVariable.chainId).concat(GlobalVariable.defchainId).concat(GlobalVariable.bindchainId).concat(GlobalVariable.node)
			.concat(' --service-name=').concat(serviceName).concat(' --provider=').concat(provider_fva)
		ResponseObject response = CmdUtils.sendRequest('cmd/CmdWithOneArgs', cmd, "sync")
		return response.responseBodyContent
	}
}
