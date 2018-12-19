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
name = KeysUtils.recover(u.password, u.user_map.get("mnemonic"))
u.faa_after = CmdUtils.getAddressFromName(name, "faa")
WS.verifyEqual(u.faa_before, u.faa_after)
String cmd1 = u.command + name
ResponseObject response = CmdUtils.sendRequest('cmd/CmdWithTwoArgs', cmd1,  u.password,  u.user_map.get("mnemonic"), "sync")
WS.verifyEqual(StringUtils.stringContains(response.responseBodyContent, name), true)

//case 2 
user_map = KeysUtils.add(u.password)


//KeysUtils.update("a", "00000000", u.password)
//case 3
//name = KeysUtils.recover(u.password, u.user_map.get("mnemonic"))
//u.faa_after = CmdUtils.getAddressFromName(name, "faa")
//WS.verifyEqual(u.faa_before, u.faa_after)


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
				
		KeysUtils.delete(user_map.get("name"), password)		

		command = 'iriscli keys add --recover '	
	}
}
