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

import com.google.gson.JsonObject as JsonObject;

data = findTestData('gov/query-proposal-s/IRISHUB-913')
faucet = findTestData('base/faucet')
v0 = faucet.getValue('name', 1)
password = faucet.getValue('password', 1)

i = 1
//String title, String description, String proposal_type, String proposer, String initial_deposit
response = GovUtils.proposalSubmit(v0, password, data.getValue("title",i), data.getValue("description",i), data.getValue("type",i), data.getValue("deposit",i))
WS.verifyEqual(StringUtils.stringContains(response.responseBodyContent,"hash"), true)
String proposalId = GovUtils.getProposalId(response.responseBodyContent)

response = GovUtils.proposalsQueryByID(proposalId)
re = CmdUtils.Parse(response.responseBodyContent).getAsJsonObject()
WS.verifyEqual(re.get("proposal_id").getAsString(), proposalId)
WS.verifyEqual(re.get("title").getAsString(), data.getValue("title", i))
WS.verifyEqual(re.get("description").getAsString(), data.getValue("description", i))
WS.verifyEqual(re.get("proposal_type").getAsString(), data.getValue("type", i))
WS.verifyEqual(re.get("proposal_status").getAsString(), data.getValue("cmd_result", i))


deposit_array = re.get("total_deposit").getAsJsonArray()
actual   = Double.valueOf(CmdUtils.IrisattoToIris(deposit_array.get(0).getAsJsonObject().get("amount").getAsString()).replace("iris", ""))
expected = Double.valueOf(data.getValue("deposit", i).replace("iris", ""))
WS.verifyEqual(actual,expected)


i = 2
//String title, String description, String proposal_type, String proposer, String initial_deposit
response = GovUtils.proposalSubmit(v0, password, data.getValue("title",i), data.getValue("description",i), data.getValue("type",i), data.getValue("deposit",i))
WS.verifyEqual(StringUtils.stringContains(response.responseBodyContent,"hash"), true)
proposal_id = GovUtils.getProposalId(response.responseBodyContent)

response = GovUtils.proposalsQuery("nothing", "nothing", "nothing", "1")
WS.verifyEqual(StringUtils.stringContains(response.responseBodyContent,proposal_id), true)
WS.verifyEqual(StringUtils.stringContains(response.responseBodyContent,data.getValue("title", i)), true)

