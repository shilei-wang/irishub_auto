import static com.kms.katalon.core.checkpoint.CheckpointFactory.findCheckpoint
import static com.kms.katalon.core.testcase.TestCaseFactory.findTestCase
import static com.kms.katalon.core.testdata.TestDataFactory.findTestData
import static com.kms.katalon.core.testobject.ObjectRepository.findTestObject

import java.util.Map

import com.kms.katalon.core.checkpoint.Checkpoint as Checkpoint
import com.kms.katalon.core.cucumber.keyword.CucumberBuiltinKeywords as CucumberKW
import com.kms.katalon.core.mobile.keyword.MobileBuiltInKeywords as Mobile
import com.kms.katalon.core.model.FailureHandling as FailureHandling
import com.kms.katalon.core.testcase.TestCase as TestCase
import com.kms.katalon.core.testdata.TestData as TestData
import com.kms.katalon.core.testobject.TestObject as TestObject
import com.kms.katalon.core.webservice.keyword.WSBuiltInKeywords as WS
import com.kms.katalon.core.webui.keyword.WebUiBuiltInKeywords as WebUI
import internal.GlobalVariable as GlobalVariable
import utils.CmdUtils
import utils.StringUtils as StringUtils
import cmd.KeysUtils as KeysUtils


Utils u = new Utils()

String cmd = u.command + u.user_map1.get("name")
response = CmdUtils.sendRequest('cmd/CmdWithOneArgs', cmd,  u.password, "sync")
WS.verifyEqual(StringUtils.stringContains(response.responseBodyContent, "Password deleted forever"), true)

cmd = u.command + u.user_map2.get("name")
response = CmdUtils.sendRequest('cmd/CmdWithOneArgs', cmd,  u.password, "sync")
WS.verifyEqual(StringUtils.stringContains(response.responseBodyContent, "Password deleted forever"), true)

cmd = u.command + u.user_map3.get("name")
response = CmdUtils.sendRequest('cmd/CmdWithOneArgs', cmd,  u.password, "sync")
WS.verifyEqual(StringUtils.stringContains(response.responseBodyContent, "Password deleted forever"), true)

WS.verifyEqual(KeysUtils.verifyKeyInlist(u.user_map1.get("name")), false)
WS.verifyEqual(KeysUtils.verifyKeyInlist(u.user_map2.get("name")), false)
WS.verifyEqual(KeysUtils.verifyKeyInlist(u.user_map3.get("name")), false)



class Utils {
	public String command
	public String password
	public Map<String, String> user_map1
	public Map<String, String> user_map2
	public Map<String, String> user_map3
	
	public Utils(){
		password = findTestData('base/faucet').getValue('password', 1)
		user_map1 = KeysUtils.add(password)
		user_map2 = KeysUtils.add(password)
		user_map3 = KeysUtils.add(password)
		
		WS.verifyEqual(KeysUtils.verifyKeyInlist(user_map1.get("name")), true)
		WS.verifyEqual(KeysUtils.verifyKeyInlist(user_map2.get("name")), true)
		WS.verifyEqual(KeysUtils.verifyKeyInlist(user_map3.get("name")), true)
		
		command ="iriscli keys delete "
	}
}