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
import cmd.GovUtils as GovUtils
import cmd.GuardianUtils as GuardianUtils
import utils.CmdUtils as CmdUtils
import utils.StringUtils as StringUtils


import com.google.gson.JsonObject as JsonObject;
	
Utils u = new Utils();


//type="TxTaxUsage" 抵押300iris. (达到初始抵押金额， 不到最小抵押金额)
//验证 ：   1. 提议进入deposit peroid
//  	  2. 抵押的300iris被从账户扣除
//        3. 等待deposit period （10s），验证300iris不会被退回
balance_before = CmdUtils.getBalance(u.user_faa, "faa")
cmd = u.command+" --usage=Burn --percent=0.0000000001 --type=TxTaxUsage --deposit=300iris"
response = CmdUtils.sendRequest('cmd/CmdWithOneArgs', cmd, "wait")
WS.verifyEqual(StringUtils.stringContains(response.responseBodyContent,"tx hash"), true)
status = GovUtils.getProposalStatus(CmdUtils.getValueFromTag(response.responseBodyContent, "proposal-id"))
WS.verifyEqual(status, "DepositPeriod")
balance_after = CmdUtils.getBalance(u.user_faa, "faa")

actual   = (balance_before - balance_after)
expected = 300
WS.verifyEqual(CmdUtils.compareIgnoreFee(actual, expected), true)

sleep(u.DepositPeriod)
balance_end = CmdUtils.getBalance(u.user_faa, "faa")
WS.verifyEqual(CmdUtils.compareIgnoreFee(balance_after, balance_end), true)


//type="TxTaxUsage" 抵押1000iris. (大于最小抵押金额，验证进入voting peroid)
//验证 ：   1. 验证进入voting peroid
//  	  2. 抵押的1000iris被从账户扣除
//        3. 等待voting period （10s），验证20% 200iris被扣除，80% 800iris被退回
balance_before = CmdUtils.getBalance(u.user_faa, "faa")
cmd = u.command+" --usage=Burn --percent=0.0000000001 --type=TxTaxUsage --deposit=1000iris"
response = CmdUtils.sendRequest('cmd/CmdWithOneArgs', cmd, "wait")
WS.verifyEqual(StringUtils.stringContains(response.responseBodyContent,"tx hash"), true)
status = GovUtils.getProposalStatus(CmdUtils.getValueFromTag(response.responseBodyContent, "proposal-id"))
WS.verifyEqual(status, "VotingPeriod")
balance_after = CmdUtils.getBalance(u.user_faa, "faa")

actual   = (balance_before - balance_after)
expected = 1000
WS.verifyEqual(CmdUtils.compareIgnoreFee(actual, expected), true)

sleep(u.VotingPeriod)
balance_end = CmdUtils.getBalance(u.user_faa, "faa")
actual   = (balance_end - balance_after)
expected = 1000*(1-0.2)
WS.verifyEqual(CmdUtils.compareIgnoreFee(actual, expected), true)


//type="ParameterChange"  抵押2000iris.(大于最小抵押金额，验证进入voting peroid)
//验证 ：   1. 验证进入voting peroid
cmd = u.command+" --param=auth/txSizeLimit=1000 --type=ParameterChange --deposit=2000iris"
response = CmdUtils.sendRequest('cmd/CmdWithOneArgs', cmd, "wait")
WS.verifyEqual(StringUtils.stringContains(response.responseBodyContent,"tx hash"), true)
status = GovUtils.getProposalStatus(CmdUtils.getValueFromTag(response.responseBodyContent, "proposal-id"))
WS.verifyEqual(status, "VotingPeriod")


//type="SoftwareUpgrade"   抵押1200iris.(达到初始抵押金额， 不到最小抵押金额)
//验证 ：    1. 提议进入deposit peroid
cmd = u.command+" --software=t --version=1 --switch-height=50000000 --type=SoftwareUpgrade --deposit=1200iris"
response = CmdUtils.sendRequest('cmd/CmdWithOneArgs', cmd, "wait")
WS.verifyEqual(StringUtils.stringContains(response.responseBodyContent,"tx hash"), true)
status = GovUtils.getProposalStatus(CmdUtils.getValueFromTag(response.responseBodyContent, "proposal-id"))
WS.verifyEqual(status, "DepositPeriod")

/*	//CmdUtils.pl(response.responseBodyContent)
 * utils
 */

class Utils {
	public TestData td;
	public String command;
	public String v0;
	public String user;
	public String user_faa;	
	public int DepositPeriod = 12000;
	public int VotingPeriod = 12000;
	
	public Utils(){
		if (CmdUtils.isMainnet()) {
			VotingPeriod = 22000
			DepositPeriod = 22000;
		}		
		
		td = findTestData('gov/submit-proposal/IRISHUB-911')
		TestData faucet = findTestData('base/faucet')
		v0 = faucet.getValue('name', 1)
		user = CmdUtils.createNewAccount(v0,"5000iris")
		user_faa = CmdUtils.getAddressFromName(user, "faa")
		GuardianUtils.addProfilers(v0, user_faa)
		
		command = 'iriscli gov submit-proposal --description=t --title=t'.concat(GlobalVariable.chainId).concat(GlobalVariable.node).concat(' --from='+user)
		command = CmdUtils.addTxFee(command, findTestData('base/tx'), 1)
	}
}

