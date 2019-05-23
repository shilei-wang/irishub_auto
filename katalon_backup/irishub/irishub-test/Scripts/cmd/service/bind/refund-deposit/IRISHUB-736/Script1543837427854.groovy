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

//主网版本不测 refundDeposit
if (!CmdUtils.isMainnet()) {
	//取回抵押
	balance_before = CmdUtils.getBalance(u.v0, "name")
	response = CmdUtils.sendRequest('cmd/CmdWithOneArgs', u.command, "wait")
	WS.verifyEqual(StringUtils.stringContains(response.responseBodyContent,"tx hash"), true)
	balance_after = CmdUtils.getBalance(u.v0, "name")
	
	//检查 refundDeposit 是否到账
	actual = balance_after-balance_before
	expected = Double.valueOf(u.td.getValue("deposit", 1).replace("iris", ""))
	//WS.verifyEqual(CmdUtils.compareIgnoreFee(actual, expected), true)
	//CmdUtils.pl(actual)
	//CmdUtils.pl(expected)
}


/* //CmdUtils.pl(u.command)
 * utils
 */

class Utils {
	public TestData td;
	public String command;
	public String serviceName;
	public String v0;
	
	public Utils(){
		td = findTestData('service/bind/refund-deposit/IRISHUB-736')
		TestData faucet = findTestData('base/faucet')
		v0 = faucet.getValue('name', 1)
		
		serviceName = ServiceUtils.defineService(v0)
		ServiceUtils.bindService(v0, serviceName)
		ServiceUtils.disableService(v0, serviceName)
		
		command = 'iriscli service refund-deposit'.concat(GlobalVariable.chainId).concat(GlobalVariable.defchainId).concat(GlobalVariable.node)
		.concat(' --from=').concat(v0)+" --service-name="+serviceName
		command = CmdUtils.addTxFee(command, findTestData('base/tx'), 1)
	}
}
