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
	

data = findTestData('gov/query-vote-s/IRISHUB-916')
faucet = findTestData('base/faucet')
v0 = faucet.getValue('name', 1)
v1 = faucet.getValue('name', 2)
password = faucet.getValue('password', 1)
String v0Addr = AccountUtils.getAddrByName(v0, "acc")
String v1Addr = AccountUtils.getAddrByName(v1, "acc")

Map<String, String> vote_map = new HashMap<String, String>()
vote_map.put(v0Addr, data.getValue("v0_option", 1))
vote_map.put(v1Addr, data.getValue("v1_option", 1))


response = GovUtils.proposalBurnSubmitEx(v0, password, "1000iris")
WS.verifyEqual(StringUtils.stringContains(response.responseBodyContent,"hash"), true)
proposalId = GovUtils.getProposalId(response.responseBodyContent)

GovUtils.proposalVoteSubmit(v0, password, proposalId, data.getValue("v0_option", 1))
WS.verifyEqual(StringUtils.stringContains(response.responseBodyContent,"hash"), true)

GovUtils.proposalVoteSubmit(v1, password, proposalId, data.getValue("v1_option", 1))
WS.verifyEqual(StringUtils.stringContains(response.responseBodyContent,"hash"), true)

//根据 voter， proposal-id 执行 query-vote 对投票进行查询
i=1
response = GovUtils.voteQueryByAddrId(proposalId, v0Addr)
WS.verifyEqual(StringUtils.getValueFromKey(response.responseBodyContent, "option"), data.getValue("v0_option", 1))

response = GovUtils.voteQueryByAddrId(proposalId, v1Addr)
WS.verifyEqual(StringUtils.getValueFromKey(response.responseBodyContent, "option"), data.getValue("v1_option", 1))

//根据 proposal-id 执行 query-votes 对投票进行查询
i=2
response = GovUtils.votesQueryById(proposalId)
vote_array = CmdUtils.ParseArray(response.responseBodyContent)

for (int i = 0; i < vote_array.size() ; i++) {
	vote_item = vote_array.get(i).getAsJsonObject()
	voterAddr = vote_item.get("voter").getAsString()
	WS.verifyEqual(vote_item.get("option").getAsString(), vote_map.get(voterAddr))
	WS.verifyEqual(vote_item.get("proposal_id").getAsString(), proposalId)
}

