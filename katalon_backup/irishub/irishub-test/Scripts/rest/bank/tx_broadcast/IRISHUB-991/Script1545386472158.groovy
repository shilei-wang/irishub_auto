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
import rest.AccountUtils
import rest.BaseTx
import rest.BankUtils
import utils.StringUtils as StringUtils
import utils.CmdUtils
import com.google.gson.JsonObject

data = findTestData('bank/send/IRISHUB-967')
faucet = findTestData('base/faucet')
v0 = faucet.getValue('name', 1)
v1 = faucet.getValue('name', 2)
password = faucet.getValue('password', 1)
v0_faa = AccountUtils.getAddrByName(v0, "acc")
v1_faa = AccountUtils.getAddrByName(v1, "acc")

v0_balance_before = BaseTx.getAccountBalance(v0_faa)
v1_balance_before = BaseTx.getAccountBalance(v1_faa)
response = BaseTx.transferWithParam(v0, password, "none", "none", "none", "true", v1_faa, "1iris")
WS.verifyEqual(StringUtils.stringContains(response.responseBodyContent,"irishub/bank/Send"), true)
WS.verifyEqual(response.responseBodyContent.contains("\"signatures\":null"), true)
AccountUtils.waitUntilNextBlock()
v0_balance_after = BaseTx.getAccountBalance(v0_faa)
v1_balance_after = BaseTx.getAccountBalance(v1_faa)
WS.verifyEqual(v0_balance_after, v0_balance_before)
WS.verifyEqual(v1_balance_after, v1_balance_before)

JsonObject txMsg = BankUtils.getTxValue(response.responseBodyContent)
response = BankUtils.transactionSign(v0, password, txMsg)
WS.verifyEqual(response.responseBodyContent.contains("\"signatures\""), true)

txMsg = BankUtils.getTxValue(response.responseBodyContent)
response = BankUtils.transactionBroadcastWithParams(txMsg, "none", "true", "none")
WS.verifyEqual(response.responseBodyContent.contains("hash"), true)
AccountUtils.waitUntilNextBlock()