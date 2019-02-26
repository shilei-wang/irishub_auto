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
import cmd.ServiceUtils as ServiceUtils

import com.google.gson.JsonObject as JsonObject;

Utils u = new Utils();

provider = CmdUtils.getAddressFromName(u.v0, "faa")
command = u.command.concat(' --service-name=').concat(u.serviceName)
command = command.concat(' --provider=').concat(provider)
response = CmdUtils.sendRequest('cmd/CmdWithOneArgs',command, "sync")
//CmdUtils.pl(response.responseBodyContent)

re = CmdUtils.Parse(response.responseBodyContent).getAsJsonObject()
WS.verifyEqual(re.get("def_name").getAsString(), u.serviceName)
WS.verifyEqual(re.get("def_chain_id").getAsString(), GlobalVariable.chainId)
WS.verifyEqual(re.get("bind_chain_id").getAsString(), GlobalVariable.chainId)
WS.verifyEqual(re.get("provider").getAsString(), provider)
WS.verifyEqual(re.get("binding_type").getAsString(), u.td.getValue("binding_type", 1))
deposit_array = re.get("deposit").getAsJsonArray()
WS.verifyEqual(deposit_array.get(0).getAsJsonObject().get("amount").getAsString(), u.td.getValue("deposit", 1))
deposit_array = re.get("price").getAsJsonArray()
WS.verifyEqual(deposit_array.get(0).getAsJsonObject().get("amount").getAsString(), u.td.getValue("price", 1))
WS.verifyEqual(deposit_array.get(1).getAsJsonObject().get("amount").getAsString(), u.td.getValue("price", 1))
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
	public String v0;
	
	public Utils(){		
		td = findTestData('service/bind/binding/IRISHUB-730')
		TestData faucet = findTestData('base/faucet')
		v0 = faucet.getValue('name', 1)		
		command = 'iriscli service binding --def-chain-id='.concat(GlobalVariable.chainId).concat(' --bind-chain-id=').concat(GlobalVariable.chainId).concat(' --node=').concat(GlobalVariable.node)
		
		serviceName = ServiceUtils.defineService(v0)
		ServiceUtils.bindService(v0, serviceName)
	}
}

