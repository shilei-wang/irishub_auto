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

Utils u = new Utils();

response = CmdUtils.sendRequest('cmd/CmdWithOneArgs', u.command, "sync")
WS.verifyEqual(StringUtils.stringContains(response.responseBodyContent,u.v0_fva), true)
CmdUtils.pl(response.responseBodyContent)
/*	//CmdUtils.pl(response.responseBodyContent)
 * utils
 */

class Utils {
	public TestData td;
	public String command;
	public String v0;	
	public String v0_fva;
	
	public Utils(){
		td = findTestData('stake/validator/IRISHUB-892')
		TestData faucet = findTestData('base/faucet')
		v0 = faucet.getValue('name', 1)
		v0_fva = CmdUtils.getAddressFromName(v0, "fva")

		command = 'iriscli stake validator'.concat(GlobalVariable.node).concat(GlobalVariable.json).concat(' ').concat(v0_fva)
	}
}
