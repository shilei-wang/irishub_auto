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
import com.kms.katalon.core.testobject.ResponseObject as ResponseObject

Utils u = new Utils();
String[] cmdArray=new String[3];

//不指定validator地址， 从所有validators的地址取回reward （--is-validator指定false），验证reward是否到账
cmdArray[0] = ""

//指定validator地址， 从指定的validator的地址取回reward（--is-validator指定false），验证reward是否到账
cmdArray[1] = " --only-from-validator="+CmdUtils.getAddressFromName(u.name, "fva")

//指定validator地址， 从指定的validator的地址取回reward（--is-validator指定false），验证reward是否到账
cmdArray[2] = " --is-validator=true"

for (int i = 0; i < cmdArray.length ; i++) {
	u.Prepare(i)
	balance_before = CmdUtils.getBalance(u.withdraw_add, "address")
	u.GenerateFee()
	cmd = u.command+cmdArray[i]
	
	response = CmdUtils.sendRequest('cmd/CmdWithOneArgs', u.command, 5000)
	balance_after = CmdUtils.getBalance(u.withdraw_add, "address")
	WS.verifyEqual(StringUtils.stringContains(response.responseBodyContent,"tx hash"), true)
	WS.verifyGreaterThan(balance_after, balance_before)
}


class Utils {
	public String command;
	public String name;
	public String withdraw_add;
	
	public Utils(){
		withdraw_add = findTestData('distribution/withdraw-rewards/IRISHUB-809').getValue('withdraw_address', 1)
		name =  findTestData('base/faucet').getValue('name', 1)
		CmdUtils.sendIris(name, withdraw_add, "1iris-atto")

		command = 'iriscli distribution withdraw-rewards --chain-id='.concat(GlobalVariable.chainId).concat(' --node=').concat(GlobalVariable.node).concat(
			' --from=').concat(name)
		command = CmdUtils.addTxFee(command, findTestData('base/tx'), 1)			
	}
	
	public GenerateFee(){
		String address =  CmdUtils.getAddressFromName(name, "faa")
		String cmd ="iriscli bank send --chain-id="+GlobalVariable.chainId+" --amount=1iris"+" --from="+name+" --to="+address+" --gas=10000 --fee=50iris"
		ResponseObject response = CmdUtils.sendRequest('cmd/CmdWithOneArgs', cmd, 5000)
		WS.verifyEqual(StringUtils.stringContains(response.responseBodyContent,"tx hash"), true)
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
