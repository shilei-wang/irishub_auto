import static com.kms.katalon.core.checkpoint.CheckpointFactory.findCheckpoint
import static com.kms.katalon.core.testcase.TestCaseFactory.findTestCase
import static com.kms.katalon.core.testdata.TestDataFactory.findTestData
import static com.kms.katalon.core.testobject.ObjectRepository.findTestObject

import java.util.Map

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
import cmd.GuardianUtils as GuardianUtils
import cmd.GovUtils as GovUtils
import utils.CmdUtils as CmdUtils
import utils.StringUtils as StringUtils
	
/*
 * 如果遇到sequence问题 在两次proposal之间加sleep(1000)
 */
Utils u = new Utils();


//设置 --usage="Distribute" --percent=0.2  发起取回TxTaxUsage提议 (投yes票)
balance_before = CmdUtils.getBalance(u.user_faa, "faa")
cmd = u.command + " --usage=Distribute --percent=0.2"+ " --dest-address="+u.user_faa
response = CmdUtils.sendRequest('cmd/CmdWithOneArgs', cmd, "wait")
proposal_id = CmdUtils.getValueFromTag(response.responseBodyContent, "proposal-id")
GovUtils.voteProposal(u.v0, proposal_id, "yes")
GovUtils.voteProposal(u.v1, proposal_id, "yes")
sleep(u.VotingPeriod)
status = GovUtils.getProposalStatus(proposal_id)
WS.verifyEqual("Passed", status)
balance_after = CmdUtils.getBalance(u.user_faa, "faa")
Tax_1 = balance_after - balance_before
WS.verifyEqual((Tax_1>0), true)


// 设置 --usage="Grant" --percent=0.25  发起取回TxTaxUsage提议 (投yes票 并验证和上一步取回的tax是否一致)
// 假设tax总数为a 则：第一次0.2a = 第二次0.8a*0.25
balance_before = CmdUtils.getBalance(u.user_faa, "faa")
cmd = u.command + " --usage=Grant --percent=0.25"+ " --dest-address="+u.user_faa
response = CmdUtils.sendRequest('cmd/CmdWithOneArgs', cmd, "wait")
proposal_id = CmdUtils.getValueFromTag(response.responseBodyContent, "proposal-id")
GovUtils.voteProposal(u.v0, proposal_id, "yes")
GovUtils.voteProposal(u.v1, proposal_id, "yes")
sleep(u.VotingPeriod)
status = GovUtils.getProposalStatus(proposal_id)
WS.verifyEqual("Passed", status)
balance_after = CmdUtils.getBalance(u.user_faa, "faa")
Tax_2 = balance_after - balance_before
WS.verifyEqual(CmdUtils.compareIgnoreFee(Tax_1, Tax_2), true) //因为每个块的增发会有一部分放到Tax中，所以这里Tax_1, Tax_2会略有误差， 目前inflation关了



// 设置 --usage="Burn"  --percent=1  发起销毁TxTaxUsage提议 (投yes票)
// 再次 设置 --usage="Distribute" --percent=0.2  发起取回TxTaxUsage提议 (确认无任何tax取回)
balance_before = CmdUtils.getBalance(u.user_faa, "faa")
cmd = u.command + " --usage=Burn --percent=1"
response = CmdUtils.sendRequest('cmd/CmdWithOneArgs', cmd, "wait")
proposal_id = CmdUtils.getValueFromTag(response.responseBodyContent, "proposal-id")
GovUtils.voteProposal(u.v0, proposal_id, "yes")
GovUtils.voteProposal(u.v1, proposal_id, "yes")

cmd = u.command + " --usage=Grant --percent=0.25"+ " --dest-address="+u.user_faa
response = CmdUtils.sendRequest('cmd/CmdWithOneArgs', cmd, "wait")
proposal_id = CmdUtils.getValueFromTag(response.responseBodyContent, "proposal-id")
GovUtils.voteProposal(u.v0, proposal_id, "yes")
GovUtils.voteProposal(u.v1, proposal_id, "yes")
sleep(u.VotingPeriod)
status = GovUtils.getProposalStatus(proposal_id)
WS.verifyEqual("Passed", status)
balance_after = CmdUtils.getBalance(u.user_faa, "faa")
Tax_3 = balance_after - balance_before
WS.verifyEqual(CmdUtils.compareIgnoreFee(Tax_3, 0), true)


/*	//CmdUtils.pl(response.responseBodyContent)
 * utils
 */

class Utils {
	public String trustees_faa;
	public String v0;
	public String v1;
	public String user;
	public String user_faa;
	public String command;
	public int VotingPeriod = 12000;
	
	public Utils(){		
		if (CmdUtils.isMainnet()) {
			VotingPeriod = 22000
		}
		
		TestData faucet = findTestData('base/faucet')
		v0 = faucet.getValue('name', 1)
		v1 = faucet.getValue('name', 2)
		user = CmdUtils.createNewAccount(v0, "1iris")
		user_faa = CmdUtils.getAddressFromName(user, "faa")
		
		GenerateFee()
		GuardianUtils.addTrustee(v0, user_faa) //尽量不用v0账户检余额， 随时会发生distribution
		
		command = 'iriscli gov submit-proposal --title=test --description=test --type=TxTaxUsage --deposit=1000iris'.concat(GlobalVariable.chainId).concat(GlobalVariable.node).concat(' --from='+v0)
		command= CmdUtils.addTxFee(command, findTestData('base/tx'), 1)
	}
	
	public GenerateFee(){
		String address =  CmdUtils.getAddressFromName(v0, "faa")
		String cmd ="iriscli bank send --commit".concat(GlobalVariable.chainId).concat(GlobalVariable.node)+" --amount=1iris"+" --from="+v0+" --to="+address+" --gas=10000 --fee=2260iris"
		ResponseObject response = CmdUtils.sendRequest('cmd/CmdWithOneArgs', cmd, "wait")
		WS.verifyEqual(StringUtils.stringContains(response.responseBodyContent,"tx hash"), true)
	}
}

