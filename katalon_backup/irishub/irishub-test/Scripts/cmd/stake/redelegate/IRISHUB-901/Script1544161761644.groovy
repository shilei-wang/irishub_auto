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
import com.kms.katalon.core.testobject.ResponseObject as ResponseObject
import com.kms.katalon.core.webservice.keyword.WSBuiltInKeywords as WS
import com.kms.katalon.core.webui.keyword.WebUiBuiltInKeywords as WebUI


import internal.GlobalVariable as GlobalVariable
import utils.CmdUtils
import utils.StringUtils as StringUtils
import cmd.GuardianUtils as GuardianUtils
import cmd.DistributionUtils as DistributionUtils
import cmd.StakeUtils as StakeUtils
import com.google.gson.JsonObject as JsonObject;

Utils u = new Utils();

//  redelegate 部分解绑，设置--shares-amount=1/2的delegation , 验证shares是否扣除
shares_before = StakeUtils.delegation(u.user,u.v0)
share = CmdUtils.round(shares_before/2,8,BigDecimal.ROUND_HALF_UP)
cmd = u.command + " --shares-amount="+String.valueOf(share)
response = CmdUtils.sendRequest('cmd/CmdWithOneArgs', cmd, "wait")
WS.verifyEqual(StringUtils.stringContains(response.responseBodyContent,"tx hash"), true)
CmdUtils.waitUntilSeveralBlock(3)
shares_after = StakeUtils.delegation(u.user,u.v0)
expect = share
actual = CmdUtils.round(shares_before - shares_after,8,BigDecimal.ROUND_HALF_UP)
//CmdUtils.pl(expect)
//CmdUtils.pl(actual)

WS.verifyEqual(actual, expect)

redelegated_share = StakeUtils.delegation(u.user,u.v1)
WS.verifyEqual((redelegated_share>0), true)

//主网版本 受unbound时间限制 ，不能连续redelegate2次
if (!CmdUtils.isMainnet()) { 
	//  redelegate 全部解绑，设置--shares-percent=1 , 验证shares是否扣除
	redelegated_share_before = StakeUtils.delegation(u.user,u.v1)
	cmd = u.command + " --shares-percent=1"
	response = CmdUtils.sendRequest('cmd/CmdWithOneArgs', cmd, "wait")
	WS.verifyEqual(StringUtils.stringContains(response.responseBodyContent,"tx hash"), true)
	CmdUtils.waitUntilSeveralBlock(3)
	shares_after = StakeUtils.delegation(u.user,u.v0)
	redelegated_share_after = StakeUtils.delegation(u.user,u.v1)
	
	WS.verifyEqual(shares_after, 0) //全部转出 余额0shares
	
	redelegated_share = redelegated_share_after - redelegated_share_before
	WS.verifyEqual((redelegated_share>0), true)
}

/*	//CmdUtils.pl(response.responseBodyContent)
 * utils
 */

class Utils {
	public String command;
	public String v0;
	public String v1;
	public String v0_fva;
	public String v1_fva;
	public String user;

	public Utils(){
		TestData faucet = findTestData('base/faucet')
		v0 = faucet.getValue('name', 1)
		v1 = faucet.getValue('name', 2)
		v0_fva = CmdUtils.getAddressFromName(v0, "fva")
		v1_fva = CmdUtils.getAddressFromName(v1, "fva")	
		
		user = CmdUtils.createNewAccount(v0,"6iris")
		StakeUtils.delegate(user, v0, "5iris")
		
		command = 'iriscli stake redelegate'.concat(GlobalVariable.chainId).concat(GlobalVariable.node)
			.concat(' --from=').concat(user).concat(" --address-validator-source=").concat(v0_fva).concat(" --address-validator-dest=").concat(v1_fva)
		command = CmdUtils.addTxFee(command, findTestData('base/tx'), 1)	
	}
}
