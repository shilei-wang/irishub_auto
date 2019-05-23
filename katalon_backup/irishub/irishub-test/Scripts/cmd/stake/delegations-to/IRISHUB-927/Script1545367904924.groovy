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
import cmd.StakeUtils as StakeUtils
import com.google.gson.JsonObject as JsonObject;

Utils u = new Utils();

response = CmdUtils.sendRequest('cmd/CmdWithOneArgs', u.command, "sync")
array = CmdUtils.ParseArray(response.responseBodyContent)

index = StakeUtils.findItemInDelegations(array,u.user0_faa, "delegator_addr")
WS.verifyEqual((index!=-1), true)
item = array.get(index).getAsJsonObject()
WS.verifyEqual(item.get("delegator_addr").getAsString(), u.user0_faa)
WS.verifyEqual(item.get("validator_addr").getAsString(), u.v0_fva)
WS.verifyEqual((item.get("shares").getAsDouble()>0),true)

index = StakeUtils.findItemInDelegations(array,u.user1_faa, "delegator_addr")
WS.verifyEqual((index!=-1), true)
item = array.get(index).getAsJsonObject()
WS.verifyEqual(item.get("delegator_addr").getAsString(), u.user1_faa)
WS.verifyEqual(item.get("validator_addr").getAsString(), u.v0_fva)
WS.verifyEqual((item.get("shares").getAsDouble()>0),true)

/*	//CmdUtils.pl(response.responseBodyContent)
 * utils
 */

class Utils {
	public String command;
	public String v0;
	public String v0_fva;
	public String user0;
	public String user0_faa;
	public String user1;
	public String user1_faa;
	
	public Utils(){
		TestData faucet = findTestData('base/faucet')
		v0 = faucet.getValue('name', 1)
		v0_fva = CmdUtils.getAddressFromName(v0, "fva")
		
		user0 = CmdUtils.createNewAccount(v0,"2.1iris")
		user0_faa = CmdUtils.getAddressFromName(user0, "faa")	
		user1 = CmdUtils.createNewAccount(v0,"2.1iris")
		user1_faa = CmdUtils.getAddressFromName(user1, "faa")
		StakeUtils.delegate(user0, v0, "1iris")
		StakeUtils.delegate(user1, v0, "1iris")
		
		command = 'iriscli stake delegations-to'.concat(GlobalVariable.node).concat(GlobalVariable.json)+" "+v0_fva	
	}
}
