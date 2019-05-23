import static com.kms.katalon.core.checkpoint.CheckpointFactory.findCheckpoint
import static com.kms.katalon.core.testcase.TestCaseFactory.findTestCase
import static com.kms.katalon.core.testdata.TestDataFactory.findTestData
import static com.kms.katalon.core.testobject.ObjectRepository.findTestObject

import java.util.Map

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
import rest.AccountUtils
import rest.BaseTx
import utils.StringUtils as StringUtils
import rest.BankUtils
import com.google.gson.JsonArray as JsonArray;
import com.google.gson.JsonObject as JsonObject;


faucet = findTestData('base/faucet')
v0 = faucet.getValue('name', 1)
password = faucet.getValue('password', 1)
v0_faa = AccountUtils.getAddrByName(v0, "acc")
fee = findTestData('base/tx').getValue("fee", 1)

user = AccountUtils.createNewAccount("1iris")
user_faa = AccountUtils.getAddrByName(user, "acc")

response = BankUtils.coinType("iris")

Map units_map = BankUtils.setUnits(response.responseBodyContent)
BigDecimal bigDecimal = new BigDecimal(Long.MAX_VALUE);

String[] BaseTxInfo
// 确认所有coin-type 位数正确且可用 都转账1iris的数额
for (String key : units_map.keySet()) {	
	bigDecimal = Math.pow(10,units_map.get(key))
	amount = bigDecimal.toString()+key;
	println amount
	balance_before = BaseTx.getAccountBalance(user_faa)
	BaseTxInfo = BaseTx.baseTxProduce(v0, password)
	//String withdrawAddress, String amount, String fee
	BaseTx.transfer(BaseTxInfo[0], BaseTxInfo[1], user_faa, amount, fee)
	AccountUtils.waitUntilNextBlock()
	balance_after = BaseTx.getAccountBalance(user_faa)
	println balance_before
	println balance_after
	WS.verifyEqual((balance_after-balance_before), 1)	
}	



