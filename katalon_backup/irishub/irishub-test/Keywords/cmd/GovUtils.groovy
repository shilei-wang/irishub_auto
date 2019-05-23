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

import com.google.gson.JsonObject as JsonObject;

public class GovUtils {
	@Keyword
	public static String getProposalStatus(String id){
		String cmd = "iriscli gov query-proposal".concat(GlobalVariable.node).concat(GlobalVariable.json)+" --proposal-id="+id
		ResponseObject response = CmdUtils.sendRequest('cmd/CmdWithOneArgs', cmd, "sync")
		return StringUtils.getValueFromKey(response.responseBodyContent, "proposal_status")
	}

	@Keyword
	public static String submitProposal(String account, TestData td, int i){
		String cmd = 'iriscli gov submit-proposal --title=t --description=t --type=TxTaxUsage --usage=Burn --percent=0.0000000001'.concat(GlobalVariable.chainId).concat(GlobalVariable.node).concat(' --from='+account)
		cmd = CmdUtils.addTxFee(cmd, findTestData('base/tx'), 1)
		cmd = CmdUtils.generateCmd(cmd, td, i)

		//CmdUtils.pl(cmd)
		ResponseObject response = CmdUtils.sendRequest('cmd/CmdWithOneArgs', cmd, "wait")
		WS.verifyEqual(StringUtils.stringContains(response.responseBodyContent,"tx hash"), true)
		return CmdUtils.getValueFromTag(response.responseBodyContent, "proposal-id")
	}

	@Keyword
	public static String submitProposal(String account, String deposit){
		String cmd = 'iriscli gov submit-proposal --title=t --description=t --type=TxTaxUsage --usage=Burn --percent=0.0000000001'.concat(GlobalVariable.chainId).concat(GlobalVariable.node).concat(' --from='+account)
		cmd = cmd+" --deposit="+deposit
		cmd = CmdUtils.addTxFee(cmd, findTestData('base/tx'), 1)

		//CmdUtils.pl(cmd)
		ResponseObject response = CmdUtils.sendRequest('cmd/CmdWithOneArgs', cmd, "wait")
		WS.verifyEqual(StringUtils.stringContains(response.responseBodyContent,"tx hash"), true)
		return CmdUtils.getValueFromTag(response.responseBodyContent, "proposal-id")
	}

	@Keyword
	public static depositProposal(String account, String proposal_id, String deposit){
		String cmd = 'iriscli gov deposit'.concat(GlobalVariable.chainId).concat(GlobalVariable.node).concat(' --from='+account)
		cmd = cmd+" --deposit="+deposit+" --proposal-id="+proposal_id
		cmd = CmdUtils.addTxFee(cmd, findTestData('base/tx'), 1)

		//CmdUtils.pl(cmd)
		ResponseObject response = CmdUtils.sendRequest('cmd/CmdWithOneArgs', cmd, "wait")
		WS.verifyEqual(StringUtils.stringContains(response.responseBodyContent,"tx hash"), true)
	}

	@Keyword
	public static voteProposal(String account, String proposal_id, String option){
		account = CmdUtils.getAddressFromName(account,"faa")
		String cmd = 'iriscli gov vote'.concat(GlobalVariable.chainId).concat(GlobalVariable.node).concat(' --from='+account)
		cmd = cmd+" --option="+option+" --proposal-id="+proposal_id
		cmd = CmdUtils.addTxFee(cmd, findTestData('base/tx'), 1)

		//CmdUtils.pl(cmd)
		ResponseObject response = CmdUtils.sendRequest('cmd/CmdWithOneArgs', cmd, "wait")
		WS.verifyEqual(StringUtils.stringContains(response.responseBodyContent,"tx hash"), true)
	}

	@Keyword
	public static String getParam(String module, String key){
		String cmd = 'iriscli gov query-params'.concat(GlobalVariable.node).concat(GlobalVariable.json)+" --module="+module
		ResponseObject response = CmdUtils.sendRequest('cmd/CmdWithOneArgs', cmd, "sync")
		JsonObject re = CmdUtils.Parse(response.responseBodyContent).getAsJsonObject()

		return re.get("value").getAsJsonObject().get(key).getAsString()
	}
}
