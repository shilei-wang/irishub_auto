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

//case 1 : 对已经绑定的service进行再次绑定。
response = u.defineService()
WS.verifyEqual(StringUtils.stringContains(response.responseBodyContent,"tx hash"), true)
response = u.bindService()
WS.verifyEqual(StringUtils.stringContains(response.responseBodyContent,"tx hash"), true)
response = u.bindService()
WS.verifyEqual(StringUtils.stringContains(response.responseBodyContent,u.td.getValue("cmd_result", 1)), true)

//case 2 : 对未定义的服务执行绑定命令。
u.serviceName = "test_bind"
response = u.bindService()
WS.verifyEqual(StringUtils.stringContains(response.responseBodyContent,u.td.getValue("cmd_result", 2)), true)

//case 3 ->  case 14
response = u.defineService()
WS.verifyEqual(StringUtils.stringContains(response.responseBodyContent,"tx hash"), true)

for (int i = 3; i <= u.td.getRowNumbers(); i++) {
	command = CmdUtils.generateCmd(u.command, findTestData('service/bind/IRISHUB-727'), i)
	command = command.concat(' --service-name=').concat(u.serviceName)
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
	public String serviceName;
	public String name;
	
	public Utils(){
		td = findTestData('service/bind/IRISHUB-727')
		TestData faucet = findTestData('base/faucet')
		name = faucet.getValue('name', 1)
		
		command = 'iriscli service bind --chain-id='.concat(GlobalVariable.chainId).concat(' --def-chain-id=').concat(GlobalVariable.chainId).concat(' --node=').concat(GlobalVariable.node).concat(
			' --from=').concat(name)
	    command = CmdUtils.addTxFee(command, findTestData('base/tx'), 1)
	}
	
	public ResponseObject defineService(){
		String define_command = 'iriscli service define --chain-id='.concat(GlobalVariable.chainId).concat(' --node=').concat(GlobalVariable.node).concat(
				' --from=').concat(name)
		define_command = CmdUtils.addTxFee(define_command, findTestData('base/tx'), 1)
				
		define_command = CmdUtils.generateCmd(define_command, findTestData('service/define/IRISHUB-624'), 1)
		define_command = UseRandomServiceName(define_command)
		ResponseObject response = CmdUtils.sendRequest('cmd/CmdWithOneArgs', define_command, "wait")
		
		return response
		//WS.verifyEqual(StringUtils.stringContains(response.responseBodyContent,"tx hash"), true)
		//CmdUtils.printLog(response.responseBodyContent)
	}
	
	public bindService(){
		String bind_command = 'iriscli service bind --chain-id='.concat(GlobalVariable.chainId).concat(' --def-chain-id=').concat(GlobalVariable.chainId).concat(' --node=').concat(GlobalVariable.node).concat(
				' --from=').concat(name)
		bind_command = CmdUtils.addTxFee(bind_command, findTestData('base/tx'), 1)		
		
		bind_command = bind_command.concat(' --service-name=').concat(serviceName)
		bind_command = CmdUtils.generateCmd(bind_command, findTestData('service/bind/IRISHUB-726'), 1)
		ResponseObject response = CmdUtils.sendRequest('cmd/CmdWithOneArgs',bind_command, "wait")
		
		return response
		//WS.verifyEqual(StringUtils.stringContains(response.responseBodyContent,"tx hash"), true)
		//CmdUtils.printLog(response.responseBodyContent)
	}
	
	public String UseRandomServiceName(String cmd){
		String RandomID =  CmdUtils.generateRandomID()
		serviceName =  "001_service_"+RandomID
		return cmd.replace("001_service", serviceName)
	}
}

