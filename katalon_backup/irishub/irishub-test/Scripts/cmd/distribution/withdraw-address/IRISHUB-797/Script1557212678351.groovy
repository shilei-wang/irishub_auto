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
import utils.CmdUtils
import utils.StringUtils as StringUtils
import cmd.DistributionUtils as DistributionUtils

Utils u = new Utils();

DistributionUtils.setWithDrawAdd(u.v0, u.withdraw_add)

response = CmdUtils.sendRequest('cmd/CmdWithOneArgs', u.command, "sync")
WS.verifyEqual(StringUtils.stringContains(response.responseBodyContent,u.withdraw_add), true)

class Utils {
	public TestData td;
	public String command;
	public String v0;
	public String v0_faa;
	public String withdraw_add;
	
	public Utils(){
		td = findTestData('distribution/withdraw-address/IRISHUB-797')
		withdraw_add = td.getValue("set-withdraw-addr", 1)
		
		v0 =  findTestData('base/faucet').getValue('name', 1)
		v0_faa = CmdUtils.getAddressFromName(v0,"faa")
		command = 'iriscli distribution withdraw-address'.concat(GlobalVariable.chainId).concat(GlobalVariable.node).concat(' '+v0_faa)
	}
}

