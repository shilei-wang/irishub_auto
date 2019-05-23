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
	proposal_id = GovUtils.submitProposal(u.v0, u.td.getValue("submit_deposit", i))
	cmd = u.command+" --deposit="+u.td.getValue("deposit", i)+" --proposal-id="+proposal_id
	response = CmdUtils.sendRequest('cmd/CmdWithOneArgs', cmd, "wait")
	WS.verifyEqual(StringUtils.stringContains(response.responseBodyContent,"tx hash"), true)
	status = GovUtils.getProposalStatus(proposal_id)
	WS.verifyEqual(u.td.getValue("cmd_result", i), status)
}


/*	//CmdUtils.pl(response.responseBodyContent)
 * utils
 */

class Utils {
	public TestData td;
	public String command;
	public String v0;
	
	public Utils(){
		td = findTestData('gov/deposit/IRISHUB-914')
		TestData faucet = findTestData('base/faucet')
		v0 = faucet.getValue('name', 1)
		
		command = 'iriscli gov deposit'.concat(GlobalVariable.chainId).concat(GlobalVariable.node).concat(' --from='+v0)
		command = CmdUtils.addTxFee(command, findTestData('base/tx'), 1)
	}
}

