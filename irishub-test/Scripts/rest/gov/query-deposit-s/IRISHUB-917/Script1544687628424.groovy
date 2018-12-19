import com.kms.katalon.core.webui.keyword.WebUiBuiltInKeywords as WebUI
import com.kms.katalon.core.mobile.keyword.MobileBuiltInKeywords as Mobile
import com.kms.katalon.core.cucumber.keyword.CucumberBuiltinKeywords as CucumberKW
import com.kms.katalon.core.webservice.keyword.WSBuiltInKeywords as WS
import static com.kms.katalon.core.testobject.ObjectRepository.findTestObject
import static com.kms.katalon.core.testdata.TestDataFactory.findTestData
import static com.kms.katalon.core.testcase.TestCaseFactory.findTestCase
import static com.kms.katalon.core.checkpoint.CheckpointFactory.findCheckpoint
import com.kms.katalon.core.model.FailureHandling as FailureHandling
import com.kms.katalon.core.testcase.TestCase as TestCase
import com.kms.katalon.core.testdata.TestData as TestData
import com.kms.katalon.core.testobject.ResponseObject as ResponseObject
import com.kms.katalon.core.testobject.TestObject as TestObject
import com.kms.katalon.core.checkpoint.Checkpoint as Checkpoint

import internal.GlobalVariable as GlobalVariable
import rest.GovUtils
import utils.CmdUtils as CmdUtils
import utils.StringUtils as StringUtils
import rest.AccountUtils
import com.google.gson.JsonObject as JsonObject
	

data = findTestData('gov/query-deposit-s/IRISHUB-917')
faucet = findTestData('base/faucet')
v0 = faucet.getValue('name', 1)
password = faucet.getValue('password', 1)

response = GovUtils.proposalSubmit(v0, password, "test", "test", "Text", data.getValue("submit_deposit", 1))

WS.verifyEqual(StringUtils.stringContains(response.responseBodyContent,"hash"), true)
proposalId = GovUtils.getProposalId(response.responseBodyContent)
response = GovUtils.depositSubmit(v0, password, proposalId, data.getValue("deposit", 1))
WS.verifyEqual(StringUtils.stringContains(response.responseBodyContent,"hash"), true)

response = GovUtils.depositsQueryById(proposalId)
actual   = Double.valueOf(CmdUtils.IrisattoToIris(StringUtils.getValueFromKey(response.responseBodyContent, "amount")).replace("iris", ""))
expected = Double.valueOf(data.getValue("rest_result", 1).replace("iris", ""))
WS.verifyEqual(actual,expected)

String addr = AccountUtils.getAddrByName(v0, "acc")
response = GovUtils.depositQueryByAddrId(proposalId, addr)
actual   = Double.valueOf(CmdUtils.IrisattoToIris(StringUtils.getValueFromKey(response.responseBodyContent, "amount")).replace("iris", ""))
expected = Double.valueOf(data.getValue("rest_result", 1).replace("iris", ""))
WS.verifyEqual(actual,expected)


