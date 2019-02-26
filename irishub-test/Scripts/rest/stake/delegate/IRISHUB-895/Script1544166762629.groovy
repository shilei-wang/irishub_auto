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
import rest.BaseTx
import com.google.gson.Gson
import com.google.gson.JsonObject
import com.google.gson.JsonParser
import rest.GetAddressByKey
import rest.StakeUtils
import rest.AccountUtils
import utils.StringUtils as StringUtils

Map map = StakeUtils.getValidatorList()
delegateData = findTestData('stake/delegate/IRISHUB-895')
TestData faucet = findTestData('base/faucet')
String v0 = faucet.getValue('name', 1)
String v1 = faucet.getValue('name', 2)
String password = faucet.getValue('password', 1)
String v0ValAddr = map.get(v0)
String v1ValAddr = map.get(v1)
String address = GetAddressByKey.getAddressByKey(v0)
println v0ValAddr
println v1ValAddr

balance_before = BaseTx.getAccountBalance(address)
response = StakeUtils.delegationSubmit(v0, password, v0ValAddr, delegateData.getValue("amount",1))


WS.verifyEqual(StringUtils.stringContains(response.responseBodyContent,"hash"), true)
balance_after = BaseTx.getAccountBalance(address)
actual = balance_before - balance_after
expected = Double.valueOf(delegateData.getValue("amount", 1).replace("iris", ""))
WS.verifyEqual(CmdUtils.compareIgnoreFee(actual, expected), true)


response = StakeUtils.delegationSubmit(v0, password, v1ValAddr, delegateData.getValue("amount",2))
WS.verifyEqual(StringUtils.stringContains(response.responseBodyContent,"hash"), true)

