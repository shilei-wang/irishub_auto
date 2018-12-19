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
import rest.BaseTx
import com.google.gson.Gson
import com.google.gson.JsonObject
import com.google.gson.JsonParser
import rest.GetAddressByKey
import rest.StakeUtils
import rest.AccountUtils
import utils.StringUtils as StringUtils

Map map = StakeUtils.getValidatorList()
TestData faucet = findTestData('base/faucet')
String name = faucet.getValue('name', 1)
String password = faucet.getValue('password', 1)
String validatorAddr = map.get(name)
println validatorAddr 

TestData data = findTestData('distribution/withdraw-rewards/IRISHUB-809')
String withdrawAddress = data.getValue("withdraw_address",1)

String[] BaseTxInfo = new String[2]
String fee = "50iris"

BaseTxInfo = BaseTx.baseTxProduce(name, password)
BaseTx.transfer(BaseTxInfo[0], BaseTxInfo[1], withdrawAddress, "1iris", "0.04iris")
AccountUtils.waitUntilNextBlock()
BaseTxInfo = BaseTx.baseTxProduce(name, password)
BaseTx.setWithDrawAddr(BaseTxInfo[0], BaseTxInfo[1], withdrawAddress)
String[] HttpBody = new String[3]
AccountUtils.waitUntilNextBlock()
for (int i = 0; i < HttpBody.length ; i++) {
	Double balance_before = BaseTx.getAccountBalance(withdrawAddress)
	BaseTxInfo = BaseTx.baseTxProduce(name, password);
	BaseTx.transfer(BaseTxInfo[0], BaseTxInfo[1], withdrawAddress, "1iris", fee)
	AccountUtils.waitUntilNextBlock()
	BaseTxInfo = BaseTx.baseTxProduce(name, password);
	//不指定validator地址， 从所有validators的地址取回reward （--is-validator指定false），验证reward是否到账
	HttpBody[0] = BaseTx.withdrawRewardBodyProduce(BaseTxInfo[1], BaseTxInfo[0], false, false, false, validatorAddr)
	//指定validator地址， 从指定的validator的地址取回reward（--is-validator指定false），验证reward是否到账
	HttpBody[1] = BaseTx.withdrawRewardBodyProduce(BaseTxInfo[1], BaseTxInfo[0], true, false, true, validatorAddr)
	//--is-validator指定为true， 从自身验证人节点取回commission （不指定validator地址），验证commission是否到账
	HttpBody[2] = BaseTx.withdrawRewardBodyProduce(BaseTxInfo[1], BaseTxInfo[0], true, true, false, validatorAddr)
	response =  WS.sendRequest(findTestObject('rest/distribution/ICS24_post_distribution_delegatorAddr_withdrawReward', [ ('delegatorAddr') : BaseTxInfo[1], ('HttpBody') : HttpBody[i], ('lcdIP') : GlobalVariable.lcdIP]))
	System.out.println(response.responseBodyContent)
	WS.verifyEqual(response.responseBodyContent.contains("hash"), true)
	AccountUtils.waitUntilNextBlock()
	Double balance_after = BaseTx.getAccountBalance(withdrawAddress)
	WS.verifyGreaterThan(balance_after, balance_before)
}


String newAccount1 = AccountUtils.creNewAccBuildTwoDelegation("201iris")
String newAccount2 = AccountUtils.creNewAccBuildTwoDelegation("201iris")

BaseTxInfo = BaseTx.baseTxProduce(name, password)
BaseTx.transfer(BaseTxInfo[0], BaseTxInfo[1], withdrawAddress, "1iris", fee)
sleep(5000)


Double balance_before = BaseTx.getAccountBalance(GetAddressByKey.getAddressByKey(newAccount1))
println balance_before
BaseTxInfo = BaseTx.baseTxProduce(newAccount1, password)
HttpBodyAccount = BaseTx.withdrawRewardBodyProduce(BaseTxInfo[1], BaseTxInfo[0], false, false, false, "")
response =  WS.sendRequest(findTestObject('rest/distribution/ICS24_post_distribution_delegatorAddr_withdrawReward', [ ('delegatorAddr') : BaseTxInfo[1], ('HttpBody') : HttpBodyAccount, ('lcdIP') : GlobalVariable.lcdIP]))
System.out.println(response.responseBodyContent)
AccountUtils.waitUntilNextBlock()
Double balance_after = BaseTx.getAccountBalance(GetAddressByKey.getAddressByKey(newAccount1))
println balance_after
Double reward_1 = balance_after - balance_before

WS.verifyEqual(StringUtils.stringContains(response.responseBodyContent,"hash"), true)
WS.verifyEqual((reward_1>0), true)


balance_before = BaseTx.getAccountBalance(GetAddressByKey.getAddressByKey(newAccount2))
println balance_before
BaseTxInfo = BaseTx.baseTxProduce(newAccount2, password)
HttpBodyAccount = BaseTx.withdrawRewardBodyProduce(BaseTxInfo[1], BaseTxInfo[0], false, false, true, validatorAddr)
response =  WS.sendRequest(findTestObject('rest/distribution/ICS24_post_distribution_delegatorAddr_withdrawReward', [ ('delegatorAddr') : BaseTxInfo[1], ('HttpBody') : HttpBodyAccount, ('lcdIP') : GlobalVariable.lcdIP]))
System.out.println(response.responseBodyContent)
AccountUtils.waitUntilNextBlock()
balance_after = BaseTx.getAccountBalance(GetAddressByKey.getAddressByKey(newAccount2))
println balance_after
Double reward_2 = balance_after - balance_before
WS.verifyEqual(StringUtils.stringContains(response.responseBodyContent,"hash"), true)
WS.verifyEqual((reward_2>0), true)

println reward_1
println reward_2
WS.verifyEqual(((reward_1-reward_2)>0),  true)

/*

*/