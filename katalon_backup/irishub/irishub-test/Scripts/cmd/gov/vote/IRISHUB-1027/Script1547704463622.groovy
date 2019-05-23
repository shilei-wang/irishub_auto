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
import cmd.StakeUtils as StakeUtils
import com.google.gson.JsonObject as JsonObject;
	
Utils u = new Utils();

//1. 使用非验证人进行投票
proposal_id = GovUtils.submitProposal(u.v0, "1000iris")
cmd = u.command+" --from="+u.user+" --option=yes"+" --proposal-id="+proposal_id
response = CmdUtils.sendRequest('cmd/CmdWithOneArgs', cmd, "wait")
WS.verifyEqual(StringUtils.stringContains(response.responseBodyContent,"isn't a validator"), true)


//2. 对提议进行重复投票
proposal_id = GovUtils.submitProposal(u.v0, "1000iris")
cmd = u.command+" --from="+u.v0+" --option=yes"+" --proposal-id="+proposal_id
response = CmdUtils.sendRequest('cmd/CmdWithOneArgs', cmd, "wait")
WS.verifyEqual(StringUtils.stringContains(response.responseBodyContent,"tx hash"), true)

cmd = u.command+" --from="+u.v0+" --option=yes"+" --proposal-id="+proposal_id
response = CmdUtils.sendRequest('cmd/CmdWithOneArgs', cmd, "wait")
WS.verifyEqual(StringUtils.stringContains(response.responseBodyContent,"has voted for the proposal"), true)

//等待一个voting period，避免下个用例出错
sleep(u.VotingPeriod)
//3. 验证人不投票 被slash 5%
// 由于要改genesis 此处建议手动测试.
// 如果把slash打开 会显著增加autotest的复杂度和准确度


/*	//CmdUtils.pl(response.responseBodyContent)
 * utils
 */

class Utils {
	public TestData td;
	public String command;
	public String v0;
	public String v0_faa;
	public String v1;
	public String user;
	public int VotingPeriod = 12000;
	
	public Utils(){
		td = findTestData('gov/vote/IRISHUB-915')
		TestData faucet = findTestData('base/faucet')
		v0 = faucet.getValue('name', 1)
		v1 = faucet.getValue('name', 2)
		v0_faa = CmdUtils.getAddressFromName(v0, "faa")		
		user = CmdUtils.createNewAccount(v0,"1iris")

		command = 'iriscli gov vote'.concat(GlobalVariable.chainId).concat(GlobalVariable.node)
		command = CmdUtils.addTxFee(command, findTestData('base/tx'), 1)
	}
}

