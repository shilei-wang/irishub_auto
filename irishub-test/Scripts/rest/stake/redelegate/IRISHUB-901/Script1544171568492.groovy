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
//balance_before = BaseTx.getAccountBalance(userAddr)
response = StakeUtils.delegationSubmit(user, password, v0ValAddr, "5iris")

//  redelegate 部分解绑，设置--shares-amount=1/2的delegation , 验证shares是否扣除
shares_before = StakeUtils.getSpecifiedDelegationShares(userAddr, v0ValAddr)
share = shares_before/2

response = StakeUtils.redelegationSubmit(user, password, v0ValAddr, v1ValAddr, String.valueOf(share))
WS.verifyEqual(StringUtils.stringContains(response.responseBodyContent,"hash"), true)
AccountUtils.waitUntilSeveralBlock(3)

shares_after = StakeUtils.getSpecifiedDelegationShares(userAddr, v0ValAddr)

expect = share
actual = shares_before - shares_after
WS.verifyEqual(actual, expect)

redelegated_share = StakeUtils.getSpecifiedDelegationShares(userAddr, v1ValAddr)
WS.verifyEqual((redelegated_share>0), true)


//  redelegate 全部解绑，设置--shares-percent=1 , 验证shares是否扣除
redelegated_share_before = StakeUtils.getSpecifiedDelegationShares(userAddr, v1ValAddr)
response = StakeUtils.redelegationSubmit(user, password, v0ValAddr, v1ValAddr, String.valueOf(share))
WS.verifyEqual(StringUtils.stringContains(response.responseBodyContent,"hash"), true)
//AccountUtils.waitUntilSeveralBlock(3)
response = StakeUtils.delegationQueryByDelValAddr(userAddr, v0ValAddr)
WS.verifyEqual(StringUtils.stringContains(response.responseBodyContent,"no delegation for this"), true)
//shares_after = StakeUtils.getSpecifiedDelegationShares(userAddr, v0ValAddr)
redelegated_share_after = StakeUtils.getSpecifiedDelegationShares(userAddr, v1ValAddr)

//WS.verifyEqual(shares_after, 0) //全部转出 余额0shares

redelegated_share = redelegated_share_after - redelegated_share_before
WS.verifyEqual(redelegated_share, share)

