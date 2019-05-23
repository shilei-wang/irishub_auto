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

Utils u = new Utils();

//编辑validator信息 --details="a1_" --moniker="B-2" --website="3_C" (不修改commission-rate)
cmd = CmdUtils.generateCmd(u.command, u.td, 1)
response = CmdUtils.sendRequest('cmd/CmdWithOneArgs', cmd, "wait")
WS.verifyEqual(StringUtils.stringContains(response.responseBodyContent,"tx hash"), true)	
re = StakeUtils.getValidatorInfo(u.v0)
WS.verifyEqual(StringUtils.stringContains(re,u.td.getValue("details", 1)), true)
WS.verifyEqual(StringUtils.stringContains(re,u.td.getValue("moniker", 1)), true)
WS.verifyEqual(StringUtils.stringContains(re,u.td.getValue("website", 1)), true)

//编辑validator信息 commission-rate=当前rate+0.001, --details="" --moniker="" --website=""
cmd = CmdUtils.generateCmd(u.command, u.td, 2)
rate = String.format("%.5f", (StakeUtils.getCommissionRate(u.v0)+0.0001))
cmd = cmd + " --commission-rate="+rate
response = CmdUtils.sendRequest('cmd/CmdWithOneArgs', cmd, "wait")
re = response.responseBodyContent

//注意 24小时内只能修改一次
if (StringUtils.stringContains(re,"tx hash")) {	
	actual = StakeUtils.getCommissionRate(u.v0)
	except = Double.valueOf(rate)
	WS.verifyEqual(actual, except)
} else {
	WS.verifyEqual(StringUtils.stringContains(re,"commission cannot be changed more than once in 24h"), true)
}

//
/*	//CmdUtils.pl(response.responseBodyContent)
 * utils
 */

class Utils {
	public TestData td;
	public String command;
	public String v0;
	public String v0_fva;
	
	public Utils(){
		td = findTestData('stake/edit-validator/IRISHUB-904')
		TestData faucet = findTestData('base/faucet')
		v0 = faucet.getValue('name', 1)
		v0_fva = CmdUtils.getAddressFromName(v0, "fva")

		command = 'iriscli stake edit-validator'.concat(GlobalVariable.chainId).concat(GlobalVariable.node).concat(' --from=').concat(v0)	
		command = CmdUtils.addTxFee(command, findTestData('base/tx'), 1)
	}
}
