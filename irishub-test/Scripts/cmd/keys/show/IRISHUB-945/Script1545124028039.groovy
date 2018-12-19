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
import cmd.KeysUtils as KeysUtils

data = findTestData('base/faucet')
valName = data.getValue('name', 1)
password = data.getValue('password', 1)
valFaaAddr = CmdUtils.getAddressFromName(valName, "faa")
valFvaAddr = CmdUtils.getAddressFromName(valName, "fva")

WS.verifyEqual(valFaaAddr.contains("faa"), true)
WS.verifyEqual(valFvaAddr.contains("fva"), true)

userMap = KeysUtils.add(password)
println userMap.get("name")

userFaaAddr = CmdUtils.getAddressFromName(userMap.get("name"), "faa")
userFvaAddr = CmdUtils.getAddressFromName(userMap.get("name"), "fva")

WS.verifyEqual(userFaaAddr.contains("faa"), true)
WS.verifyEqual(userFvaAddr.contains("fva"), true)
