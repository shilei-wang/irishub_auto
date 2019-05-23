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
import cmd.StakeUtils as StakeUtils
import com.kms.katalon.core.testobject.ResponseObject as ResponseObject
import cmd.DistributionUtils as DistributionUtils


Utils u = new Utils();
String[] cmdArray=new String[3];

//不指定validator地址， 从所有validators的地址取回reward （--is-validator指定false），验证reward是否到账
cmdArray[0] = ""

//指定validator地址， 从指定的validator的地址取回reward（--is-validator指定false），验证reward是否到账
cmdArray[1] = " --only-from-validator="+CmdUtils.getAddressFromName(u.v0, "fva")

//指定validator地址， 从指定的validator的地址取回reward（--is-validator指定true），验证reward是否到账
cmdArray[2] = " --is-validator=true"

DistributionUtils.setWithDrawAdd(u.v0, u.withdraw_add)
for (int i = 0; i < cmdArray.length ; i++) {
	balance_before = CmdUtils.getBalance(u.withdraw_add, "faa")
	u.GenerateFee()
	cmd = u.command+cmdArray[i]

	response = CmdUtils.sendRequest('cmd/CmdWithOneArgs', u.command, "wait")
	balance_after = CmdUtils.getBalance(u.withdraw_add, "faa")	
	WS.verifyEqual(StringUtils.stringContains(response.responseBodyContent,"tx hash"), true)
	WS.verifyEqual(((balance_after-balance_before)>0),  true)
}


//将一个delegator与两个validator设置两组委托关系，不指定validator地址，（--is-validator指定false），验证reward是否到账
user_0 = CmdUtils.createNewAccount(u.v0,"201iris")
StakeUtils.delegate(user_0, u.v0,"100iris")
StakeUtils.delegate(user_0, u.v1,"100iris")
user_1 = CmdUtils.createNewAccount(u.v0,"201iris")
StakeUtils.delegate(user_1, u.v0,"100iris")
StakeUtils.delegate(user_1, u.v1,"100iris")


u.GenerateFee()
sleep(5000)
//withdraw address 
DistributionUtils.setWithDrawAdd(user_0, u.withdraw_add)
balance_before = CmdUtils.getBalance(u.withdraw_add, "faa")
cmd = 'iriscli distribution withdraw-rewards'.concat(GlobalVariable.chainId).concat(GlobalVariable.node).concat(' --from='+user_0)
cmd = CmdUtils.addTxFee(cmd, findTestData('base/tx'), 1)
response = CmdUtils.sendRequest('cmd/CmdWithOneArgs', cmd, "wait")
balance_after = CmdUtils.getBalance(u.withdraw_add, "faa")
reward_1 = balance_after - balance_before
WS.verifyEqual(StringUtils.stringContains(response.responseBodyContent,"tx hash"), true)

CmdUtils.pl(balance_before)
CmdUtils.pl(balance_after)
CmdUtils.pl(reward_1)

WS.verifyEqual((reward_1>0), true)


sleep(5000)
//将一个delegator与两个validator设置两组委托关系，指定validator地址，（--is-validator指定false），验证reward是否到账，验证到账金额是不是比4）少。
DistributionUtils.setWithDrawAdd(user_1, u.withdraw_add)
balance_before = CmdUtils.getBalance(u.withdraw_add, "faa")
cmd = 'iriscli distribution withdraw-rewards'.concat(GlobalVariable.chainId).concat(GlobalVariable.node).concat(' --from='+user_1)+" --only-from-validator="+CmdUtils.getAddressFromName(u.v0, "fva") 
cmd = CmdUtils.addTxFee(cmd, findTestData('base/tx'), 1)
response = CmdUtils.sendRequest('cmd/CmdWithOneArgs', cmd, "wait")
balance_after = CmdUtils.getBalance(u.withdraw_add, "faa")
reward_2 = balance_after - balance_before
WS.verifyEqual(StringUtils.stringContains(response.responseBodyContent,"tx hash"), true)
WS.verifyEqual((reward_2>0), true)

WS.verifyEqual(((reward_1-reward_2)>0),  true)

class Utils {
	public String command;
	public String v0;
	public String v1;
	public String withdraw_add;

	
	public Utils(){
		withdraw_add = GlobalVariable.address_aa
		v0 =  findTestData('base/faucet').getValue('name', 1)
		v1 =  findTestData('base/faucet').getValue('name', 2)
		
		command = 'iriscli distribution withdraw-rewards'.concat(GlobalVariable.chainId).concat(GlobalVariable.node).concat(' --from='+v0)
		command = CmdUtils.addTxFee(command, findTestData('base/tx'), 1)			
	}
	
	public GenerateFee(){
		String address =  CmdUtils.getAddressFromName(v1, "faa")
		String cmd ="iriscli bank send --commit".concat(GlobalVariable.chainId).concat(GlobalVariable.node)+
		" --amount=1iris"+" --from="+v1+" --to="+address+" --gas=10000 --fee=50iris"
		ResponseObject response = CmdUtils.sendRequest('cmd/CmdWithOneArgs', cmd, "wait")
		WS.verifyEqual(StringUtils.stringContains(response.responseBodyContent,"tx hash"), true)
	}
}
