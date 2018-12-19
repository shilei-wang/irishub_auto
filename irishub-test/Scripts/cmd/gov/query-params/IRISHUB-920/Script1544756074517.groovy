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

import com.google.gson.JsonObject as JsonObject;
	
Utils u = new Utils();

// --module
cmd=u.command+" --module=gov"
response = CmdUtils.sendRequest('cmd/CmdWithOneArgs', cmd, "wait")
array = CmdUtils.ParseArray(response.responseBodyContent)
for (int i = 0; i < array.size() ; i++) {
	item = array.get(i).getAsString()
	WS.verifyEqual(StringUtils.arrayContains(u.keys, item), true)	
}

// --key
for (int i = 1; i <= u.td.getRowNumbers(); i++) {
	cmd = CmdUtils.generateCmd(u.command, u.td, i)
	response = CmdUtils.sendRequest('cmd/CmdWithOneArgs', cmd, "wait")
	//CmdUtils.pl(response.responseBodyContent)
	re = CmdUtils.Parse(response.responseBodyContent).getAsJsonObject()
	WS.verifyEqual(re.get("key").getAsString(), u.td.getValue("key", i))	
}


/*	//CmdUtils.pl(response.responseBodyContent)
 * utils
 */

class Utils {
	public TestData td;
	public String command;
	public String[] keys=new String[3];
	
	public Utils(){
		td = findTestData('gov/query-params/IRISHUB-920')
		
		for (int i = 1; i <= td.getRowNumbers(); i++) {
			keys[i-1] = td.getValue("key", i)		
		}	
		
		command = 'iriscli gov query-params'.concat(' --node=').concat(GlobalVariable.node)
	}
}

