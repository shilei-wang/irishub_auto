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
TestData faucet = findTestData('base/faucet')
String v0 = faucet.getValue('name', 1)
String password = faucet.getValue('password', 1)
String v0ValAddr = map.get(v0)
String address = GetAddressByKey.getAddressByKey(v0)
println v0ValAddr

user = AccountUtils.createNewAccount("2.1iris")
userAddr = GetAddressByKey.getAddressByKey(user)

response = StakeUtils.delegationSubmit(user, password, v0ValAddr, "1iris")
JsonObject re = CmdUtils.Parse(response.responseBodyContent)
String hash = re.get("hash").getAsString()
println hash

response2 = StakeUtils.stakingTxsQuery(userAddr)

WS.verifyEqual(response2.responseBodyContent.contains(hash), true)
WS.verifyEqual(response2.responseBodyContent.contains(userAddr), true)
WS.verifyEqual(response2.responseBodyContent.contains(v0ValAddr), true)