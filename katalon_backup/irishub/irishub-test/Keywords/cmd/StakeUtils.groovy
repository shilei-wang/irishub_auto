package cmd

import static com.kms.katalon.core.checkpoint.CheckpointFactory.findCheckpoint
import static com.kms.katalon.core.testcase.TestCaseFactory.findTestCase
import static com.kms.katalon.core.testdata.TestDataFactory.findTestData
import static com.kms.katalon.core.testobject.ObjectRepository.findTestObject

import com.google.gson.JsonArray
import com.google.gson.JsonObject
import com.kms.katalon.core.annotation.Keyword
import com.kms.katalon.core.checkpoint.Checkpoint
import com.kms.katalon.core.cucumber.keyword.CucumberBuiltinKeywords as CucumberKW
import com.kms.katalon.core.mobile.keyword.MobileBuiltInKeywords as Mobile
import com.kms.katalon.core.model.FailureHandling
import com.kms.katalon.core.testcase.TestCase
import com.kms.katalon.core.testdata.TestData
import com.kms.katalon.core.testobject.TestObject
import com.kms.katalon.core.testobject.ResponseObject as ResponseObject
import com.kms.katalon.core.webservice.keyword.WSBuiltInKeywords as WS
import com.kms.katalon.core.webui.keyword.WebUiBuiltInKeywords as WebUI

import internal.GlobalVariable
import utils.CmdUtils as CmdUtils
import utils.StringUtils as StringUtils

public class StakeUtils {
	@Keyword
	public static delegate(String source, String dest, String amount){
		dest=CmdUtils.getAddressFromName(dest, "fva")
		String cmd = "iriscli stake delegate".concat(GlobalVariable.chainId).concat(GlobalVariable.node)+" --from="+source+" --address-validator="+dest+" --amount="+amount
		cmd = CmdUtils.addTxFee(cmd, findTestData('base/tx'), 1)
		ResponseObject response = CmdUtils.sendRequest('cmd/CmdWithOneArgs', cmd, "wait")
		WS.verifyEqual(StringUtils.stringContains(response.responseBodyContent,"tx hash"), true)
	}

	@Keyword
	public static redelegate(String account, String source, String dest, Double shares){
		source=CmdUtils.getAddressFromName(source, "fva")
		dest=CmdUtils.getAddressFromName(dest, "fva")
		String cmd = 'iriscli stake redelegate'.concat(GlobalVariable.chainId).concat(GlobalVariable.node)
				.concat(' --from=').concat(account).concat(" --address-validator-source=").concat(source).concat(" --address-validator-dest=")
				.concat(dest)+ " --shares-amount="+String.valueOf(shares)
		cmd = CmdUtils.addTxFee(cmd, findTestData('base/tx'), 1)
		ResponseObject response = CmdUtils.sendRequest('cmd/CmdWithOneArgs', cmd, "wait")
		WS.verifyEqual(StringUtils.stringContains(response.responseBodyContent,"tx hash"), true)
	}

	@Keyword
	public static unbond(String source, String dest, Double share){
		Double max_share = delegation(source, dest)
		if (share > max_share){
			share = max_share
		}

		dest=CmdUtils.getAddressFromName(dest, "fva")
		String cmd = 'iriscli stake unbond begin'.concat(GlobalVariable.chainId).concat(GlobalVariable.node)
				.concat(' --from=').concat(source).concat(" --address-validator=").concat(dest).concat(" --shares-amount=")+String.valueOf(share)
		cmd = CmdUtils.addTxFee(cmd, findTestData('base/tx'), 1)
		ResponseObject response = CmdUtils.sendRequest('cmd/CmdWithOneArgs', cmd, "wait")
		WS.verifyEqual(StringUtils.stringContains(response.responseBodyContent,"tx hash"), true)
	}

	@Keyword
	public static int findItemInDelegations(JsonArray array, String addr, String key){
		for (int i = 0; i < array.size() ; i++) {
			JsonObject item = array.get(i).getAsJsonObject()
			if (item.get(key).getAsString() == addr){
				return i
			}
		}

		return -1
	}

	@Keyword
	public static Double delegation(String source, String dest){
		String faa = CmdUtils.getAddressFromName(source, "faa")
		String fva = CmdUtils.getAddressFromName(dest, "fva")
		String cmd ='iriscli stake delegation'.concat(GlobalVariable.node).concat(GlobalVariable.json)+" --address-delegator="+faa+" --address-validator="+fva
		ResponseObject response = CmdUtils.sendRequest('cmd/CmdWithOneArgs', cmd, "sync")

		if (StringUtils.stringContains(response.responseBodyContent,"no delegation")){
			return 0
		}

		Double shares = getSharesFromDelegation(response.responseBodyContent)
		return shares
	}

	@Keyword
	public static Double getSharesFromDelegation(String msg){
		String shares = CmdUtils.Parse(msg).get("shares").getAsString()
		return  Double.valueOf(shares)
	}

	@Keyword
	public static Double getSourceSharesFromRedelegation(String msg){
		String share = CmdUtils.Parse(msg).get("shares_src").getAsString()
		return  Double.valueOf(share)
	}

	@Keyword
	public static Double getDestinationSharesFromRedelegation(String msg){
		String share = CmdUtils.Parse(msg).get("shares_dst").getAsString()
		return  Double.valueOf(share)
	}

	@Keyword
	public static Double unbondingDelegation(String source, String dest){
		String faa = CmdUtils.getAddressFromName(source, "faa")
		String fva = CmdUtils.getAddressFromName(dest, "fva")
		String cmd = 'iriscli stake unbonding-delegation'.concat(GlobalVariable.chainId).concat(GlobalVariable.node).concat(GlobalVariable.json)
				.concat(" --address-delegator=").concat(faa).concat(" --address-validator=").concat(fva)
		ResponseObject response = CmdUtils.sendRequest('cmd/CmdWithOneArgs', cmd, "sync")

		if (StringUtils.stringContains(response.responseBodyContent,"no unbonding delegation found")){
			return 0
		}

		Double shares = getExpectedBalanceFromUnbonding(response.responseBodyContent)
		return shares
	}

	@Keyword
	public static Double getExpectedBalanceFromUnbonding(String msg){
		String balance = CmdUtils.Parse(msg).get("balance").getAsString().replace("iris", "")
		return  Double.valueOf(balance)
	}

	@Keyword
	public static String getSeedFvp(String account){
		JsonObject re = CmdUtils.Parse(getValidatorInfo(account)).getAsJsonObject()
		return re.get("consensus_pubkey").getAsString()
	}


	@Keyword
	public static String getValidatorInfo(String account){
		String fva = CmdUtils.getAddressFromName(account, "fva")
		String cmd = 'iriscli stake validator'.concat(GlobalVariable.node).concat(GlobalVariable.json).concat(" ").concat(fva)
		ResponseObject response = CmdUtils.sendRequest('cmd/CmdWithOneArgs', cmd, "sync")

		return  response.responseBodyContent
	}

	@Keyword
	public static Double getCommissionRate(String account){
		String msg = getValidatorInfo(account)
		String rate = CmdUtils.Parse(msg).get("commission").getAsJsonObject().get("rate").getAsString()
		return  Double.valueOf(rate)
	}
}
