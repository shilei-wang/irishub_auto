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
import rest.StakeUtils
import com.google.gson.JsonParser
import com.google.gson.JsonObject

TestData faucet = findTestData('base/faucet')
String v0 = faucet.getValue('name', 1)

String password = faucet.getValue('password', 1)
String v0Addr= AccountUtils.getAddrByName(v0, "acc")

response = AccountUtils.authAccountInfo(v0Addr)
JsonParser jsonParser = new JsonParser();
JsonObject object1 = (JsonObject)jsonParser.parse(response.responseBodyContent)
WS.verifyEqual(object1.get("address").getAsString(), v0Addr)

String user = AccountUtils.createNewAccount("1iris")
String userAddr= AccountUtils.getAddrByName(user, "acc")

response = AccountUtils.authAccountInfo(userAddr)
JsonObject object2 = (JsonObject)jsonParser.parse(response.responseBodyContent)
WS.verifyEqual(object2.get("address").getAsString(), userAddr)


