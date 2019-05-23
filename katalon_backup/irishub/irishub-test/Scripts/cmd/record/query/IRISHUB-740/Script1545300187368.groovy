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
import cmd.RecordUtils


Utils u = new Utils();

//for (int i = 1; i <= u.td.getRowNumbers(); i++) {
//	record_id = RecordUtils.submit(u.v0, u.td.getValue("description", i), u.td.getValue("onchain-data", i)+"_"+u.randomId)
//	cmd = u.command+" --record-id="+record_id
//	response = CmdUtils.sendRequest('cmd/CmdWithOneArgs', cmd, "sync")
//	re = CmdUtils.Parse(response.responseBodyContent).getAsJsonObject()
//	WS.verifyEqual(re.get("owner_addr").getAsString(), u.v0_faa)
//	WS.verifyEqual(re.get("record_id").getAsString(), record_id)
//	WS.verifyEqual(re.get("description").getAsString(), u.td.getValue("description", i))
//	WS.verifyEqual(re.get("data").getAsString(), u.td.getValue("onchain-data", i)+"_"+u.randomId)
//}


/*	//CmdUtils.pl(response.responseBodyContent)
 * utils
 */

class Utils {
	public TestData td;
	public String command;
	public String v0;
	public String v0_faa;
	public String randomId;
	
	public Utils(){
		td = findTestData('record/query/IRISHUB-740')
		TestData faucet = findTestData('base/faucet')
		v0 = faucet.getValue('name', 1)
		v0_faa = CmdUtils.getAddressFromName(v0, "faa")
		randomId = CmdUtils.generateRandomID()

		command = 'iriscli record query --chain-id='.concat(GlobalVariable.chainId).concat(' --node=').concat(GlobalVariable.node)
	}
}
