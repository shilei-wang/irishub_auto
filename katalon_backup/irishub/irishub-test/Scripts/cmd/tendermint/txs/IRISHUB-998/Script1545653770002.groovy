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
import cmd.ServiceUtils as ServiceUtils


Utils u = new Utils();

//测试bank-send
cmd ="iriscli bank send"+GlobalVariable.chainId+" --amount=1iris --from="+u.v0+" --to="+u.user_faa+GlobalVariable.node
cmd = CmdUtils.addTxFee(cmd, findTestData('base/tx'), 1)
response = CmdUtils.sendRequest('cmd/CmdWithOneArgs', cmd, "wait")
WS.verifyEqual(StringUtils.stringContains(response.responseBodyContent,"tx hash"), true)
TxHash = CmdUtils.getTxHash(response.responseBodyContent)
cmd = u.command + "action:send&recipient:"+u.user_faa+"&sender:"+u.v0_faa
response = CmdUtils.sendRequest('cmd/CmdWithOneArgs', cmd, "sync")
WS.verifyEqual(StringUtils.stringContains(response.responseBodyContent, TxHash), true)
WS.verifyEqual(StringUtils.stringContains(response.responseBodyContent,"irishub/bank/Send"), true)





//测试gov-submit-proposal
cmd = 'iriscli gov submit-proposal --title=t --description=t --type=TxTaxUsage --usage=Burn --percent=0.0000000001'.concat(GlobalVariable.chainId).concat(GlobalVariable.node).concat(' --from=').concat(u.v0)+" --deposit=300iris"
cmd = CmdUtils.addTxFee(cmd, findTestData('base/tx'), 1)
response = CmdUtils.sendRequest('cmd/CmdWithOneArgs', cmd, "wait")
WS.verifyEqual(StringUtils.stringContains(response.responseBodyContent,"tx hash"), true)
TxHash = CmdUtils.getTxHash(response.responseBodyContent)
proposal_id = CmdUtils.getValueFromTag(response.responseBodyContent, "proposal-id")
cmd = u.command + "action:submit_proposal&percent:0.0000000001&proposer:"+u.v0_faa+"&usage:Burn&proposal-id:"+proposal_id
response = CmdUtils.sendRequest('cmd/CmdWithOneArgs', cmd, "sync")
WS.verifyEqual(StringUtils.stringContains(response.responseBodyContent, TxHash), true)
WS.verifyEqual(StringUtils.stringContains(response.responseBodyContent,"irishub/gov/MsgSubmitTxTaxUsageProposal"), true)



/*	//CmdUtils.pl(response.responseBodyContent)
 * utils
 */

class Utils {
	public String command;
	public String v0;	
	public String v0_faa;
	public String v0_fva;
	public String user;
	public String user_faa;
	
	public Utils(){
		TestData faucet = findTestData('base/faucet')
		v0 = faucet.getValue('name', 1)
		v0_faa = CmdUtils.getAddressFromName(v0, "faa")
		v0_fva = CmdUtils.getAddressFromName(v0, "fva")
		user = CmdUtils.createNewAccount(v0, "1iris")
		user_faa = CmdUtils.getAddressFromName(user, "faa")

		command = 'iriscli tendermint txs --trust-node'.concat(GlobalVariable.chainId).concat(GlobalVariable.node).concat(' --tags ')
	}
}
