package cmd

import static com.kms.katalon.core.checkpoint.CheckpointFactory.findCheckpoint
import static com.kms.katalon.core.testcase.TestCaseFactory.findTestCase
import static com.kms.katalon.core.testdata.TestDataFactory.findTestData
import static com.kms.katalon.core.testobject.ObjectRepository.findTestObject

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

public class GuardianUtils {
	@Keyword
	public static String profilers(){
		String cmd = 'iriscli guardian profilers'.concat(GlobalVariable.chainId).concat(GlobalVariable.node).concat(GlobalVariable.json)
		ResponseObject response = CmdUtils.sendRequest('cmd/CmdWithOneArgs', cmd, "sync")
		return response.responseBodyContent
	}
	
	@Keyword
	public static String trustees(){
		String cmd = 'iriscli guardian trustees'.concat(GlobalVariable.chainId).concat(GlobalVariable.node).concat(GlobalVariable.json)
		ResponseObject response = CmdUtils.sendRequest('cmd/CmdWithOneArgs', cmd, "sync")
		return response.responseBodyContent
	}

	@Keyword
	public static int findItem(JsonArray array, String add){
		for (int i = 0; i < array.size() ; i++) {
			JsonObject item = array.get(i).getAsJsonObject()
			if (item.get("address").getAsString() == add){
				return i
			}
		}

		return -1
	}
	
	
	@Keyword
	public static addTrustee(String account, String address){
		String cmd = 'iriscli guardian add-trustee --description=t --chain-id='.concat(GlobalVariable.chainId).concat(' --node=').concat(GlobalVariable.node).concat(
				' --from=').concat(account)+" --address="+address
		cmd = CmdUtils.addTxFee(cmd, findTestData('base/tx'), 1)

		ResponseObject response = CmdUtils.sendRequest('cmd/CmdWithOneArgs', cmd, "wait")
		WS.verifyEqual(StringUtils.stringContains(response.responseBodyContent,"tx hash"), true)		
	}
	
	
	@Keyword
	public static addProfilers(String account, String address){
		String cmd = 'iriscli guardian add-profiler --description=t'.concat(GlobalVariable.chainId).concat(GlobalVariable.node).concat(' --from='+account)+" --address="+address
		cmd = CmdUtils.addTxFee(cmd, findTestData('base/tx'), 1)

		ResponseObject response = CmdUtils.sendRequest('cmd/CmdWithOneArgs', cmd, "wait")
		WS.verifyEqual(StringUtils.stringContains(response.responseBodyContent,"tx hash"), true)
	}
	
}
