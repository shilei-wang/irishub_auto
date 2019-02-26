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
array = CmdUtils.ParseArray(response.responseBodyContent)
index = GuardianUtils.findItem(array,u.v0_faa)
WS.verifyEqual((index!=-1), true)
item = array.get(index).getAsJsonObject()
WS.verifyEqual(item.get("name").getAsString(), u.td.getValue("profiler-name", 1))
WS.verifyEqual(item.get("addr").getAsString(), u.v0_faa)
WS.verifyEqual(item.get("added_addr").getAsString(), u.v0_faa)


class Utils {
	public TestData td;
	public String command;
	public String v0;	
	public String v0_faa;
	
	public Utils(){
		td = findTestData('guardian/profilers/IRISHUB-888')
		TestData faucet = findTestData('base/faucet')
		v0 = faucet.getValue('name', 1)
		v0_faa = CmdUtils.getAddressFromName(v0, "faa")

		command = 'iriscli guardian profilers'.concat(' --node=').concat(GlobalVariable.node)		
	}
}

