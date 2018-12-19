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
command = 'iriscli bank account '+CmdUtils.getAddressFromName(v0,"faa").concat(' --node=').concat(GlobalVariable.node)
//command ="iriscli service define --chain-id="+GlobalVariable.chainId+" --node="+GlobalVariable.node+" --from=node0 --fee=0.004iris --gas=200000 --service-name=001_service_470r78ew81d38 --service-description=service-description --author-description=author-description --tags=tag1 tag2 --idl-content="+GlobalVariable.debug+" --file="+GlobalVariable.cmdFilePath+"auto_test.proto"                     
re = CmdUtils.sendRequest('cmd/CmdWithOneArgs', command, "wait")
CmdUtils.pl(re.responseBodyContent)
 