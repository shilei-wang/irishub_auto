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
import com.kms.katalon.core.annotation.Keyword
import com.kms.katalon.core.checkpoint.Checkpoint as Checkpoint

import internal.GlobalVariable as GlobalVariable
import utils.CmdUtils as CmdUtils
import cmd.ServiceUtils as ServiceUtils
import utils.StringUtils as StringUtils

import com.google.gson.JsonObject as JsonObject;


Utils u = new Utils();

//case 1 : 对已经绑定的service进行再次绑定。
serviceName = ServiceUtils.defineService(u.v0)
ServiceUtils.bindService(u.v0, serviceName)
response = u.bindService(u.v0, serviceName)
WS.verifyEqual(StringUtils.stringContains(response.responseBodyContent,u.td.getValue("cmd_result", 1)), true)

//case 2 : 对未定义的服务执行绑定命令。
response = u.bindService(u.v0, "test_bind")
WS.verifyEqual(StringUtils.stringContains(response.responseBodyContent,u.td.getValue("cmd_result", 2)), true)

//case 3 ->  case 14
serviceName = ServiceUtils.defineService(u.v0)
for (int i = 3; i <= u.td.getRowNumbers(); i++) {
	command = CmdUtils.generateCmd(u.command, findTestData('service/bind/bind/IRISHUB-727'), i)
	command = command.concat(' --service-name=').concat(serviceName)
	response = CmdUtils.sendRequest('cmd/CmdWithOneArgs',command, "wait")
	WS.verifyEqual(StringUtils.stringContains(response.responseBodyContent,u.td.getValue("cmd_result", i)), true)
	
	//	if (!StringUtils.stringContains(response.responseBodyContent,u.td.getValue("cmd_result", i))){
	//		CmdUtils.printLog(response.responseBodyContent)
	//	}
}


/*
 * utils
 */

class Utils {
	public TestData td;
	public String command;
	public String v0;
	
	public Utils(){
		td = findTestData('service/bind/bind/IRISHUB-727')
		TestData faucet = findTestData('base/faucet')
		v0 = faucet.getValue('name', 1)
		
		command = 'iriscli service bind --chain-id='.concat(GlobalVariable.chainId).concat(' --def-chain-id=').concat(GlobalVariable.chainId).concat(' --node=').concat(GlobalVariable.node).concat(
			' --from=').concat(v0)
	    command = CmdUtils.addTxFee(command, findTestData('base/tx'), 1)
	}
	
	@Keyword
	public ResponseObject bindService(String account, String serviceName){
		String cmd = 'iriscli service bind --chain-id='.concat(GlobalVariable.chainId).concat(' --def-chain-id=').concat(GlobalVariable.chainId).concat(' --node=').concat(GlobalVariable.node).concat(
				' --from=').concat(account)
		cmd = CmdUtils.generateCmd(cmd, findTestData('service/bind/bind/IRISHUB-726'), 1)
		cmd = CmdUtils.addTxFee(cmd, findTestData('base/tx'), 1)
		cmd = cmd.concat(' --service-name=').concat(serviceName)

		ResponseObject response = CmdUtils.sendRequest('cmd/CmdWithOneArgs',cmd, "wait")
		return response
	}
}

