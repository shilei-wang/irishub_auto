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
import cmd.GuardianUtils as GuardianUtils
import cmd.DistributionUtils as DistributionUtils

Utils u = new Utils();

// 抵押委托给自己（验证余额时候扣除）

cmd = CmdUtils.generateCmd(u.command, u.td, 1)
cmd = cmd+" --address-validator="+u.v0_fva+" --from="+u.v0
response = CmdUtils.sendRequest('cmd/CmdWithOneArgs', cmd, "wait")
WS.verifyEqual(StringUtils.stringContains(response.responseBodyContent,"tx hash"), true)

// 抵押委托给他人
balance_before = CmdUtils.getBalance(u.user_faa, "faa")

cmd = CmdUtils.generateCmd(u.command, u.td, 2)
cmd = cmd+" --address-validator="+u.v1_fva+" --from="+u.user
response = CmdUtils.sendRequest('cmd/CmdWithOneArgs', cmd, "wait")
WS.verifyEqual(StringUtils.stringContains(response.responseBodyContent,"tx hash"), true)

CmdUtils.waitUntilSeveralBlock(3)
balance_after = CmdUtils.getBalance(u.user_faa, "faa")
actual = balance_before - balance_after
expected = Double.valueOf(u.td.getValue("amount", 2).replace("iris", ""))

CmdUtils.pl(balance_before)
CmdUtils.pl(balance_after)
CmdUtils.pl(expected)
WS.verifyEqual(CmdUtils.compareIgnoreFee(actual, expected), true)



/*	//CmdUtils.pl(response.responseBodyContent)
 * utils
 */

class Utils {
	public TestData td;
	public String command;
	public String v0;
	public String v1;
	public String v0_fva;
	public String v1_fva;
	public String user;
	public String user_faa;
	
	public Utils(){
		td = findTestData('stake/delegate/IRISHUB-895')
		TestData faucet = findTestData('base/faucet')
		v0 = faucet.getValue('name', 1)
		v1 = faucet.getValue('name', 2)
		v0_fva = CmdUtils.getAddressFromName(v0, "fva")
		v1_fva = CmdUtils.getAddressFromName(v1, "fva")
		
		//前面的slash，unbond等操作会引起 分发reward给validators 所以用独立的delegator测试
		user = CmdUtils.createNewAccount(v0,"200iris")
		user_faa = CmdUtils.getAddressFromName(user, "faa")
		
		command = 'iriscli stake delegate'.concat(GlobalVariable.chainId).concat(GlobalVariable.node)
		command = CmdUtils.addTxFee(command, findTestData('base/tx'), 1)
	}
}
