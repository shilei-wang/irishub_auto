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

data = findTestData('gov/submit-proposal/IRISHUB-911')
faucet = findTestData('base/faucet')
v0 = faucet.getValue('name', 1)
password = faucet.getValue('password', 1)

for (int i = 1; i <= data.getRowNumbers(); i++) {
	//String title, String description, String proposal_type, String proposer, String initial_deposit
	response = GovUtils.proposalSubmit(v0, password, data.getValue("title",i), data.getValue("description",i), data.getValue("type",i), data.getValue("deposit",i))
	WS.verifyEqual(StringUtils.stringContains(response.responseBodyContent,"hash"), true)
	response = GovUtils.proposalsQueryByID(GovUtils.getProposalId(response.responseBodyContent))
	status = GovUtils.getProposalStatusFromResponse(response.responseBodyContent)
	WS.verifyEqual(data.getValue("cmd_result", i), status)
}
	

