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
import com.kms.katalon.core.testobject.ResponseObject as ResponseObject
import com.kms.katalon.core.webservice.keyword.WSBuiltInKeywords as WS
import com.kms.katalon.core.webui.keyword.WebUiBuiltInKeywords as WebUI


import internal.GlobalVariable as GlobalVariable
import utils.CmdUtils
import utils.StringUtils as StringUtils


Utils u = new Utils();

// 给他人转账123iris（验证余额是否扣除，余额是否到账）
i=1
v0_balance_before = CmdUtils.getBalance(u.v0, "name")
v1_balance_before = CmdUtils.getBalance(u.v1, "name")
cmd = CmdUtils.generateCmd(u.command, u.td, i)
cmd = cmd+" --to="+u.v1_faa
response = CmdUtils.sendRequest('cmd/CmdWithOneArgs', cmd, "wait")
WS.verifyEqual(StringUtils.stringContains(response.responseBodyContent,"tx hash"), true)
v0_balance_after = CmdUtils.getBalance(u.v0, "name")
v1_balance_after = CmdUtils.getBalance(u.v1, "name")
v0_actual = v0_balance_before - v0_balance_after
v1_actual = v1_balance_after - v1_balance_before
expected = Double.valueOf(u.td.getValue("amount", i).replace("iris", ""))
WS.verifyEqual(CmdUtils.compareIgnoreFee(v0_actual, expected), true)
WS.verifyEqual(v1_actual, expected)


//给自己转账456iris-atto （验证是不是只扣除了fee）
i=2
v0_balance_before = CmdUtils.getBalance(u.v0, "name")
cmd = CmdUtils.generateCmd(u.command, u.td, i)
cmd = cmd+" --to="+u.v0_faa
response = CmdUtils.sendRequest('cmd/CmdWithOneArgs', cmd, "wait")
WS.verifyEqual(StringUtils.stringContains(response.responseBodyContent,"tx hash"), true)
v0_balance_after = CmdUtils.getBalance(u.v0, "name")
WS.verifyEqual(CmdUtils.compareIgnoreFee(v0_balance_before, v0_balance_after), true)

/*	//CmdUtils.pl(response.responseBodyContent)
 * utils
 */

class Utils {
	public TestData td;
	public String command;
	public String v0;
	public String v1;
	public String v0_faa;
	public String v1_faa;

	
	public Utils(){
		td = findTestData('bank/send/IRISHUB-967')
		TestData faucet = findTestData('base/faucet')
		v0 = faucet.getValue('name', 1)
		v1 = faucet.getValue('name', 2)
		v0_faa = CmdUtils.getAddressFromName(v0, "faa")
		v1_faa = CmdUtils.getAddressFromName(v1, "faa")
		
		command = 'iriscli bank send'.concat(GlobalVariable.chainId).concat(GlobalVariable.node).concat(' --from=').concat(v0)
		command = CmdUtils.addTxFee(command, findTestData('base/tx'), 1)
	}
}
