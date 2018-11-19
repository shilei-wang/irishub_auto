package utils

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

Utils u = new Utils();

//验证第一个特别的case
u.Prepare(1)
cmd = CmdUtils.generateCmd(u.command, u.td, 1)
cmd = u.UseRandomServiceName(cmd) 
response = CmdUtils.sendRequest('cmd/CmdWithOneArgs', cmd, 5000)
WS.verifyResponseStatusCode(response, 200)
response = CmdUtils.sendRequest('cmd/CmdWithOneArgs', cmd, 5000)
if (WS.verifyResponseStatusCode(response, 400)) {
	WS.verifyEqual(StringUtils.stringContains(response.responseBodyContent,"already exist"), true)
}

//从第二个case开始遍历 
for (int i = 2; i <= u.td.getRowNumbers() ; i++) {
	u.Prepare(i)
	cmd = CmdUtils.generateCmd(u.command, u.td, i)	
	response = CmdUtils.sendRequest('cmd/CmdWithOneArgs', cmd, 0)
	if (WS.verifyResponseStatusCode(response, 400)) {
		WS.verifyEqual(StringUtils.stringContains(response.responseBodyContent,u.td.getValue("result", i)), true)
	}
}


/*
 * utils
 */

class Utils {	
	public TestData td;
	public String command;
	
	public Utils(){ 
		td = findTestData('service/define/IRISHUB-625')
		TestData faucet = findTestData('keys/faucet')
		String name = faucet.getValue('name', 1)
		command = 'iriscli service define --chain-id='.concat(GlobalVariable.chainId).concat(' --node=').concat(GlobalVariable.node).concat(
				' --from=').concat(name)
	}
	
	public String UseRandomServiceName(String cmd){
		String RandomID =  CmdUtils.generateRandomID()
		return cmd.replace("_service", "_service_"+RandomID)
	}
	
	//JUST FOR　
	public Prepare (int i){
		switch(i){
			case 1:
				println "--- TESTCASE 1 --- 验证重复service name"
				break;
			case 2:
				println "--- TESTCASE 2 --- 服务名称使用空字符串"
				break;
			case 3:			
				println "--- TESTCASE 3 --- file名称，idl都改为空，然后执行"
				break;
			case 4:
				println "--- TESTCASE 4 --- file填写错误路径，然后执行"
				break;
			case 5:
				println "--- TESTCASE 5 --- messaging  改为为空，然后执行"
				break;
			case 6:
				println "--- TESTCASE 6 --- messaging  改为为abc"
				break;
			}
	}

}


