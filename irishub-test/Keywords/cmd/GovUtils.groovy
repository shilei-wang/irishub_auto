package cmd

import static com.kms.katalon.core.checkpoint.CheckpointFactory.findCheckpoint
import static com.kms.katalon.core.testcase.TestCaseFactory.findTestCase
import static com.kms.katalon.core.testdata.TestDataFactory.findTestData
import static com.kms.katalon.core.testobject.ObjectRepository.findTestObject

import java.util.Map

import com.google.gson.JsonObject
import com.google.gson.JsonArray
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

public class GovUtils {
	@Keyword
	public static String getProposalStatus(String id){
		String cmd = "iriscli gov query-proposal".concat(' --node=').concat(GlobalVariable.node)+" --proposal-id="+id
		ResponseObject response = CmdUtils.sendRequest('cmd/CmdWithOneArgs', cmd, "sync")
		return StringUtils.getValueFromKey(response.responseBodyContent, "proposal_status")
	}

	@Keyword
	public static String submitProposal(String account, TestData td, int i){
		String cmd = 'iriscli gov submit-proposal --chain-id='.concat(GlobalVariable.chainId).concat(' --node=').concat(GlobalVariable.node).concat(
				' --from=').concat(account)
		cmd = CmdUtils.addTxFee(cmd, findTestData('base/tx'), 1)
		cmd = CmdUtils.generateCmd(cmd, td, i)

		//CmdUtils.pl(cmd)
		ResponseObject response = CmdUtils.sendRequest('cmd/CmdWithOneArgs', cmd, "wait")
		WS.verifyEqual(StringUtils.stringContains(response.responseBodyContent,"tx hash"), true)
		return StringUtils.getValueFromKey(response.responseBodyContent, "proposal-id")
	}

	@Keyword
	public static String submitProposal(String account, String type, String deposit){
		String cmd = 'iriscli gov submit-proposal --title=test --description=test --chain-id='.concat(GlobalVariable.chainId).concat(' --node=').concat(GlobalVariable.node).concat(' --from=').concat(account)
		cmd = cmd+" --type="+type+" --deposit="+deposit
		cmd = CmdUtils.addTxFee(cmd, findTestData('base/tx'), 1)

		//CmdUtils.pl(cmd)
		ResponseObject response = CmdUtils.sendRequest('cmd/CmdWithOneArgs', cmd, "wait")
		WS.verifyEqual(StringUtils.stringContains(response.responseBodyContent,"tx hash"), true)
		return StringUtils.getValueFromKey(response.responseBodyContent, "proposal-id")
	}

	@Keyword
	public static depositProposal(String account, String proposal_id, String deposit){
		String cmd = 'iriscli gov deposit --chain-id='.concat(GlobalVariable.chainId).concat(' --node=').concat(GlobalVariable.node).concat(' --from=').concat(account)
		cmd = cmd+" --deposit="+deposit+" --proposal-id="+proposal_id
		cmd = CmdUtils.addTxFee(cmd, findTestData('base/tx'), 1)

		//CmdUtils.pl(cmd)
		ResponseObject response = CmdUtils.sendRequest('cmd/CmdWithOneArgs', cmd, "wait")
		WS.verifyEqual(StringUtils.stringContains(response.responseBodyContent,"tx hash"), true)
	}

	@Keyword
	public static voteProposal(String account, String proposal_id, String option){
		account = CmdUtils.getAddressFromName(account,"faa")
		String cmd = 'iriscli gov vote --chain-id='.concat(GlobalVariable.chainId).concat(' --node=').concat(GlobalVariable.node).concat(' --from=').concat(account)
		cmd = cmd+" --option="+option+" --proposal-id="+proposal_id
		cmd = CmdUtils.addTxFee(cmd, findTestData('base/tx'), 1)

		//CmdUtils.pl(cmd)
		ResponseObject response = CmdUtils.sendRequest('cmd/CmdWithOneArgs', cmd, "wait")
		WS.verifyEqual(StringUtils.stringContains(response.responseBodyContent,"tx hash"), true)
	}

	@Keyword
	public static saveParams(){
		String cmd = 'iriscli gov pull-params'.concat(' --node=').concat(GlobalVariable.node)
		CmdUtils.sendRequest('cmd/CmdWithOneArgs', cmd, "sync")
	}

	@Keyword
	public static Map<String, String> getParams(){
		Map<String, String> param_map = new HashMap<String, String>()

		//读取govDepositProcedure
		String cmd = 'iriscli gov query-params --key=Gov/govDepositProcedure'.concat(' --node=').concat(GlobalVariable.node)
		ResponseObject response = CmdUtils.sendRequest('cmd/CmdWithOneArgs', cmd, "sync")
		String msg = response.responseBodyContent.replace("\\","").replace("\"{","{").replace("}\"","}") //这里iris返回有bug ， json不标准
		JsonObject re = CmdUtils.Parse(msg).get("value").getAsJsonObject()
		JsonArray array = re.get("min_deposit").getAsJsonArray()
		JsonObject item = array.get(0).getAsJsonObject()
		param_map.put("max_deposit_period", re.get("max_deposit_period").getAsString())
		param_map.put("amount", item.get("amount").getAsString())

		//读取govTallyingProcedure
		cmd = 'iriscli gov query-params --key=Gov/govTallyingProcedure'.concat(' --node=').concat(GlobalVariable.node)
		response = CmdUtils.sendRequest('cmd/CmdWithOneArgs', cmd, "sync")
		msg = response.responseBodyContent.replace("\\","").replace("\"{","{").replace("}\"","}") //这里iris返回有bug ， json不标准
		re = CmdUtils.Parse(msg).get("value").getAsJsonObject()
		param_map.put("threshold", re.get("threshold").getAsString())
		param_map.put("veto", re.get("veto").getAsString())
		param_map.put("participation", re.get("participation").getAsString())

		return param_map
	}
}
