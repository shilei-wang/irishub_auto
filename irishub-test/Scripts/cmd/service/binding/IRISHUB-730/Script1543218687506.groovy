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
import utils.CmdUtils as CmdUtils
import utils.StringUtils as StringUtils

import com.google.gson.JsonObject as JsonObject;

Utils u = new Utils();

provider = CmdUtils.getAddressFromName(u.name, "faa")
command = u.command.concat(' --service-name=').concat(u.serviceName)
command = command.concat(' --provider=').concat(provider)
response = CmdUtils.sendRequest('cmd/CmdWithOneArgs',command, "sync")

re = CmdUtils.Parse(response.responseBodyContent).get("value").getAsJsonObject()
WS.verifyEqual(re.get("def_name").getAsString(), u.serviceName)
WS.verifyEqual(re.get("def_chain_id").getAsString(), GlobalVariable.chainId)
WS.verifyEqual(re.get("bind_chain_id").getAsString(), GlobalVariable.chainId)
WS.verifyEqual(re.get("provider").getAsString(), provider)
WS.verifyEqual(re.get("binding_type").getAsString(), u.td.getValue("binding_type", 1))
deposit_array = re.get("deposit").getAsJsonArray()
WS.verifyEqual(deposit_array.get(0).getAsJsonObject().get("amount").getAsString(), u.td.getValue("deposit", 1))
deposit_array = re.get("price").getAsJsonArray()
WS.verifyEqual(deposit_array.get(0).getAsJsonObject().get("amount").getAsString(), u.td.getValue("price", 1))
level = re.get("level").getAsJsonObject()
WS.verifyEqual(level.get("avg_rsp_time").getAsString(), u.td.getValue("avg_rsp_time", 1))
WS.verifyEqual(level.get("usable_time").getAsString(), u.td.getValue("usable_time", 1))
WS.verifyEqual(re.get("available").asBoolean(), true)


/*
 * utils
 */

class Utils {
	public TestData td;
	public String command;
	public String serviceName;
	public String name;
	
	public Utils(){		
		td = findTestData('service/binding/IRISHUB-730')
		TestData faucet = findTestData('base/faucet')
		name = faucet.getValue('name', 1)
		
		command = 'iriscli service binding --def-chain-id='.concat(GlobalVariable.chainId).concat(' --bind-chain-id=').concat(GlobalVariable.chainId).concat(' --node=').concat(GlobalVariable.node)

		ResponseObject response = defineService()
		WS.verifyEqual(StringUtils.stringContains(response.responseBodyContent,"tx hash"), true)
		response = bindService()
		WS.verifyEqual(StringUtils.stringContains(response.responseBodyContent,"tx hash"), true)
	}
	
	public ResponseObject defineService(){
		String define_command = 'iriscli service define --chain-id='.concat(GlobalVariable.chainId).concat(' --node=').concat(GlobalVariable.node).concat(
				' --from=').concat(name)
		define_command = CmdUtils.addTxFee(define_command, findTestData('base/tx'), 1)
				
		define_command = CmdUtils.generateCmd(define_command, findTestData('service/define/IRISHUB-624'), 1)
		define_command = UseRandomServiceName(define_command)
		ResponseObject response = CmdUtils.sendRequest('cmd/CmdWithOneArgs', define_command, "wait")
		
		return response
		//CmdUtils.printLog(response.responseBodyContent)
	}
	
	public bindService(){
		String bind_command = 'iriscli service bind --chain-id='.concat(GlobalVariable.chainId).concat(' --def-chain-id=').concat(GlobalVariable.chainId).concat(' --node=').concat(GlobalVariable.node).concat(
				' --from=').concat(name)
		bind_command = CmdUtils.addTxFee(bind_command, findTestData('base/tx'), 1)		
		
		bind_command = bind_command.concat(' --service-name=').concat(serviceName)
		bind_command = CmdUtils.generateCmd(bind_command, findTestData('service/bind/IRISHUB-726'), 1)
		ResponseObject response = CmdUtils.sendRequest('cmd/CmdWithOneArgs',bind_command, "wait")
		
		return response
		//CmdUtils.printLog(response.responseBodyContent)
	}
	
	public String UseRandomServiceName(String cmd){
		String RandomID =  CmdUtils.generateRandomID()
		serviceName =  "001_service_"+RandomID
		return cmd.replace("001_service", serviceName)
	}
}

