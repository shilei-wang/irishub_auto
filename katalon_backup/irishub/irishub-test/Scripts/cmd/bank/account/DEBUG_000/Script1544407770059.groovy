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
import cmd.GuardianUtils as GuardianUtils
import cmd.DistributionUtils as DistributionUtils

//for   test

TestData faucet = findTestData('base/faucet')
v0 = faucet.getValue('name', 1)
command ="iriscli bank send".concat(GlobalVariable.chainId).concat(GlobalVariable.node)+" --amount=1iris --to="+GlobalVariable.address_aa+" --from=node0 --fee=0.4iris --commit --gas=50000"                     
re = CmdUtils.sendRequest('cmd/CmdWithOneArgs', command, "wait")
WS.verifyEqual(StringUtils.stringContains(re.responseBodyContent,"tx hash"), true)
//CmdUtils.pl(re.responseBodyContent)
 