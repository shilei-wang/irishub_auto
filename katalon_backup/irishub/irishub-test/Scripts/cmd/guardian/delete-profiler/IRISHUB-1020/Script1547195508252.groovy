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
import utils.CmdUtils as CmdUtils
import utils.StringUtils as StringUtils
import cmd.ServiceUtils as ServiceUtils
import cmd.GuardianUtils as GuardianUtils
import com.google.gson.JsonObject as JsonObject;
import com.google.gson.JsonArray as JsonArray;

Utils u = new Utils();

array = CmdUtils.ParseArray(GuardianUtils.profilers())
index = GuardianUtils.findItem(array, u.user_faa)
WS.verifyEqual((index!=-1), true)

response = CmdUtils.sendRequest('cmd/CmdWithOneArgs', u.command, "wait")
WS.verifyEqual(StringUtils.stringContains(response.responseBodyContent,"tx hash"), true)

array = CmdUtils.ParseArray(GuardianUtils.profilers())
index = GuardianUtils.findItem(array, u.user_faa)
WS.verifyEqual(index,-1)	


/* //CmdUtils.pl(u.command)
 * utils
 */

class Utils {
	public TestData td;
	public String command;
	public String v0;
	public String v0_faa;
	public String user;
	public String user_faa;
	
	public Utils(){
		td = findTestData('guardian/add-profiler/IRISHUB-890')
		TestData faucet = findTestData('base/faucet')
		v0 = faucet.getValue('name', 1)
		v0_faa = CmdUtils.getAddressFromName(v0, "faa")
		user = CmdUtils.createNewAccount(v0,"0iris")
		user_faa = CmdUtils.getAddressFromName(user, "faa")		
		GuardianUtils.addProfilers(v0, user_faa)
		
		command = 'iriscli guardian delete-profiler'.concat(GlobalVariable.chainId).concat(GlobalVariable.node)
				.concat(' --from='+v0)+" --address="+user_faa
		command = CmdUtils.addTxFee(command, findTestData('base/tx'), 1)
	}
}
