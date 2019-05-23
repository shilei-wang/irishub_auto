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
response = CmdUtils.sendRequest('cmd/CmdWithOneArgs', u.command , "sync")
array = CmdUtils.ParseArray(response.responseBodyContent)

for (int i = 0; i < 2 ; i++) {
	user_faa = CmdUtils.getAddressFromName(u.user_maps[0].get("name"), "faa")
	
	index = CmdUtils.findItemIndex(array, "name", u.user_maps[0].get("name"))
	item = array.get(index).getAsJsonObject()
	WS.verifyEqual(item.get("address").getAsString(), user_faa)
}

/*	//CmdUtils.pl(response.responseBodyContent)
 * utils
 */

class Utils {
	public String command
	public String password
	//map数组
	public Map<String, String>[] user_maps=new Map<String, String>[3];
	
	public Utils(){
		password = findTestData('base/faucet').getValue('password', 1)		
		for (int i = 0; i < 2 ; i++) {
			user_maps[i] = KeysUtils.add(password)
		}
		
		command = 'iriscli keys list'.concat(GlobalVariable.json)
	}
}