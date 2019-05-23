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
	

data = findTestData('gov/query-tally/IRISHUB-918')
faucet = findTestData('base/faucet')
v0 = faucet.getValue('name', 1)
v1 = faucet.getValue('name', 2)
password = faucet.getValue('password', 1)

String[] votes_result = new String[2]

for (int i = 1; i <= data.getRowNumbers() ; i++) {

	votes_result[0] = data.getValue("v0_option", i)
	votes_result[1] = data.getValue("v1_option", i)
	response = GovUtils.proposalBurnSubmitEx(v0, password, "1000iris")
	WS.verifyEqual(StringUtils.stringContains(response.responseBodyContent,"hash"), true)
	proposalId = GovUtils.getProposalId(response.responseBodyContent)
	
	GovUtils.proposalVoteSubmit(v0, password, proposalId, data.getValue("v0_option", i))
	WS.verifyEqual(StringUtils.stringContains(response.responseBodyContent,"hash"), true)
	GovUtils.proposalVoteSubmit(v1, password, proposalId, data.getValue("v1_option", i))
	WS.verifyEqual(StringUtils.stringContains(response.responseBodyContent,"hash"), true)
	
	response = GovUtils.tallyResultQueryById(proposalId)
	re = CmdUtils.Parse(response.responseBodyContent).getAsJsonObject()
	
	GovUtils.checkPower(votes_result, re, "yes")
	GovUtils.checkPower(votes_result, re, "no")
	GovUtils.checkPower(votes_result, re, "abstain")
	GovUtils.checkPower(votes_result, re, "no_with_veto")
	votes_result[0]=""
	votes_result[1]=""
}


