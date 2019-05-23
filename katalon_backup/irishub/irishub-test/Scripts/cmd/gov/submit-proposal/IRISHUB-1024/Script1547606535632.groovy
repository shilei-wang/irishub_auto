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
import cmd.GovUtils as GovUtils
import cmd.GuardianUtils as GuardianUtils
import utils.CmdUtils as CmdUtils
import utils.StringUtils as StringUtils


import com.google.gson.JsonObject as JsonObject;
	
Utils u = new Utils();

//1. 设置 type="TxTaxUsage" 抵押299iris. (不到初始抵押金额)
//验证 ：   1. 提议被拒绝
cmd = u.command+" --usage=Burn --percent=0.0000000001 --type=TxTaxUsage --deposit=299iris"
response = CmdUtils.sendRequest('cmd/CmdWithOneArgs', cmd, "wait")
WS.verifyEqual(StringUtils.stringContains(response.responseBodyContent,"less than minInitialDeposit"), true)

//2. 用非profiler用户提起SoftwareUpgrade提议， type="SoftwareUpgrade" 抵押1200iris.
//验证 ：   1. 提议被拒绝
cmd = u.command+" --software=t --version=1 --switch-height=50000000 --type=SoftwareUpgrade --deposit=1200iris"
response = CmdUtils.sendRequest('cmd/CmdWithOneArgs', cmd, "wait")
WS.verifyEqual(StringUtils.stringContains(response.responseBodyContent,"not a profiler address"), true)


//type="TxTaxUsage"  --dest-address设置非trustee账户
//验证 ：   1. 提议被拒绝
cmd = u.command+" --deposit=300iris --usage=Distribute --percent=0.1 --type=TxTaxUsage --dest-address="+u.dest_address
response = CmdUtils.sendRequest('cmd/CmdWithOneArgs', cmd, "wait")
WS.verifyEqual(StringUtils.stringContains(response.responseBodyContent,"not a trustee address"), true)


//type="TxTaxUsage"  description长度大于280
//验证 ：   1. 提议被拒绝
cmd = u.command+" --deposit=300iris --usage=Burn --percent=0.1 --type=TxTaxUsage "
cmd = cmd.replace("test_title", StringUtils.generateRandomString(71))
response = CmdUtils.sendRequest('cmd/CmdWithOneArgs', cmd, "wait")
WS.verifyEqual(StringUtils.stringContains(response.responseBodyContent,"bad length for title"), true)


//type="TxTaxUsage"  description长度大于280
//验证 ：   1. 提议被拒绝
cmd = u.command+" --deposit=300iris --usage=Burn --percent=0.1 --type=TxTaxUsage "
cmd = cmd.replace("test_description", StringUtils.generateRandomString(281))
response = CmdUtils.sendRequest('cmd/CmdWithOneArgs', cmd, "wait")
WS.verifyEqual(StringUtils.stringContains(response.responseBodyContent,"bad length for description"), true)


/*	//CmdUtils.pl(response.responseBodyContent)
 * utils
 */

class Utils {
	public TestData td;
	public String command;
	public String v0;
	public String user;
	public String dest_address;
	
	public Utils(){
		if (CmdUtils.isMainnet()) {
			dest_address = "iaa13nzsae74qype65rshc0wyvhk9s0l3uecwf8y93"
		} else {
			dest_address = "faa1lcuw6ewd2gfxap37sejewmta205sgssmv5fnju"
		}
		
		
		td = findTestData('gov/submit-proposal/IRISHUB-911')
		TestData faucet = findTestData('base/faucet')
		v0 = faucet.getValue('name', 1)
		user = CmdUtils.createNewAccount(v0,"5000iris")
		
		command = 'iriscli gov submit-proposal --description=test_description --title=test_title'.concat(GlobalVariable.chainId)
			.concat(GlobalVariable.node).concat(' --from='+user)
		command = CmdUtils.addTxFee(command, findTestData('base/tx'), 1)
	}
}

