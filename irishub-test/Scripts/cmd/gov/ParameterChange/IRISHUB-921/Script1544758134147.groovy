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
import cmd.GovUtils as GovUtils
import utils.CmdUtils as CmdUtils
import utils.StringUtils as StringUtils
	
/*
 * 如果遇到sequence问题 在两次proposal之间加sleep(1000)
 */
Utils u = new Utils();
Map<String, String> params_init
Map<String, String> params_new
GovUtils.saveParams()
params_init = GovUtils.getParams()

//使用参数方式提交提议
//接近1000iris 不影响其他测试结果
//暂时不修改 govVotingProcedure 会影响测试 (不能改小于20秒)
//DepositProcedure
random_amount = "9999999999999"+ u.RandomID
random_deposit_period = "1728000" + u.RandomID
cmd = u.command[1] + " --param="+'{\\"key\\":\\"Gov/govDepositProcedure\\",\\"value\\":\\"{\\\\"min_deposit\\\\":[{\\\\"denom\\\\":\\\\"iris-atto\\\\",\\\\"amount\\\\":\\\\"'+random_amount+'\\\\"}],\\\\"max_deposit_period\\\\":'+random_deposit_period+'}\\",\\"op\\":\\"update\\"}'
response = CmdUtils.sendRequest('cmd/CmdWithOneArgs', cmd, "wait")
proposal_id = StringUtils.getValueFromKey(response.responseBodyContent, "proposal-id")
GovUtils.voteProposal(u.v0, proposal_id, "yes")
GovUtils.voteProposal(u.v1, proposal_id, "yes")

//TallyingProcedure
random_threshold = "0.49"+ u.RandomID
random_veto = "0.49" + u.RandomID
random_participation = "0.49" + u.RandomID
cmd = u.command[1] + " --param="+'{\\"key\\":\\"Gov/govTallyingProcedure\\",\\"value\\":\\"{\\\\"threshold\\\\": \\\\"'+random_threshold+'\\\\",\\\\"veto\\\\": \\\\"'+random_veto+'\\\\",\\\\"participation\\\\": \\\\"'+random_participation+'\\\\"}\\",\\"op\\":\\"update\\"}'
response = CmdUtils.sendRequest('cmd/CmdWithOneArgs', cmd, "wait")
proposal_id = StringUtils.getValueFromKey(response.responseBodyContent, "proposal-id")
GovUtils.voteProposal(u.v0, proposal_id, "yes")
GovUtils.voteProposal(u.v1, proposal_id, "yes")

sleep(8000)
params_new = GovUtils.getParams()
WS.verifyEqual(params_new.get("amount"), random_amount)
WS.verifyEqual(params_new.get("max_deposit_period"), random_deposit_period)
WS.verifyEqual(params_new.get("threshold"), random_threshold)
WS.verifyEqual(params_new.get("veto"), random_veto)
WS.verifyEqual(params_new.get("participation"), random_participation)


//使用文件方式提交提议
//条件限制，--path使用默认目录
//DepositProcedure
cmd = u.command[2] + " --key=Gov/govDepositProcedure"
response = CmdUtils.sendRequest('cmd/CmdWithOneArgs', cmd, "wait")
proposal_id = StringUtils.getValueFromKey(response.responseBodyContent, "proposal-id")
GovUtils.voteProposal(u.v0, proposal_id, "yes")
GovUtils.voteProposal(u.v1, proposal_id, "yes")

//TallyingProcedure
cmd = u.command[2] + " --key=Gov/govTallyingProcedure"
response = CmdUtils.sendRequest('cmd/CmdWithOneArgs', cmd, "wait")
proposal_id = StringUtils.getValueFromKey(response.responseBodyContent, "proposal-id")
GovUtils.voteProposal(u.v0, proposal_id, "yes")
GovUtils.voteProposal(u.v1, proposal_id, "yes")

sleep(8000)
params_new = GovUtils.getParams()
WS.verifyEqual(params_new.get("amount"), params_init.get("amount"))
WS.verifyEqual(params_new.get("max_deposit_period"),  params_init.get("max_deposit_period"))
WS.verifyEqual(params_new.get("threshold"), params_init.get("threshold"))
WS.verifyEqual(params_new.get("veto"), params_init.get("veto"))
WS.verifyEqual(params_new.get("participation"), params_init.get("participation"))


/*	//CmdUtils.pl(response.responseBodyContent)
 * utils
 */

class Utils {
	public TestData td;
	public String[] command=new String[3];
	public String v0;
	public String v1;
	public String RandomID;
	
	public Utils(){
		td = findTestData('gov/query-proposal-s/IRISHUB-913')
		TestData faucet = findTestData('base/faucet')
		v0 = faucet.getValue('name', 1)
		v1 = faucet.getValue('name', 2)
		
		RandomID = CmdUtils.generateRandomID()		
		
		command[1] = 'iriscli gov submit-proposal --title=test --description=test --type=ParameterChange --deposit=1000iris --chain-id='.concat(GlobalVariable.chainId).concat(' --node=')
			.concat(GlobalVariable.node).concat(' --from=').concat(v0)
		command[1] = CmdUtils.addTxFee(command[1], findTestData('base/tx'), 1)
					
		command[2] = 'iriscli gov submit-proposal --op=update --title=test --description=test --type=ParameterChange --deposit=1000iris --chain-id='.concat(GlobalVariable.chainId).concat(' --node=')
			.concat(GlobalVariable.node).concat(' --from=').concat(v0)
		command[2] = CmdUtils.addTxFee(command[2], findTestData('base/tx'), 1)
	}
}

