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
//默认service price =1iris ， 2% tax= 0.02iris， 取0.01iris
expected = Double.valueOf(ServiceUtils.fees(u.v0, "incoming_fee").replace("iris", ""))
response = CmdUtils.sendRequest('cmd/CmdWithOneArgs', u.command, "wait")
WS.verifyEqual(StringUtils.stringContains(response.responseBodyContent,"tx hash"), true)
balance = CmdUtils.getBalance(u.user_faa, "faa")
WS.verifyEqual(balance, 0.01)

/* //CmdUtils.pl(u.command)
 * utils
 */

class Utils {
	public String command;
	public String serviceName;
	public String v0;
	public String user;
	public String user_faa;
	public String request_id;
	
	public Utils(){
		TestData faucet = findTestData('base/faucet')
		v0   = faucet.getValue('name', 1)
		user = CmdUtils.createNewAccount(v0, "0iris")
		user_faa = CmdUtils.getAddressFromName(user, "faa")

		serviceName = ServiceUtils.defineService(v0)
		ServiceUtils.bindService(v0, serviceName)	
		request_id = ServiceUtils.call(v0, v0, serviceName)
		ServiceUtils.respond(v0, request_id)
		
		command = 'iriscli service withdraw-tax'.concat(GlobalVariable.chainId).concat(GlobalVariable.node)
				.concat(' --from=').concat(v0).concat(' --dest-address=').concat(user_faa).concat(' --withdraw-amount=').concat("0.01iris")
		command = CmdUtils.addTxFee(command, findTestData('base/tx'), 1)
	}
}
