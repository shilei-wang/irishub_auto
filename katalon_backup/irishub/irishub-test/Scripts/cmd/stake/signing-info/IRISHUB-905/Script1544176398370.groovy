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

Utils u = new Utils();

response = CmdUtils.sendRequest('cmd/CmdWithOneArgs', u.command, "wait")
WS.verifyEqual(StringUtils.stringContains(response.responseBodyContent,"start_height"), true)

/*	//CmdUtils.pl(response.responseBodyContent)
 * utils
 */

class Utils {
	public String command;
	public String v0;
	public String v0_fvp;

	
	public Utils(){
		TestData faucet = findTestData('base/faucet')
		v0 = faucet.getValue('name', 1)		
		v0_fvp = StakeUtils.getSeedFvp(v0)

		command = 'iriscli stake signing-info'.concat(GlobalVariable.node).concat(GlobalVariable.json).concat(' ')+v0_fvp	
	}
}
