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

name = findTestData('base/faucet').getValue('name', 1)
faa = CmdUtils.getAddressFromName(name,"faa")
cmd = u.command+" "+faa
response = CmdUtils.sendRequest('cmd/CmdWithOneArgs', cmd, 0)
WS.verifyEqual(StringUtils.stringContains(response.responseBodyContent,u.td.getValue("set-withdraw-addr", 1)), true)



class Utils {
	public TestData td;
	public String command;
	
	public Utils(){
		td = findTestData('distribution/withdraw-address/IRISHUB-797')
		
		setWithDrawAdd()
		command = 'iriscli distribution withdraw-address --chain-id='.concat(GlobalVariable.chainId).concat(' --node=').concat(GlobalVariable.node)		
	}
	
	public setWithDrawAdd(){
		TestData data = findTestData('distribution/withdraw-address/IRISHUB-797')		
		String name = findTestData('base/faucet').getValue('name', 1)
		String cmd = 'iriscli distribution set-withdraw-addr --chain-id='.concat(GlobalVariable.chainId).concat(' --node=').concat(GlobalVariable.node).concat(
			' --from=').concat(name)
		cmd = CmdUtils.addTxFee(cmd, findTestData('base/tx'), 1)			
		cmd = CmdUtils.generateCmd(cmd, data, 1)
		cmd = cmd.replace("--set-withdraw-addr=", "")
		ResponseObject response = CmdUtils.sendRequest('cmd/CmdWithOneArgs', cmd, 5000)
		WS.verifyResponseStatusCode(response, 200)		
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
