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

//对未绑定的服务执行失效命令。
response = CmdUtils.sendRequest('cmd/CmdWithOneArgs', u.command, "wait")
WS.verifyEqual(StringUtils.stringContains(response.responseBodyContent, u.td.getValue("cmd_result", 1)), true)


//对已绑定，已失效的服务执行失效命令。
ServiceUtils.bindService(u.v0, u.serviceName)
ServiceUtils.disableService(u.v0, u.serviceName)
response = CmdUtils.sendRequest('cmd/CmdWithOneArgs', u.command, "wait")
WS.verifyEqual(StringUtils.stringContains(response.responseBodyContent, u.td.getValue("cmd_result", 2)), true)




/* //CmdUtils.pl(u.command)
 * utils
 */

class Utils {
	public TestData td;
	public String command;
	public String serviceName;
	public String v0;
	
	public Utils(){
		td = findTestData('service/bind/disable/IRISHUB-804')
		TestData faucet = findTestData('base/faucet')
		v0 = faucet.getValue('name', 1)
		
		serviceName = ServiceUtils.defineService(v0)
		
		command = 'iriscli service disable'.concat(GlobalVariable.chainId).concat(GlobalVariable.defchainId).concat(GlobalVariable.node)
			.concat(' --from='+v0)+" --service-name="+serviceName
		command = CmdUtils.addTxFee(command, findTestData('base/tx'), 1)
	}
}

