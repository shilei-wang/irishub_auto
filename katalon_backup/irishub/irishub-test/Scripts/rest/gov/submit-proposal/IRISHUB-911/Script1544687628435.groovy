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
import rest.AccountUtils
import utils.CmdUtils as CmdUtils
import utils.StringUtils as StringUtils

import com.google.gson.JsonObject as JsonObject;

data = findTestData('gov/submit-proposal/IRISHUB-911')
faucet = findTestData('base/faucet')
v1 = faucet.getValue('name', 2)
password = faucet.getValue('password', 1)
v1_faa = AccountUtils.getAddrByName(v1, "acc")

//user = AccountUtils.createNewAccount("5000iris")
//user_faa = AccountUtils.getAddrByName(user, "acc")

//case 1 type="TxTaxUsage" 抵押300iris. (达到初始抵押金额， 不到最小抵押金额)
//验证 ：   1. 提议进入deposit peroid
//  	  2. 抵押的300iris被从账户扣除
//        3. 等待deposit period （10s），验证300iris不会被退回

balance_before = BaseTx.getAccountBalance(v1_faa)
//String proposal_type, String initial_deposit, String percent, String usage
response = GovUtils.proposalSubmit(v1, password, "t", "t", "TxTaxUsage", "300iris", "0.0000000001", "Burn", "nothing")
WS.verifyEqual(StringUtils.stringContains(response.responseBodyContent,"hash"), true)
String proposalId = GovUtils.getProposalId(response.responseBodyContent)
status = GovUtils.getProposalStatusByProposalId(proposalId)
WS.verifyEqual(status, "DepositPeriod")
balance_after = BaseTx.getAccountBalance(v1_faa)
actual   = (balance_before - balance_after)
expected = 300
WS.verifyEqual(CmdUtils.compareIgnoreFee(actual, expected), true)

sleep(12000)
balance_end = BaseTx.getAccountBalance(v1_faa)
WS.verifyEqual(CmdUtils.compareIgnoreFee(balance_after, balance_end), true)

//type="TxTaxUsage" 抵押1000iris. (大于最小抵押金额，验证进入voting peroid)
//验证 ：   1. 验证进入voting peroid
//  	  2. 抵押的1000iris被从账户扣除
//        3. 等待voting period （10s），验证20% 200iris被扣除，80% 800iris被退回
balance_before = BaseTx.getAccountBalance(v1_faa)
response = GovUtils.proposalSubmit(v1, password, "t", "t", "TxTaxUsage", "1000iris", "0.0000000001", "Burn", "nothing")
WS.verifyEqual(StringUtils.stringContains(response.responseBodyContent,"hash"), true)
proposalId = GovUtils.getProposalId(response.responseBodyContent)
status = GovUtils.getProposalStatusByProposalId(proposalId)

WS.verifyEqual(status, "VotingPeriod")
balance_after = BaseTx.getAccountBalance(v1_faa)

actual   = (balance_before - balance_after)
expected = 1000
WS.verifyEqual(CmdUtils.compareIgnoreFee(actual, expected), true)

sleep(12000)
balance_end = BaseTx.getAccountBalance(v1_faa)
actual   = (balance_end - balance_after)
expected = 1000*(1-0.2)
WS.verifyEqual(CmdUtils.compareIgnoreFee(actual, expected), true)


//type="ParameterChange"  抵押2000iris.(大于最小抵押金额，验证进入voting peroid)
//验证 ：   1. 验证进入voting peroid
response = GovUtils.proposalSubmit(v1, password, "t", "t", "ParameterChange", "2000iris", "nothing", "nothing", "auth/txSizeLimit=1000")
WS.verifyEqual(StringUtils.stringContains(response.responseBodyContent,"hash"), true)
proposalId = GovUtils.getProposalId(response.responseBodyContent)
status = GovUtils.getProposalStatusByProposalId(proposalId)
WS.verifyEqual(status, "VotingPeriod")


/*
//type="SoftwareUpgrade"   抵押1200iris.(达到初始抵押金额， 不到最小抵押金额)
//验证 ：    1. 提议进入deposit peroid
response = GovUtils.UpdateProposalSubmit(v1, password, "t", "t", "SoftwareUpgrade", "1200iris", "1", "t", "500000")
WS.verifyEqual(StringUtils.stringContains(response.responseBodyContent,"hash"), true)
proposalId = GovUtils.getProposalId(response.responseBodyContent)
status = GovUtils.getProposalStatusByProposalId(proposalId)
WS.verifyEqual(status, "DepositPeriod")
*/

/*

for (int i = 1; i <= data.getRowNumbers(); i++) {
	//String title, String description, String proposal_type, String proposer, String initial_deposit
	response = GovUtils.proposalSubmit(v0, password, data.getValue("title",i), data.getValue("description",i), data.getValue("type",i), data.getValue("deposit",i))
	WS.verifyEqual(StringUtils.stringContains(response.responseBodyContent,"hash"), true)
	response = GovUtils.proposalsQueryByID(GovUtils.getProposalId(response.responseBodyContent))
	status = GovUtils.getProposalStatusFromResponse(response.responseBodyContent)
	WS.verifyEqual(data.getValue("cmd_result", i), status)
}
	
*/
