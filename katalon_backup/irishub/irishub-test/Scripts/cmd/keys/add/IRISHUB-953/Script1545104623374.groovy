import static com.kms.katalon.core.checkpoint.CheckpointFactory.findCheckpoint
import static com.kms.katalon.core.testcase.TestCaseFactory.findTestCase
import static com.kms.katalon.core.testdata.TestDataFactory.findTestData
import static com.kms.katalon.core.testobject.ObjectRepository.findTestObject

import java.util.Map

import org.junit.After

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


String name1 = "user_"+ CmdUtils.generateRandomID()
Utils u = new Utils()
String cmd1 = u.command+name1
response = CmdUtils.sendRequest('cmd/CmdWithOneArgs', cmd1, u.password, "sync")
println response.responseBodyContent
WS.verifyEqual(StringUtils.stringContains(response.responseBodyContent, name1), true)
WS.verifyEqual(KeysUtils.verifyKeyInlist(name1), true)


//case 1
KeysUtils.delete(name1, u.password)
WS.verifyEqual(KeysUtils.verifyKeyInlist(name1), false)


//case 2
response = CmdUtils.sendRequest('cmd/CmdWithOneArgs', cmd1, u.password, "sync")
println response.responseBodyContent
WS.verifyEqual(StringUtils.stringContains(response.responseBodyContent, name1), true)
WS.verifyEqual(KeysUtils.verifyKeyInlist(name1), true)
user1_faa = CmdUtils.getAddressFromName(name1, "faa")

response = CmdUtils.sendRequest('cmd/CmdWithTwoArgs', cmd1, "y", u.password, "sync")
println response.responseBodyContent
user1_faa_change = CmdUtils.getAddressFromName(name1, "faa")

WS.verifyEqual(user1_faa_change.equals(user1_faa), false)

//case 3
response = CmdUtils.sendRequest('cmd/CmdWithTwoArgs', cmd1, "n", "sync")
println response.responseBodyContent
user1_faa_change2 = CmdUtils.getAddressFromName(name1, "faa")
WS.verifyEqual(user1_faa_change.equals(user1_faa_change2), true)

//case 4

String name2 = "user_"+ CmdUtils.generateRandomID()
String cmd2 = u.command+name2
response = CmdUtils.sendRequest('cmd/CmdWithOneArgs', cmd2, "123456789", "sync")
println response.responseBodyContent
WS.verifyEqual(StringUtils.stringContains(response.responseBodyContent, name2), true)
WS.verifyEqual(KeysUtils.verifyKeyInlist(name1), true)


class Utils {
	public String command
	public String password

	public Utils(){
		password = findTestData('base/faucet').getValue('password', 1)
		command ="iriscli keys add "
	}
}