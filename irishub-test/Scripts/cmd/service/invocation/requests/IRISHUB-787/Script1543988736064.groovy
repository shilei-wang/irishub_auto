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

//在有调用请求的情况下，服务提供者查询自己绑定的某一服务的请求列表.
ServiceUtils.call(u.v0, u.v0, u.serviceName)
response = CmdUtils.sendRequest('cmd/CmdWithOneArgs', u.command, "sync")	
binding_array = CmdUtils.ParseArray(response.responseBodyContent)	

binding_item = binding_array.get(0).getAsJsonObject()
WS.verifyEqual(binding_item.get("def_name").getAsString(), u.serviceName)
WS.verifyEqual(binding_item.get("def_chain_id").getAsString(), GlobalVariable.chainId)
WS.verifyEqual(binding_item.get("bind_chain_id").getAsString(), GlobalVariable.chainId)
WS.verifyEqual(binding_item.get("req_chain_id").getAsString(), GlobalVariable.chainId)
WS.verifyEqual(binding_item.get("method_id").getAsString(), u.td.getValue("method_id", 1))
WS.verifyEqual(binding_item.get("provider").getAsString(), u.provider)
WS.verifyEqual(binding_item.get("consumer").getAsString(), u.provider)
WS.verifyEqual(binding_item.get("input").getAsString(), u.td.getValue("input", 1))
deposit_array = binding_item.get("service_fee").getAsJsonArray()
WS.verifyEqual(deposit_array.get(0).getAsJsonObject().get("amount").getAsString(), u.td.getValue("service_fee", 1))


//在最大请求时间已经过期的情况下，查询该请求是否会从列表里删除, 目前设置5个块
CmdUtils.waitUntilSeveralBlock(5)
response = CmdUtils.sendRequest('cmd/CmdWithOneArgs', u.command, "sync")
WS.verifyEqual(StringUtils.stringContains(response.responseBodyContent, "null"), true)


/* //CmdUtils.pl(u.command)
 * utils
 */

class Utils {
	public TestData td;
	public String command;
	public String serviceName;
	public String v0;
	public String provider;
	
	public Utils(){
		td = findTestData('service/invocation/requests/IRISHUB-787')
		TestData faucet = findTestData('base/faucet')
		v0 = faucet.getValue('name', 1)
		provider = CmdUtils.getAddressFromName(v0, "faa")

		serviceName = ServiceUtils.defineService(v0)
		ServiceUtils.bindService(v0, serviceName)		
		command = 'iriscli service requests '.concat(' --bind-chain-id=').concat(GlobalVariable.chainId).concat(' --def-chain-id=').concat(GlobalVariable.chainId).concat(' --node=').
				concat(GlobalVariable.node)+" --service-name="+serviceName+" --provider="+provider
	}
}
