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
import utils.CmdUtils as CmdUtils
import utils.StringUtils as StringUtils

import com.google.gson.JsonObject as JsonObject;
	
Utils u = new Utils();

for (int i = 1; i <=u.td.getRowNumbers() ; i++) {	
	balance_before = CmdUtils.getBalance(u.v0_faa, "faa")
	proposal_id = GovUtils.submitProposal(u.v0, "1000iris")
	cmd = u.command+" --from="+u.v0+" --option="+u.td.getValue("v0_option", i)+" --proposal-id="+proposal_id
	response = CmdUtils.sendRequest('cmd/CmdWithOneArgs', cmd, "wait")
	WS.verifyEqual(StringUtils.stringContains(response.responseBodyContent,"tx hash"), true)
	
	cmd = u.command+" --from="+u.v1+" --option="+u.td.getValue("v1_option", i)+" --proposal-id="+proposal_id
	response = CmdUtils.sendRequest('cmd/CmdWithOneArgs', cmd, "wait")
	WS.verifyEqual(StringUtils.stringContains(response.responseBodyContent,"tx hash"), true)
	
	sleep(u.VotingPeriod)
	status = GovUtils.getProposalStatus(proposal_id)
	WS.verifyEqual(u.td.getValue("cmd_result", i), status)
	
	balance_after = CmdUtils.getBalance(u.v0_faa, "faa")
	rate = 0
	if (i!=3) {
		//扣除20%
		rate = 0.2
	} else {
		//全部扣除
		rate = 1
	}
	CmdUtils.pl(balance_before)
	CmdUtils.pl(balance_after)
	actual   = (balance_before - balance_after)
	expected = 1000*rate
	WS.verifyEqual(CmdUtils.compareIgnoreFee(actual, expected), true)
}


/*	//CmdUtils.pl(response.responseBodyContent)
 * utils
 */

class Utils {
	public TestData td;
	public String command;
	public String v0;
	public String v0_faa;
	public String v1;
	public int VotingPeriod = 12000;
	
	public Utils(){		
		if (CmdUtils.isMainnet()) {
			VotingPeriod = 25000
		}
		
		td = findTestData('gov/vote/IRISHUB-915')
		TestData faucet = findTestData('base/faucet')
		v0 = faucet.getValue('name', 1)
		v1 = faucet.getValue('name', 2)
		v0_faa = CmdUtils.getAddressFromName(v0, "faa")

		command = 'iriscli gov vote'.concat(GlobalVariable.chainId).concat(GlobalVariable.node)
		command = CmdUtils.addTxFee(command, findTestData('base/tx'), 1)
	}
}

