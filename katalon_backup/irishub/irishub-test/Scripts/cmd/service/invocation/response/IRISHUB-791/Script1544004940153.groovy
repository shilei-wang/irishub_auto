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

//根据调用请求返回的request id，在没有任何响应的情况下，服务消费者查询服务请求的响应结果。
response = CmdUtils.sendRequest('cmd/CmdWithOneArgs', u.command, "sync")
WS.verifyEqual(StringUtils.stringContains(response.responseBodyContent, "response is not existed"), true)

//根据调用请求返回的request id，在有响应的情况下，服务消费者查询服务请求的响应结果。
ServiceUtils.respond(u.v0, u.request_id)
response = CmdUtils.sendRequest('cmd/CmdWithOneArgs', u.command, "sync")
re = CmdUtils.Parse(response.responseBodyContent).get("value").getAsJsonObject()
//WS.verifyEqual(re.get("req_chain_id").getAsString(), GlobalVariable.chainId)
WS.verifyEqual(re.get("provider").getAsString(), u.provider)
WS.verifyEqual(re.get("consumer").getAsString(), u.provider)
WS.verifyEqual(re.get("output").getAsString(), u.td.getValue("output", 1) )


/* //CmdUtils.pl(u.command)
 * utils
 */

class Utils {
	public TestData td;
	public String command;
	public String serviceName;
	public String v0;
	public String request_id;
	public String provider;
	
	public Utils(){
		td = findTestData('service/invocation/response/IRISHUB-791')
		TestData faucet = findTestData('base/faucet')
		v0 = faucet.getValue('name', 1)
		provider = CmdUtils.getAddressFromName(v0, "faa")

		serviceName = ServiceUtils.defineService(v0)
		ServiceUtils.bindService(v0, serviceName)	
		request_id = ServiceUtils.call(v0, v0, serviceName)
		
		command = 'iriscli service response'.concat(GlobalVariable.reqchainId).concat(GlobalVariable.node)+" --request-id="+request_id
	}
}
