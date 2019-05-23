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

//在有收益的情况下， 服务提供者取回自己的服务收益。
balance_before = CmdUtils.getBalance(u.v0, "name")
expected = Double.valueOf(ServiceUtils.fees(u.v0, "incoming_fee").replace("iris", ""))
response = CmdUtils.sendRequest('cmd/CmdWithOneArgs', u.command, "wait")
WS.verifyEqual(StringUtils.stringContains(response.responseBodyContent,"tx hash"), true)
balance_after = CmdUtils.getBalance(u.v0, "name")
actual = balance_after - balance_before
WS.verifyEqual(CmdUtils.compareIgnoreFee(actual, expected), true)

/* //CmdUtils.pl(u.command)
 * utils
 */

class Utils {
	public String command;
	public String serviceName;
	public String v0;
	public String request_id;
	
	public Utils(){
		TestData faucet = findTestData('base/faucet')
		v0 = faucet.getValue('name', 1)

		serviceName = ServiceUtils.defineService(v0)
		ServiceUtils.bindService(v0, serviceName)	
		request_id = ServiceUtils.call(v0, v0, serviceName)
		ServiceUtils.respond(v0, request_id)
		
		command = 'iriscli service withdraw-fees'.concat(GlobalVariable.chainId).concat(GlobalVariable.node)
				.concat(' --from='+v0)
		command = CmdUtils.addTxFee(command, findTestData('base/tx'), 1)
	}
}
