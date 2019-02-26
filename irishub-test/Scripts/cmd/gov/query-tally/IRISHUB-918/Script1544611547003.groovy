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

for (int i = 1; i <=2 ; i++) {	
	proposal_id = GovUtils.submitProposal(u.v0, "Text", "1000iris")
	
	u.votes_result[0]=u.td.getValue("v0_option", i)
	GovUtils.voteProposal(u.v0, proposal_id, u.votes_result[0])
	u.votes_result[1]=u.td.getValue("v1_option", i)
	GovUtils.voteProposal(u.v1, proposal_id, u.votes_result[1])
	
	cmd = u.command +" --proposal-id="+proposal_id	
	response = CmdUtils.sendRequest('cmd/CmdWithOneArgs', cmd , "sync")
	
	re = CmdUtils.Parse(response.responseBodyContent).getAsJsonObject()
	u.checkPower(re, "yes")
	u.checkPower(re, "no")
	u.checkPower(re, "abstain")
	u.checkPower(re, "no_with_veto")
	u.cleanPower()
}


/*	//CmdUtils.pl(response.responseBodyContent)
 * utils
 */

class Utils {
	public TestData td;
	public String command;
	public String v0;
	public String v1;
	public String[] votes_result=new String[4];
	
	public Utils(){
		td = findTestData('gov/query-tally/IRISHUB-918')
		TestData faucet = findTestData('base/faucet')
		v0 = faucet.getValue('name', 1)
		v1 = faucet.getValue('name', 2)
				
		command = 'iriscli gov query-tally'.concat(' --node=').concat(GlobalVariable.node)
	}
	
	public checkPower(JsonObject re, String type){
		Double power = Double.valueOf(re.get(type).getAsString())
		if (StringUtils.arrayContains(votes_result, type)){
			WS.verifyEqual((power>0), true)
		} else {
			WS.verifyEqual((power==0), true)
		}
	}
	
	public cleanPower(){
		for (int i = 0; i < votes_result.length ; i++) {
			votes_result[i] = ""			
		}
	}
}

