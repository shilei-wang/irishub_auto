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

Utils u = new Utils();

for (int i = 1; i <= u.td.getRowNumbers(); i++) {
	cmd = u.command +" "+ u.td.getValue("account", i)
	response = CmdUtils.sendRequest('cmd/CmdWithOneArgs', cmd, "sync")
	WS.verifyEqual(StringUtils.stringContains(response.responseBodyContent,u.td.getValue("cmd_result", i)), true)
	//CmdUtils.printLog(response.responseBodyContent)
}



class Utils {
	public TestData td;
	public String command;

	public Utils(){		
		td = findTestData('distribution/validator-distr-info/IRISHUB-815')

		command = 'iriscli distribution validator-distr-info --chain-id='.concat(GlobalVariable.chainId).
					concat(' --node=').concat(GlobalVariable.node)
	}
}
