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

for (int i = 1; i <= u.td.getRowNumbers(); i++) {
	balance_before = CmdUtils.getBalance(u.v0, "name")
	cmd = CmdUtils.generateCmd(u.command, u.td, i)
	response = CmdUtils.sendRequest('cmd/CmdWithOneArgs', cmd, "wait")
	WS.verifyEqual(StringUtils.stringContains(response.responseBodyContent,"tx hash"), true)
	CmdUtils.pl(response.responseBodyContent)
	balance_after = CmdUtils.getBalance(u.v0, "name")
	actual = balance_before - balance_after
	expected = Double.valueOf(u.td.getValue("cmd_result", i).replace("iris", ""))
//	CmdUtils.pl(actual)
//	CmdUtils.pl(expected)
	WS.verifyEqual(CmdUtils.compareIgnoreFee(actual, expected), true)
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
		td = findTestData('service/invocation/call/IRISHUB-785')
		TestData faucet = findTestData('base/faucet')
		v0 = faucet.getValue('name', 1)
		String provider = CmdUtils.getAddressFromName(v0, "faa")

		serviceName = ServiceUtils.defineService(v0)
		ServiceUtils.bindService(v0, serviceName)
		ServiceUtils.updateBinding(v0, serviceName, "2iris,0iris", "1000iris")
		
		//CmdUtils.pl(ServiceUtils.getBindingInfo(v0, serviceName))
		
		command = 'iriscli service call --chain-id='.concat(GlobalVariable.chainId).concat(' --bind-chain-id=').concat(GlobalVariable.chainId).concat(' --def-chain-id=').concat(GlobalVariable.chainId).concat(' --node=').concat(GlobalVariable.node).concat(
				' --from=').concat(v0)+" --service-name="+serviceName+" --provider="+provider
		command = CmdUtils.addTxFee(command, findTestData('base/tx'), 1)
	}
}
