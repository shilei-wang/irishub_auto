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

user_faa1 = CmdUtils.getAddressFromName(u.user_map1.get("name"), "faa")
user_faa2 = CmdUtils.getAddressFromName(u.user_map2.get("name"), "faa")
user_faa3 = CmdUtils.getAddressFromName(u.user_map3.get("name"), "faa")
name1 = u.user_map1.get("name")
name2 = u.user_map2.get("name")
name3 = u.user_map3.get("name")

response = CmdUtils.sendRequest('cmd/CmdWithOneArgs', u.command , "sync")
println response.responseBodyContent

WS.verifyEqual(response.responseBodyContent.contains(user_faa1), true)
WS.verifyEqual(response.responseBodyContent.contains(user_faa2), true)
WS.verifyEqual(response.responseBodyContent.contains(user_faa3), true)
WS.verifyEqual(response.responseBodyContent.contains(name1), true)
WS.verifyEqual(response.responseBodyContent.contains(name2), true)
WS.verifyEqual(response.responseBodyContent.contains(name3), true)
/*	//CmdUtils.pl(response.responseBodyContent)
 * utils
 */

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
		
		command = 'iriscli keys list'
	}
}