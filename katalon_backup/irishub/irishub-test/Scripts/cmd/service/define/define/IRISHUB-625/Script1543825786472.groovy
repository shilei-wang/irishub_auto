import static com.kms.katalon.core.checkpoint.CheckpointFactory.findCheckpoint
import static com.kms.katalon.core.testcase.TestCaseFactory.findTestCase
import static com.kms.katalon.core.testdata.TestDataFactory.findTestData
import static com.kms.katalon.core.testobject.ObjectRepository.findTestObject

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

import internal.GlobalVariable as GlobalVariable
import utils.CmdUtils as CmdUtils
import utils.StringUtils as StringUtils
import cmd.ServiceUtils as ServiceUtils
import com.google.gson.JsonObject as JsonObject;

Utils u = new Utils();

//验证第一个特别的case
serviceName = ServiceUtils.generateRandomServiceName()
cmd = CmdUtils.generateCmd(u.command, u.td, 1)
cmd = cmd.replace("001_service", serviceName)
CmdUtils.sendRequest('cmd/CmdWithOneArgs', cmd, "wait")
response = CmdUtils.sendRequest('cmd/CmdWithOneArgs', cmd, "wait")
WS.verifyEqual(StringUtils.stringContains(response.responseBodyContent,u.td.getValue("cmd_result", 1)), true)

//从第二个case开始遍历 
for (int i = 2; i <= u.td.getRowNumbers() ; i++) {
	cmd = CmdUtils.generateCmd(u.command, u.td, i)	
	response = CmdUtils.sendRequest('cmd/CmdWithOneArgs', cmd, "sync")
	WS.verifyEqual(StringUtils.stringContains(response.responseBodyContent,u.td.getValue("cmd_result", i)), true)
}

/*
 * utils
 */

class Utils {	
	public TestData td;
	public String command;
	
	public Utils(){ 
		td = findTestData('service/define/define/IRISHUB-625')
		TestData faucet = findTestData('base/faucet')
		String v0 = faucet.getValue('name', 1)
		command = 'iriscli service define'.concat(GlobalVariable.chainId).concat(GlobalVariable.node)
			.concat(' --from='+v0)
		command = CmdUtils.addTxFee(command, findTestData('base/tx'), 1)
	}
}


