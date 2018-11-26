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

Utils u = new Utils();

for (int i = 1; i <= u.td.getRowNumbers(); i++) {
	u.Prepare(i)
	cmd = CmdUtils.generateCmd(u.command, u.td, i)
	cmd = cmd.replace("--account=", "")
	response = CmdUtils.sendRequest('cmd/CmdWithOneArgs', cmd, 0)
	WS.verifyEqual(StringUtils.stringContains(response.responseBodyContent,u.td.getValue("cmd_result", i)), true)

	//CmdUtils.printLog(response.responseBodyContent)
}



class Utils {
	public TestData td;
	public String command;
	public String serviceName;
	
	public Utils(){		
		td = findTestData('distribution/withdraw-address/IRISHUB-798')
		
		TestData faucet = findTestData('base/faucet')
		String name = faucet.getValue('name', 1)
		command = 'iriscli distribution withdraw-address --chain-id='.concat(GlobalVariable.chainId).concat(' --node=').concat(GlobalVariable.node)
	}

	//JUST FOR　DEBUG
	public Prepare (int i){
		switch(i){
			case 1:
				println "--- TESTCASE 1 --- "
				break;
			case 2:
				println "--- TESTCASE 2 --- "
				break;
			case 3:
				println "--- TESTCASE 3 --- "
				break;
			case 4:
				println "--- TESTCASE 4 --- "
				break;
			}
	}
}
