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
import cmd.StakeUtils as StakeUtils
import com.google.gson.JsonObject as JsonObject;

Utils u = new Utils();

// 新创建的指定账户在v0处解绑委托（部分解绑，设置--shares-amount=总share的1/2）
balance_before = CmdUtils.getBalance(u.user, "name")
shares_before = StakeUtils.delegation(u.user,u.v0)
share = shares_before/2
cmd = u.command + " --shares-amount="+String.valueOf(share)
response = CmdUtils.sendRequest('cmd/CmdWithOneArgs', cmd, "wait")
WS.verifyEqual(StringUtils.stringContains(response.responseBodyContent,"tx hash"), true)
expected_banlance = StakeUtils.unbondingDelegation(u.user,u.v0)
CmdUtils.waitUntilSeveralBlock(3)
balance_after = CmdUtils.getBalance(u.user, "name")
shares_after = StakeUtils.delegation(u.user,u.v0)

expect = share
actual = shares_before - shares_after
WS.verifyEqual(actual, expect)

expect = expected_banlance
actual = balance_after - balance_before //这里需要修改 genesis-unbondtime
WS.verifyEqual(CmdUtils.compareIgnoreFee(actual, expect), true)

// 新创建的指定账户在v0处解绑委托（全部解绑，设置--shares-percent=1 实际解绑后续1/2）
balance_before = CmdUtils.getBalance(u.user, "name")
cmd = u.command + " --shares-percent=1"
response = CmdUtils.sendRequest('cmd/CmdWithOneArgs', cmd, "wait")
WS.verifyEqual(StringUtils.stringContains(response.responseBodyContent,"tx hash"), true)
expected_banlance = StakeUtils.unbondingDelegation(u.user,u.v0)
CmdUtils.waitUntilSeveralBlock(3)
balance_after = CmdUtils.getBalance(u.user, "name")
shares_after = StakeUtils.delegation(u.user,u.v0)

WS.verifyEqual(shares_after, 0) //全部取回 余额0shares

expect = expected_banlance
actual = balance_after - balance_before //这里需要修改 genesis-unbondtime
WS.verifyEqual(CmdUtils.compareIgnoreFee(actual, expect), true)


/*	//CmdUtils.pl(response.responseBodyContent)
 * utils
 */

class Utils {
	public String command;
	public String v0;
	public String v0_fva;
	public String user;
	public String user_faa;

	public Utils(){
		TestData faucet = findTestData('base/faucet')
		v0 = faucet.getValue('name', 1)
		v0_fva = CmdUtils.getAddressFromName(v0, "fva")	
		
		user = CmdUtils.createNewAccount(v0,"5.1iris")
		user_faa = CmdUtils.getAddressFromName(user, "faa")	
		StakeUtils.delegate(user, v0, "5iris")
		
		command = 'iriscli stake unbond begin --chain-id='.concat(GlobalVariable.chainId).concat(' --node=').concat(GlobalVariable.node).concat(
				' --from=').concat(user).concat(" --address-validator=").concat(v0_fva)
		command = CmdUtils.addTxFee(command, findTestData('base/tx'), 1)	
	}
}
