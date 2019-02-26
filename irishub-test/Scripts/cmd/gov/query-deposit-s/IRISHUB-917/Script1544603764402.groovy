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
	response = CmdUtils.sendRequest('cmd/CmdWithOneArgs', u.command[i], "wait")
	actual   = Double.valueOf(CmdUtils.IrisattoToIris(StringUtils.getValueFromKey(response.responseBodyContent, "amount")).replace("iris", ""))
	expected = Double.valueOf(u.td.getValue("cmd_result", 1).replace("iris", ""))
	WS.verifyEqual(actual,expected)
}

/*	//CmdUtils.pl(response.responseBodyContent)
 * utils
 */

class Utils {
	public TestData td;
	public String[] command=new String[3];
	public String v0;
	public String proposal_id;
	
	public Utils(){
		td = findTestData('gov/query-deposit-s/IRISHUB-917')
		TestData faucet = findTestData('base/faucet')
		v0 = faucet.getValue('name', 1)
		
		proposal_id = GovUtils.submitProposal(v0, "Text", td.getValue("submit_deposit", 1))
		GovUtils.depositProposal(v0, proposal_id, td.getValue("deposit", 1))

		command[1] = 'iriscli gov query-deposit'.concat(' --node=').
						concat(GlobalVariable.node)+" --proposal-id="+proposal_id+" --depositor="+CmdUtils.getAddressFromName(v0,"faa")
		command[2] = 'iriscli gov query-deposits'.concat(' --node=').concat(GlobalVariable.node)+" --proposal-id="+proposal_id
	}
}

