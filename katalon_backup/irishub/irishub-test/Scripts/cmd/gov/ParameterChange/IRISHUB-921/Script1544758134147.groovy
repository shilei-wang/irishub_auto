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
 * 备注：系统参数的修改会影响autotest的运行， 故这里autotest只验证参数修改的可用性和正确性， 并不对所有参数进行修改。 全面的修改验证由手动测试覆盖
 */
Utils u = new Utils();

cmd = u.command+" --param=stake/MaxValidators="+u.MaxValidators
response = CmdUtils.sendRequest('cmd/CmdWithOneArgs', cmd, "wait")
WS.verifyEqual(StringUtils.stringContains(response.responseBodyContent,"tx hash"), true)
proposal_id = CmdUtils.getValueFromTag(response.responseBodyContent, "proposal-id")
GovUtils.voteProposal(u.v0, proposal_id, "yes")
GovUtils.voteProposal(u.v1, proposal_id, "yes")
sleep(u.VotingPeriod)
status = GovUtils.getProposalStatus(proposal_id)
WS.verifyEqual("Passed", status)

//验证参数是否改了
MaxValidators_new= GovUtils.getParam("stake","max_validators")
WS.verifyEqual(MaxValidators_new, u.MaxValidators)


/*	//CmdUtils.pl(response.responseBodyContent)
 * utils
 */

class Utils {
	public TestData td;
	public String command;
	public String v0;
	public String v1;
	public String MaxValidators;
	public int VotingPeriod = 12000;
	
	public Utils(){
		if (CmdUtils.isMainnet()) {
			VotingPeriod = 22000
		}
		
		td = findTestData('gov/query-proposal-s/IRISHUB-913')
		TestData faucet = findTestData('base/faucet')
		v0 = faucet.getValue('name', 1)
		v1 = faucet.getValue('name', 2)
		
		MaxValidators = generateRandomMaxValidators()		
		
		command = 'iriscli gov submit-proposal --title=test --description=test --type=ParameterChange --deposit=2000iris'
			.concat(GlobalVariable.chainId).concat(GlobalVariable.node).concat(' --from='+v0)
		command = CmdUtils.addTxFee(command, findTestData('base/tx'), 1)
	}
	
	//保证数目在100-200之间
	public String generateRandomMaxValidators(){
		String sources = "0123456789"
		Random rand = new Random()
		StringBuffer flag = new StringBuffer()
		for (int j = 0; j < 2; j++) {
			flag.append(sources.charAt(rand.nextInt(9)))
		}
		return "1"+flag.toString();		
	}	
}
