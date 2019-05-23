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

Map map = StakeUtils.getValidatorList()
TestData faucet = findTestData('base/faucet')
String v0 = faucet.getValue('name', 1)
String v1 = faucet.getValue('name', 2)
String password = faucet.getValue('password', 1)
String v0ValAddr = map.get(v0)
String v1ValAddr = map.get(v1)

user = AccountUtils.createNewAccount("5.1iris")
userAddr = GetAddressByKey.getAddressByKey(user)
response = StakeUtils.delegationSubmit(user, password, v0ValAddr, "5iris")
// 新创建的指定账户在v0处解绑委托（部分解绑，设置--shares-amount=总share的1/2）
balance_before = BaseTx.getAccountBalance(userAddr)
shares_before = StakeUtils.getSpecifiedDelegationShares(userAddr, v0ValAddr)
share = CmdUtils.round(shares_before/2,8,BigDecimal.ROUND_HALF_UP)

//String account, String password, String validator_addr, String shares
StakeUtils.unbondSubmit(user, password, v0ValAddr, String.valueOf(share))
WS.verifyEqual(StringUtils.stringContains(response.responseBodyContent,"hash"), true)
//response = StakeUtils.unbodingDelegationQueryByDelValAddr(userAddr, v0ValAddr)
expected_banlance = StakeUtils.getExpectedBalanceFromUnbonding(userAddr, v0ValAddr)
AccountUtils.waitUntilSeveralBlock(3)
balance_after = BaseTx.getAccountBalance(userAddr)
shares_after = StakeUtils.getSpecifiedDelegationShares(userAddr, v0ValAddr)

expect = share
actual =  CmdUtils.round(shares_before - shares_after,8,BigDecimal.ROUND_HALF_UP)
//actual = shares_before - shares_after
WS.verifyEqual(actual, expect)

expect = expected_banlance
actual = balance_after - balance_before //这里需要修改 genesis-unbondtime
println expect
println actual
WS.verifyEqual(CmdUtils.compareIgnoreFee(actual, expect), true)

/*
// 新创建的指定账户在v0处解绑委托（全部解绑，设置--shares-percent=1 实际解绑后续1/2）
balance_before = BaseTx.getAccountBalance(userAddr)
response = StakeUtils.unbondSubmit(user, password, v0ValAddr, String.valueOf(share))
WS.verifyEqual(StringUtils.stringContains(response.responseBodyContent,"hash"), true)
expected_banlance = StakeUtils.getExpectedBalanceFromUnbonding(userAddr, v0ValAddr)
AccountUtils.waitUntilSeveralBlock(3)
balance_after = BaseTx.getAccountBalance(userAddr)
shares_after = StakeUtils.getSpecifiedDelegationShares(userAddr, v0ValAddr)

WS.verifyEqual(shares_after, 0) //全部取回 余额0shares

expect = expected_banlance
actual = balance_after - balance_before //这里需要修改 genesis-unbondtime
WS.verifyEqual(CmdUtils.compareIgnoreFee(actual, expect), true)

*/