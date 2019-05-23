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
import cmd.ServiceUtils as ServiceUtils


Utils u = new Utils();

//测试bank-send
cmd ="iriscli bank send"+GlobalVariable.chainId+" --amount=1iris --from="+u.v0+" --to="+u.v0_faa+GlobalVariable.node
cmd = CmdUtils.addTxFee(cmd, findTestData('base/tx'), 1)
response = CmdUtils.sendRequest('cmd/CmdWithOneArgs', cmd, "wait")
WS.verifyEqual(StringUtils.stringContains(response.responseBodyContent,"tx hash"), true)
TxHash = CmdUtils.getTxHash(response.responseBodyContent)
cmd = u.command + TxHash
response = CmdUtils.sendRequest('cmd/CmdWithOneArgs', cmd, "sync")
//CmdUtils.pl(response.responseBodyContent)
WS.verifyEqual(StringUtils.stringContains(response.responseBodyContent,"irishub/bank/Send"), true)


//测试Stake-delegate
cmd = "iriscli stake delegate"+GlobalVariable.chainId+GlobalVariable.node+" --from="+u.v0+" --address-validator="+u.v0_fva+" --amount=0.0001iris"
cmd = CmdUtils.addTxFee(cmd, findTestData('base/tx'), 1)
response = CmdUtils.sendRequest('cmd/CmdWithOneArgs', cmd, "wait")
WS.verifyEqual(StringUtils.stringContains(response.responseBodyContent,"tx hash"), true)		
TxHash = CmdUtils.getTxHash(response.responseBodyContent)
cmd = u.command + TxHash
response = CmdUtils.sendRequest('cmd/CmdWithOneArgs', cmd, "sync")
//CmdUtils.pl(response.responseBodyContent)
WS.verifyEqual(StringUtils.stringContains(response.responseBodyContent,"irishub/stake/MsgDelegate"), true)


//测试gov-submit-proposal
cmd = 'iriscli gov submit-proposal --title=t --description=t --type=TxTaxUsage --usage=Burn --percent=0.0000000001'.concat(GlobalVariable.chainId).concat(GlobalVariable.node).concat(' --from=').concat(u.v0)+" --deposit=300iris"
cmd = CmdUtils.addTxFee(cmd, findTestData('base/tx'), 1)
response = CmdUtils.sendRequest('cmd/CmdWithOneArgs', cmd, "wait")
WS.verifyEqual(StringUtils.stringContains(response.responseBodyContent,"tx hash"), true)
TxHash = CmdUtils.getTxHash(response.responseBodyContent)
cmd = u.command + TxHash
response = CmdUtils.sendRequest('cmd/CmdWithOneArgs', cmd, "sync")
//CmdUtils.pl(response.responseBodyContent)
WS.verifyEqual(StringUtils.stringContains(response.responseBodyContent,"irishub/gov/MsgSubmitTxTaxUsageProposal"), true)


//测试service-define
serviceName = ServiceUtils.generateRandomServiceName()
cmd = 'iriscli service define --service-description=service-description --author-description=author-description --tags=tag1,tag2 --idl-content=idl-content'.concat(GlobalVariable.chainId).concat(GlobalVariable.node).concat(
		' --from=').concat(u.v0)+" --service-name="+serviceName+" --file="+GlobalVariable.cmdFilePath + "auto_test.proto"
cmd = CmdUtils.addTxFee(cmd, findTestData('base/tx'), 1)
response = CmdUtils.sendRequest('cmd/CmdWithOneArgs', cmd, "wait")
WS.verifyEqual(StringUtils.stringContains(response.responseBodyContent,"tx hash"), true)
TxHash = CmdUtils.getTxHash(response.responseBodyContent)
cmd = u.command + TxHash
response = CmdUtils.sendRequest('cmd/CmdWithOneArgs', cmd, "sync")
//CmdUtils.pl(response.responseBodyContent)
WS.verifyEqual(StringUtils.stringContains(response.responseBodyContent,"irishub/service/MsgSvcDef"), true)


/*	//CmdUtils.pl(response.responseBodyContent)
 * utils
 */

class Utils {
	public String command;
	public String v0;	
	public String v0_faa;
	public String v0_fva;
	
	public Utils(){
		TestData faucet = findTestData('base/faucet')
		v0 = faucet.getValue('name', 1)
		v0_faa = CmdUtils.getAddressFromName(v0, "faa")
		v0_fva = CmdUtils.getAddressFromName(v0, "fva")

		command = 'iriscli tendermint tx --trust-node'.concat(GlobalVariable.chainId).concat(GlobalVariable.node).concat(' ')
	}
}
