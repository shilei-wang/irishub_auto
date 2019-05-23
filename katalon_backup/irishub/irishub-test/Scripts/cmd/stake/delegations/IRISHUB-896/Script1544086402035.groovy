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

response = CmdUtils.sendRequest('cmd/CmdWithOneArgs', u.command, "sync")
array = CmdUtils.ParseArray(response.responseBodyContent)

index = StakeUtils.findItemInDelegations(array,u.v0_fva, "validator_addr")
WS.verifyEqual((index!=-1), true)
item = array.get(index).getAsJsonObject()
WS.verifyEqual(item.get("delegator_addr").getAsString(), u.user_faa)
WS.verifyEqual(item.get("validator_addr").getAsString(), u.v0_fva)
WS.verifyEqual((item.get("shares").getAsDouble()>0),true)


index = StakeUtils.findItemInDelegations(array,u.v1_fva, "validator_addr")
WS.verifyEqual((index!=-1), true)
item = array.get(index).getAsJsonObject()
WS.verifyEqual(item.get("delegator_addr").getAsString(), u.user_faa)
WS.verifyEqual(item.get("validator_addr").getAsString(), u.v1_fva)
WS.verifyEqual((item.get("shares").getAsDouble()>0),true)


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
		
		user = CmdUtils.createNewAccount(v0,"4iris")
		user_faa = CmdUtils.getAddressFromName(user, "faa")	
		StakeUtils.delegate(user, v0, "1iris")
		StakeUtils.delegate(user, v1, "1iris")
		
		command = 'iriscli stake delegations'.concat(GlobalVariable.node).concat(GlobalVariable.json)+" "+user_faa	
	}
}
