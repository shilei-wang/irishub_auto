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

for (int i = 1; i <= u.td.getRowNumbers(); i++) {
	u.Prepare(i)
	u.defineService()
	cmd = u.command.concat(' --service-name=').concat(u.serviceName)
	cmd = CmdUtils.generateCmd(cmd, u.td, i)
	response = CmdUtils.sendRequest('cmd/CmdWithOneArgs', cmd, 5000)
	WS.verifyEqual(StringUtils.stringContains(response.responseBodyContent,"tx hash"), true)
	//CmdUtils.printLog(response.responseBodyContent)
}


/*
 * utils
 */

class Utils {
	public TestData td;
	public String command;
	public String serviceName;
	
	public Utils(){
		td = findTestData('service/bind/IRISHUB-726')
	

		TestData faucet = findTestData('base/faucet')
		String name = faucet.getValue('name', 1)
		command = 'iriscli service bind --chain-id='.concat(GlobalVariable.chainId).concat(' --def-chain-id=').concat(GlobalVariable.chainId).concat(' --node=').concat(GlobalVariable.node).concat(
				' --from=').concat(name)
		command = CmdUtils.addTxFee(command, findTestData('base/tx'), 1)
	}
	
	public defineService(){
		TestData faucet = findTestData('base/faucet')
		String name = faucet.getValue('name', 1)
		String define_command = 'iriscli service define --chain-id='.concat(GlobalVariable.chainId).concat(' --node=').concat(GlobalVariable.node).concat(
				' --from=').concat(name)
		define_command = CmdUtils.addTxFee(define_command, findTestData('base/tx'), 1)
				
		define_command = CmdUtils.generateCmd(define_command, findTestData('service/define/IRISHUB-624'), 1)
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
	
	//JUST FOR　DEBUG
	public Prepare (int i){
		switch(i){
			case 1:
				println "--- TESTCASE 1 --- "
				break;
			case 2:
				println "--- TESTCASE 2 --- "
				break;
			case 3:
				println "--- TESTCASE 3 --- "
				break;
			case 4:
				println "--- TESTCASE 4 --- "
				break;
			}
	}
}

