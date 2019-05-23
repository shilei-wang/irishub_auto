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
import com.kms.katalon.core.webservice.keyword.WSBuiltInKeywords as WS
import com.kms.katalon.core.webui.keyword.WebUiBuiltInKeywords as WebUI
import internal.GlobalVariable as GlobalVariable

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


//提交一个提议（抵押少于最低抵押金额） 根据proposal id， query-proposal  查询已经提交的提议（检查提议状态）
i = 1
proposal_id = GovUtils.submitProposal(u.v0, u.td, i)
cmd = u.command[i] + " --proposal-id="+proposal_id
response = CmdUtils.sendRequest('cmd/CmdWithOneArgs', cmd, "sync")

re = CmdUtils.Parse(response.responseBodyContent).get("value").getAsJsonObject().get("BasicProposal").getAsJsonObject()
WS.verifyEqual(re.get("proposal_id").getAsString(), proposal_id)
WS.verifyEqual(re.get("title").getAsString(), u.td.getValue("title", i))
WS.verifyEqual(re.get("description").getAsString(), u.td.getValue("description", i))
WS.verifyEqual(re.get("proposal_status").getAsString(), u.td.getValue("cmd_result", i))

deposit_array = re.get("total_deposit").getAsJsonArray()
actual   = Double.valueOf(CmdUtils.IrisattoToIris(deposit_array.get(0).getAsJsonObject().get("amount").getAsString()).replace("iris", ""))
expected = Double.valueOf(u.td.getValue("deposit", i).replace("iris", ""))
WS.verifyEqual(actual,expected)


//提交一个提议（抵押大于最低抵押金额） 根据proposal id， query-proposals 查询已经提交的提议
i = 2
proposal_id = GovUtils.submitProposal(u.v0, u.td, i)
response = CmdUtils.sendRequest('cmd/CmdWithOneArgs', u.command[i], "wait")
WS.verifyEqual(StringUtils.stringContains(response.responseBodyContent,proposal_id), true)
WS.verifyEqual(StringUtils.stringContains(response.responseBodyContent,u.td.getValue("title", i)), true)


/*	//CmdUtils.pl(response.responseBodyContent)
 * utils
 */

class Utils {
	public TestData td;
	public String[] command=new String[3];
	public String v0;

	
	public Utils(){
		td = findTestData('gov/query-proposal-s/IRISHUB-913')
		TestData faucet = findTestData('base/faucet')
		v0 = faucet.getValue('name', 1)
		
		command[1] = 'iriscli gov query-proposal '.concat(GlobalVariable.chainId).concat(GlobalVariable.node).concat(GlobalVariable.json)
		command[2] = 'iriscli gov query-proposals --limit=1'.concat(GlobalVariable.chainId).concat(GlobalVariable.node).concat(GlobalVariable.json)
	}
}

