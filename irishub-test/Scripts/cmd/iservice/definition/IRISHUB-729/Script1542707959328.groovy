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


//查询请求列表时，-def-chain-id、-service-name分别设置为空
command = u.command.concat(' --def-chain-id=').concat("").concat(' --service-name=').concat(u.serviceName)
response = CmdUtils.sendRequest('cmd/CmdWithOneArgs', command, 0)
if (WS.verifyResponseStatusCode(response, 400)) {
	WS.verifyEqual(StringUtils.stringContains(response.responseBodyContent,"not existed"), true)
}

command = u.command.concat(' --def-chain-id=').concat(GlobalVariable.chainId).concat(' --service-name=').concat("")
response = CmdUtils.sendRequest('cmd/CmdWithOneArgs', command, 0)
if (WS.verifyResponseStatusCode(response, 400)) {
	WS.verifyEqual(StringUtils.stringContains(response.responseBodyContent,"not existed"), true)
}

//查询一个未定义的service name
command = u.command.concat(' --def-chain-id=').concat(GlobalVariable.chainId).concat(' --service-name=').concat("ABC")
response = CmdUtils.sendRequest('cmd/CmdWithOneArgs', command, 0)
if (WS.verifyResponseStatusCode(response, 400)) {
	WS.verifyEqual(StringUtils.stringContains(response.responseBodyContent,"not existed"), true)
}

//指定一个不存在的def-chain-id进行查询
command = u.command.concat(' --def-chain-id=').concat("ABC").concat(' --service-name=').concat(u.serviceName)
response = CmdUtils.sendRequest('cmd/CmdWithOneArgs', command, 0)
if (WS.verifyResponseStatusCode(response, 400)) {
	WS.verifyEqual(StringUtils.stringContains(response.responseBodyContent,"not existed"), true)
}


/*
 * utils
 */

class Utils {
	public TestData td;
	public String command;
	public String serviceName;
	
	public Utils(){
		td = findTestData('service/definition/IRISHUB-729')
		
		//前提条件， 先定义一个service
		defineService()		

		command = 'iriscli service definition'
	}
	
	public defineService(){
		TestData faucet = findTestData('keys/faucet')
		String name = faucet.getValue('name', 1)
		String define_command = 'iriscli service define --chain-id='.concat(GlobalVariable.chainId).concat(' --node=').concat(GlobalVariable.node).concat(
				' --from=').concat(name)
				
		define_command = CmdUtils.generateCmd(define_command, td, 1)
		define_command = UseRandomServiceName(define_command)
		ResponseObject response = CmdUtils.sendRequest('cmd/CmdWithOneArgs', define_command, 5000)
		WS.verifyResponseStatusCode(response, 200)
		//CmdUtils.printLog(response.responseBodyContent)
	}
	
	public String UseRandomServiceName(String cmd){
		String RandomID =  CmdUtils.generateRandomID()
		serviceName =  "001_service_"+RandomID
		return cmd.replace("001_service", serviceName)
	}
	
}

