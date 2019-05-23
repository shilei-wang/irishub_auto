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
import com.kms.katalon.core.testobject.ResponseObject as ResponseObject
import com.kms.katalon.core.webservice.keyword.WSBuiltInKeywords as WS
import com.kms.katalon.core.webui.keyword.WebUiBuiltInKeywords as WebUI


import internal.GlobalVariable as GlobalVariable
import utils.CmdUtils
import utils.StringUtils as StringUtils
import cmd.KeysUtils as KeysUtils


Utils u = new Utils();

//case 1
String name = "user_"+ CmdUtils.generateRandomID()
//name = KeysUtils.recover(u.password, u.user_map.get("mnemonic"))
String cmd1 = u.command + name
response = CmdUtils.sendRequest('cmd/CmdWithTwoArgs', cmd1,  u.password,  u.user_map.get("mnemonic"), "sync")
println response.responseBodyContent
WS.verifyEqual(StringUtils.stringContains(response.responseBodyContent, name), true)
u.faa_after = CmdUtils.getAddressFromName(name, "faa")
WS.verifyEqual(u.faa_before, u.faa_after)


//case 2 
Map<String, String> user_map2
user_map2 = KeysUtils.add(u.password)
faa_before = CmdUtils.getAddressFromName(user_map2.get("name"), "faa")
String cmd2 = "iriscli keys add --recover " + user_map2.get("name")
response = CmdUtils.sendRequest('cmd/CmdWithThreeArgs', cmd2,  "y",  u.password, user_map2.get("mnemonic"),"sync")
CmdUtils.pl(response.responseBodyContent)
faa_after = CmdUtils.getAddressFromName(user_map2.get("name"), "faa")
println faa_before
println faa_after
WS.verifyEqual(faa_before, faa_after)


//case 3
KeysUtils.delete(u.user_map.get("name"), u.password)
String cmd3 = u.command + u.user_map.get("name")
ResponseObject response = CmdUtils.sendRequest('cmd/CmdWithTwoArgs', cmd3,  u.password,  u.user_map.get("mnemonic"), "sync")
CmdUtils.pl(response.responseBodyContent)
WS.verifyEqual(StringUtils.stringContains(response.responseBodyContent, u.user_map.get("name")), true)
faa_after = CmdUtils.getAddressFromName(name, "faa")
WS.verifyEqual(u.faa_before, faa_after)



/*	//CmdUtils.pl(response.responseBodyContent)
 * utils
 */

class Utils {
	public String command
	public String password
	public Map<String, String> user_map
	public String faa_before
	public String faa_after
	
	public Utils(){
		password = findTestData('base/faucet').getValue('password', 1)
		user_map = KeysUtils.add(password)		
		faa_before = CmdUtils.getAddressFromName(user_map.get("name"), "faa")	

		command = 'iriscli keys add --recover '	
	}
}
