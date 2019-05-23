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

Double source_shares = StakeUtils.delegation(u.user,u.v0)/2  //redelegate一半shares
source_shares = CmdUtils.round(source_shares,8,BigDecimal.ROUND_HALF_UP)
StakeUtils.redelegate(u.user, u.v0,  u.v1, source_shares)

response = CmdUtils.sendRequest('cmd/CmdWithOneArgs', u.command, "wait")
re = response.responseBodyContent

WS.verifyEqual(StringUtils.stringContains(response.responseBodyContent,u.user_faa), true)
WS.verifyEqual(StringUtils.stringContains(response.responseBodyContent,u.v0_fva), true)
WS.verifyEqual(StringUtils.stringContains(response.responseBodyContent,u.v1_fva), true)
WS.verifyEqual(StakeUtils.getSourceSharesFromRedelegation(re),source_shares)
WS.verifyEqual((StakeUtils.getDestinationSharesFromRedelegation(re)>0),true)

/*	//CmdUtils.pl(response.responseBodyContent)
 * utils
 */

class Utils {
	public String command;
	public String v0;
	public String v1;
	public String v0_fva;
	public String v1_fva;
	public String user;
	public String user_faa;

	public Utils(){
		TestData faucet = findTestData('base/faucet')
		v0 = faucet.getValue('name', 1)
		v1 = faucet.getValue('name', 2)
		v0_fva = CmdUtils.getAddressFromName(v0, "fva")
		v1_fva = CmdUtils.getAddressFromName(v1, "fva")	
		
		user = CmdUtils.createNewAccount(v0,"6iris")
		user_faa = CmdUtils.getAddressFromName(user, "faa")
		StakeUtils.delegate(user, v0, "5iris")		
		
		command = 'iriscli stake redelegation'.concat(GlobalVariable.node).concat(GlobalVariable.json)
			.concat(' --address-delegator=').concat(user_faa).concat(" --address-validator-source=").concat(v0_fva).concat(" --address-validator-dest=").concat(v1_fva)
	}
}
