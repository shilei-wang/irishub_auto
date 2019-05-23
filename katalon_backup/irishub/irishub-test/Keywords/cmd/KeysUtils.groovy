package cmd

import static com.kms.katalon.core.checkpoint.CheckpointFactory.findCheckpoint
import static com.kms.katalon.core.testcase.TestCaseFactory.findTestCase
import static com.kms.katalon.core.testdata.TestDataFactory.findTestData
import static com.kms.katalon.core.testobject.ObjectRepository.findTestObject

import java.util.Map

import com.google.gson.JsonArray
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

public class KeysUtils {
	@Keyword
	public static Map<String, String> add(String pw){
		Map<String, String> account_map = new HashMap<String, String>();
		String name = "user_"+ CmdUtils.generateRandomID()
		String cmd ="iriscli keys add "+name+GlobalVariable.json
		ResponseObject response = CmdUtils.sendRequest('cmd/CmdWithOneArgs', cmd,  pw, "sync")
		WS.verifyEqual(StringUtils.stringContains(response.responseBodyContent, name), true)
		
		JsonObject re = CmdUtils.Parse(response.responseBodyContent).getAsJsonObject()
		account_map.put("name", name)
		account_map.put("mnemonic", re.get("seed").getAsString())
		return account_map
	}

	@Keyword
	public static delete(String user, String pw){
		String cmd ="iriscli keys delete "+user
		ResponseObject response = CmdUtils.sendRequest('cmd/CmdWithOneArgs', cmd,  pw, "sync")
		WS.verifyEqual(StringUtils.stringContains(response.responseBodyContent, "Password deleted forever"), true)
	}

	@Keyword
	public static update(String user, String pw_old, String pw_new){
		String cmd ="iriscli keys update "+user
		ResponseObject response = CmdUtils.sendRequest('cmd/CmdWithTwoArgs', cmd,  pw_old,  pw_new, "sync")
		WS.verifyEqual(StringUtils.stringContains(response.responseBodyContent, "Password successfully updated"), true)
	}

	@Keyword
	public static String recover(String pw, String mnemonic){
		String name = "user_"+ CmdUtils.generateRandomID()
		String cmd ="iriscli keys add --recover "+name
		ResponseObject response = CmdUtils.sendRequest('cmd/CmdWithTwoArgs', cmd,  pw,  mnemonic, "sync")
		WS.verifyEqual(StringUtils.stringContains(response.responseBodyContent, name), true)
		return name
	}

	@Keyword
	public static String list(){
		String cmd ="iriscli keys list".concat(GlobalVariable.json)
		ResponseObject response = CmdUtils.sendRequest('cmd/CmdWithOneArgs', cmd,  "sync")
		return response.responseBodyContent
	}

	@Keyword
	public static boolean verifyKeyInlist(String name){
		String response = list()		
		if(StringUtils.stringContains(response, name)){
			String user_faa = CmdUtils.getAddressFromName(name, "faa")
			return StringUtils.stringContains(response, user_faa)
		} else {
			return false
		}
	}
}
