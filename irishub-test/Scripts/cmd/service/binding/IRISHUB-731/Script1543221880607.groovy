import com.kms.katalon.core.webui.keyword.WebUiBuiltInKeywords as WebUI
import com.kms.katalon.core.mobile.keyword.MobileBuiltInKeywords as Mobile
import com.kms.katalon.core.cucumber.keyword.CucumberBuiltinKeywords as CucumberKW
import com.kms.katalon.core.webservice.keyword.WSBuiltInKeywords as WS
import static com.kms.katalon.core.testobject.ObjectRepository.findTestObject
import static com.kms.katalon.core.testdata.TestDataFactory.findTestData
import static com.kms.katalon.core.testcase.TestCaseFactory.findTestCase
import static com.kms.katalon.core.checkpoint.CheckpointFactory.findCheckpoint
import com.kms.katalon.core.model.FailureHandling as FailureHandling
import com.kms.katalon.core.testcase.TestCase as TestCase
import com.kms.katalon.core.testdata.TestData as TestData
import com.kms.katalon.core.testobject.ResponseObject as ResponseObject
import com.kms.katalon.core.testobject.TestObject as TestObject
import com.kms.katalon.core.checkpoint.Checkpoint as Checkpoint
import internal.GlobalVariable as GlobalVariable
import utils.CmdUtils as CmdUtils
import utils.StringUtils as StringUtils

import com.google.gson.JsonObject as JsonObject;

Utils u = new Utils();

for (int i = 1; i <= u.td.getRowNumbers() ; i++) {
	command = u.command.concat(' --service-name=').concat(u.td.getValue("service-name", i))
	command = command.concat(' --provider=').concat(u.td.getValue("provider", i))
	response = CmdUtils.sendRequest('cmd/CmdWithOneArgs',command, "sync")
	WS.verifyEqual(StringUtils.stringContains(response.responseBodyContent, u.td.getValue("cmd_result", i)), true)
}

//CmdUtils.printLog(command)

/*
 * utils
 */

class Utils {
	public TestData td;
	public String command;
	public String serviceName;
	public String name;
	
	public Utils(){		
		td = findTestData('service/binding/IRISHUB-731')
		TestData faucet = findTestData('base/faucet')
		name = faucet.getValue('name', 1)
		
		command = 'iriscli service binding --def-chain-id='.concat(GlobalVariable.chainId).concat(' --bind-chain-id=').concat(GlobalVariable.chainId).concat(' --node=').concat(GlobalVariable.node)
	}	
}

