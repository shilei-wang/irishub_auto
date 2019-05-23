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
import rest.BaseTx
import utils.CmdUtils as CmdUtils
import utils.StringUtils as StringUtils
import rest.AccountUtils
import com.google.gson.JsonObject as JsonObject
	

data = findTestData('gov/vote/IRISHUB-915')
faucet = findTestData('base/faucet')
v0 = faucet.getValue('name', 1)
v1 = faucet.getValue('name', 2)
password = faucet.getValue('password', 1)
v0_faa = AccountUtils.getAddrByName(v0, "acc")


for (int i = 1; i <= data.getRowNumbers() ; i++) {	
	balance_before = BaseTx.getAccountBalance(v0_faa)
	response = GovUtils.proposalBurnSubmitEx(v0, password, "1000iris")
	WS.verifyEqual(StringUtils.stringContains(response.responseBodyContent,"hash"), true)
	proposalId = GovUtils.getProposalId(response.responseBodyContent)

	GovUtils.proposalVoteSubmit(v0, password, proposalId, data.getValue("v0_option", i))
	WS.verifyEqual(StringUtils.stringContains(response.responseBodyContent,"hash"), true)
	
	GovUtils.proposalVoteSubmit(v1, password, proposalId, data.getValue("v1_option", i))
	WS.verifyEqual(StringUtils.stringContains(response.responseBodyContent,"hash"), true)
	
	sleep(12000)
	status = GovUtils.getProposalStatusByProposalId(proposalId)
	WS.verifyEqual(data.getValue("rest_result", i), status)
	
	balance_after = BaseTx.getAccountBalance(v0_faa)
	rate = 0
	if (i!=3) {
		//扣除20%
		rate = 0.2
	} else {
		//全部扣除
		rate = 1
	}
	CmdUtils.pl(balance_before)
	CmdUtils.pl(balance_after)
	actual   = (balance_before - balance_after)
	expected = 1000*rate
	WS.verifyEqual(CmdUtils.compareIgnoreFee(actual, expected), true)
}


