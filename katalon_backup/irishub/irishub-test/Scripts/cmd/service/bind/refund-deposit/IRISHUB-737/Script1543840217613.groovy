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
import cmd.ServiceUtils as ServiceUtils
import com.google.gson.JsonObject as JsonObject;
	
Utils u = new Utils();

if (!CmdUtils.isMainnet()) {
	//对未绑定的服务执行取回抵押命令。
	cmd = u.command.replace(u.serviceName, "test")
	response = CmdUtils.sendRequest('cmd/CmdWithOneArgs', cmd, "wait")
	WS.verifyEqual(StringUtils.stringContains(response.responseBodyContent,u.td.getValue("cmd_result", 1)), true)
	
	//对非自己绑定的服务执行取回抵押命令。
	cmd = u.command.replace(u.v0, u.v1)
	response = CmdUtils.sendRequest('cmd/CmdWithOneArgs', cmd, "wait")
	WS.verifyEqual(StringUtils.stringContains(response.responseBodyContent,u.td.getValue("cmd_result", 2)), true)
	
	//对已绑定，未失效的服务执行取回抵押金额命令。
	cmd = u.command
	response = CmdUtils.sendRequest('cmd/CmdWithOneArgs', cmd, "wait")
	WS.verifyEqual(StringUtils.stringContains(response.responseBodyContent,u.td.getValue("cmd_result", 3)), true)
	
	//对已经取回抵押金额的service， 再次执行取回抵押。
	cmd = u.command
	ServiceUtils.disableService(u.v0, u.serviceName)
	response = CmdUtils.sendRequest('cmd/CmdWithOneArgs', cmd, "wait")
	WS.verifyEqual(StringUtils.stringContains(response.responseBodyContent,"tx hash"), true)
	response = CmdUtils.sendRequest('cmd/CmdWithOneArgs', cmd, "wait")
	WS.verifyEqual(StringUtils.stringContains(response.responseBodyContent,u.td.getValue("cmd_result", 4)), true)
}

/* //CmdUtils.pl(u.command)
 * utils
 */

class Utils {
	public TestData td;
	public String command;
	public String serviceName;
	public String v0;
	public String v1;
	
	public Utils(){
		td = findTestData('service/bind/refund-deposit/IRISHUB-737')
		TestData faucet = findTestData('base/faucet')
		v0 = faucet.getValue('name', 1)
		v1 = faucet.getValue('name', 2)
		
		serviceName = ServiceUtils.defineService(v0)
		ServiceUtils.bindService(v0, serviceName)		
		
		command = 'iriscli service refund-deposit'.concat(GlobalVariable.chainId).concat(GlobalVariable.defchainId).concat(GlobalVariable.node)
			.concat(' --from='+v0)+" --service-name="+serviceName
		command = CmdUtils.addTxFee(command, findTestData('base/tx'), 1)
	}
}
