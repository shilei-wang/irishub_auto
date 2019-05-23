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

shares_before = StakeUtils.getSpecifiedDelegationShares(userAddr, v0ValAddr)
source_shares = shares_before/2
response = StakeUtils.redelegationSubmit(user, password, v0ValAddr, v1ValAddr, String.valueOf(source_shares))

response = StakeUtils.redelegationsQueryByValAddr(v0ValAddr)
array = CmdUtils.ParseArray(response.responseBodyContent)

index = StakeUtils.findItemIndexInDelegationsByDelAddr(array, userAddr)

item = array.get(index).getAsJsonObject()
WS.verifyEqual((index!=-1), true)
WS.verifyEqual(item.get("delegator_addr").getAsString(), userAddr)
WS.verifyEqual(item.get("validator_src_addr").getAsString(), v0ValAddr)
WS.verifyEqual(item.get("validator_dst_addr").getAsString(), v1ValAddr)
WS.verifyEqual(Double.valueOf(item.get("shares_src").getAsString()),source_shares)
WS.verifyEqual((Double.valueOf(item.get("shares_dst").getAsString())>0),true)


