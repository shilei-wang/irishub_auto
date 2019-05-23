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
import cmd.GuardianUtils as GuardianUtils
import cmd.DistributionUtils as DistributionUtils
import cmd.StakeUtils as StakeUtils
import com.google.gson.JsonObject as JsonObject;

Utils u = new Utils();

//主网版本不测unbond到账
if (!CmdUtils.isMainnet()) {
	balance_before = CmdUtils.getBalance(u.user, "name")
	StakeUtils.unbond(u.user, u.v0, 4)
	response = CmdUtils.sendRequest('cmd/CmdWithOneArgs', u.command, "sync")
	CmdUtils.waitUntilSeveralBlock(3)
	balance_after = CmdUtils.getBalance(u.user, "name")
	
	WS.verifyEqual(StringUtils.stringContains(response.responseBodyContent,u.user_faa), true)
	WS.verifyEqual(StringUtils.stringContains(response.responseBodyContent,u.v0_fva), true)
	
	expect = StakeUtils.getExpectedBalanceFromUnbonding(response.responseBodyContent)
	actual = balance_after - balance_before 
	WS.verifyEqual(CmdUtils.compareIgnoreFee(actual, expect), true)
}
/*	//CmdUtils.pl(response.responseBodyContent)
 * utils
 */

class Utils {
	public String command;
	public String v0;
	public String v0_fva;
	public String user;
	public String user_faa;

	public Utils(){
		TestData faucet = findTestData('base/faucet')
		v0 = faucet.getValue('name', 1)
		v0_fva = CmdUtils.getAddressFromName(v0, "fva")	
		
		user = CmdUtils.createNewAccount(v0,"6iris")
		user_faa = CmdUtils.getAddressFromName(user, "faa")	
		StakeUtils.delegate(user, v0, "5iris")
		//unbond会触发withdraw
		
		command = 'iriscli stake unbonding-delegation'.concat(GlobalVariable.node).concat(GlobalVariable.json).concat(" --address-delegator=").concat(user_faa)
		.concat(" --address-validator=").concat(v0_fva)
	}
}
