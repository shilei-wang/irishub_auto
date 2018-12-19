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

command = u.command.concat(' --service-name=').concat(u.serviceName)
response = CmdUtils.sendRequest('cmd/CmdWithOneArgs',command, "sync")
binding_array = CmdUtils.ParseArray(response.responseBodyContent)

for (int i = 0; i < binding_array.size() ; i++) {
	binding_item = binding_array.get(i).getAsJsonObject()
	WS.verifyEqual(binding_item.get("def_name").getAsString(), u.serviceName)
	WS.verifyEqual(binding_item.get("def_chain_id").getAsString(), GlobalVariable.chainId)
	WS.verifyEqual(binding_item.get("bind_chain_id").getAsString(), GlobalVariable.chainId)
	WS.verifyEqual(StringUtils.arrayContains(u.providers, binding_item.get("provider").getAsString()), true)
	WS.verifyEqual(binding_item.get("binding_type").getAsString(), u.td.getValue("binding_type", 1))
	deposit_array = binding_item.get("deposit").getAsJsonArray()
	WS.verifyEqual(deposit_array.get(0).getAsJsonObject().get("amount").getAsString(), u.td.getValue("deposit", 1))
	deposit_array = binding_item.get("price").getAsJsonArray()
	WS.verifyEqual(deposit_array.get(0).getAsJsonObject().get("amount").getAsString(), u.td.getValue("price", 1))
	WS.verifyEqual(deposit_array.get(1).getAsJsonObject().get("amount").getAsString(), u.td.getValue("price", 1))
	level = binding_item.get("level").getAsJsonObject()
	WS.verifyEqual(level.get("avg_rsp_time").getAsString(), u.td.getValue("avg_rsp_time", 1))
	WS.verifyEqual(level.get("usable_time").getAsString(), u.td.getValue("usable_time", 1))
	WS.verifyEqual(binding_item.get("available").asBoolean(), true)
}

//CmdUtils.pl(command)


/*
 * utils
 */

class Utils {
	public TestData td;
	public String command;
	public String serviceName;
	public String v0;
	public String v1;
	public String[] providers;
	
	public Utils(){
		td = findTestData('service/bind/bindings/IRISHUB-732')
		TestData faucet = findTestData('base/faucet')
		v0 = faucet.getValue('name', 1)
		v1 = faucet.getValue('name', 2)
		command = 'iriscli service bindings --def-chain-id='.concat(GlobalVariable.chainId).concat(' --node=').concat(GlobalVariable.node)

		serviceName = ServiceUtils.defineService(v0)		
		ServiceUtils.bindService(v0, serviceName)
		ServiceUtils.bindService(v1, serviceName)

		providers = new String[2];
		providers[0] = CmdUtils.getAddressFromName(v0, "faa")
		providers[1] = CmdUtils.getAddressFromName(v1, "faa")
	}
}

