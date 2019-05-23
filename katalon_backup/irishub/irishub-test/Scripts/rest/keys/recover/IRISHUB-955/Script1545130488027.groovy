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
import utils.StringUtils as StringUtils
import rest.AccountUtils
import groovy.json.JsonSlurper

password = findTestData('base/faucet').getValue('password', 1)
String userName1 = "user_"+ CmdUtils.generateRandomID()
Map user1 = AccountUtils.addNewKey(userName1, password)

//case 1
String userName2 = "user_"+ CmdUtils.generateRandomID()
response = AccountUtils.recoverKey(userName2, password, user1.get("seed"))
WS.verifyEqual(response.getStatusCode(), 200)

JsonSlurper slurper = new JsonSlurper()
Map user2 = (Map)slurper.parseText(response.responseBodyContent)
WS.verifyEqual(user2.get("pub_key"), user1.get("pub_key"))
WS.verifyEqual(user2.get("address"), user1.get("address"))


//case 2
String userName3 = "user_"+ CmdUtils.generateRandomID()
Map user3 = AccountUtils.addNewKey(userName3, password)
response = AccountUtils.deleteKey(userName3, password)
WS.verifyEqual(response.getStatusCode(), 200)

response = AccountUtils.recoverKey(userName3, password, user3.get("seed"))
WS.verifyEqual(response.getStatusCode(), 200)

Map user3_after = (Map)slurper.parseText(response.responseBodyContent)
WS.verifyEqual(user3_after.get("pub_key"), user3.get("pub_key"))
WS.verifyEqual(user3_after.get("address"), user3.get("address"))


