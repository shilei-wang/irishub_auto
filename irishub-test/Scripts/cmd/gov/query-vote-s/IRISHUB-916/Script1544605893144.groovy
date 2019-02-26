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

//根据 voter， proposal-id 执行 query-vote 对投票进行查询
i=1
cmd = u.command[i] + " --voter="+ u.v0_faa
response = CmdUtils.sendRequest('cmd/CmdWithOneArgs', cmd, "sync")
WS.verifyEqual(StringUtils.getValueFromKey(response.responseBodyContent, "option"), u.td.getValue("v0_option", 1))

cmd = u.command[i] + " --voter="+ u.v1_faa
response = CmdUtils.sendRequest('cmd/CmdWithOneArgs', cmd, "sync")
WS.verifyEqual(StringUtils.getValueFromKey(response.responseBodyContent, "option"), u.td.getValue("v1_option", 1))

//根据 proposal-id 执行 query-votes 对投票进行查询
i=2
response = CmdUtils.sendRequest('cmd/CmdWithOneArgs', u.command[i], "sync")
vote_array = CmdUtils.ParseArray(response.responseBodyContent)

for (int i = 0; i < vote_array.size() ; i++) {
	vote_item = vote_array.get(i).getAsJsonObject()
	voter = StringUtils.findValueInMap(u.account_map, vote_item.get("voter").getAsString())
	WS.verifyEqual(vote_item.get("option").getAsString(), StringUtils.findValueInMap(u.vote_map, voter))
	CmdUtils.pl(StringUtils.findValueInMap(u.vote_map, voter))
	CmdUtils.pl(vote_item.get("option").getAsString())
}


/*	//CmdUtils.pl(response.responseBodyContent)
 * utils
 */

class Utils {
	public TestData td;
	public String[] command=new String[3];
	public String v0;
	public String v1;
	public String v0_faa;
	public String v1_faa;
	public String proposal_id;
	public Map<String, String> account_map = new HashMap<String, String>();
	public Map<String, String> vote_map = new HashMap<String, String>();
	
	public Utils(){
		td = findTestData('gov/query-vote-s/IRISHUB-916')
		TestData faucet = findTestData('base/faucet')
		v0 = faucet.getValue('name', 1)
		v1 = faucet.getValue('name', 2)
		v0_faa = CmdUtils.getAddressFromName(v0,"faa")
		v1_faa = CmdUtils.getAddressFromName(v1,"faa")
		
		account_map.put(v0_faa,v0);
		account_map.put(v1_faa,v1);
		
		vote_map.put(v0, td.getValue("v0_option", 1));
		vote_map.put(v1, td.getValue("v1_option", 1));				
		
		proposal_id = GovUtils.submitProposal(v0, "Text", "1000iris")
		GovUtils.voteProposal(v0, proposal_id, td.getValue("v0_option", 1))
		GovUtils.voteProposal(v1, proposal_id, td.getValue("v1_option", 1))
		
		command[1] = 'iriscli gov query-vote'.concat(' --node=').concat(GlobalVariable.node)+" --proposal-id="+proposal_id
		command[2] = 'iriscli gov query-votes'.concat(' --node=').concat(GlobalVariable.node)+" --proposal-id="+proposal_id
	}
}

