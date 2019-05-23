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
import rest.AccountUtils
import rest.BaseTx
import utils.StringUtils as StringUtils
import utils.CmdUtils

data = findTestData('bank/send/IRISHUB-967')
faucet = findTestData('base/faucet')
v0 = faucet.getValue('name', 1)
v1 = faucet.getValue('name', 2)
password = faucet.getValue('password', 1)
v0_faa = AccountUtils.getAddrByName(v0, "acc")
v1_faa = AccountUtils.getAddrByName(v1, "acc")

//String async, String simulate, String generate_only, String withdrawAddress, String amount
i=1
v0_balance_before = BaseTx.getAccountBalance(v0_faa)
v1_balance_before = BaseTx.getAccountBalance(v1_faa)
response = BaseTx.transferWithParam(v0, password, "none", "true", "none", "none", v1_faa, data.getValue("amount", i))
WS.verifyEqual(StringUtils.stringContains(response.responseBodyContent,"hash"), true)
AccountUtils.waitUntilNextBlock()
v0_balance_after = BaseTx.getAccountBalance(v0_faa)
v1_balance_after = BaseTx.getAccountBalance(v1_faa)
v0_actual = v0_balance_before - v0_balance_after
v1_actual = v1_balance_after - v1_balance_before
expected = Double.valueOf(data.getValue("amount", i).replace("iris", ""))
WS.verifyEqual(CmdUtils.compareIgnoreFee(v0_actual, expected), true)
WS.verifyEqual(v1_actual, expected)


i=2
v0_balance_before = BaseTx.getAccountBalance(v0_faa)
response = BaseTx.transferWithParam(v0, password, "none", "true", "none", "none", v0_faa, data.getValue("amount", i))
AccountUtils.waitUntilNextBlock()
WS.verifyEqual(StringUtils.stringContains(response.responseBodyContent,"hash"), true)
v0_balance_before = BaseTx.getAccountBalance(v0_faa)
WS.verifyEqual(CmdUtils.compareIgnoreFee(v0_balance_before, v0_balance_after), true)



