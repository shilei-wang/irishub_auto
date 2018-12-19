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


//正向测试 校验返回值
command = u.command.concat(' --def-chain-id=').concat(GlobalVariable.chainId).concat(' --service-name=').concat(u.serviceName)
response = CmdUtils.sendRequest('cmd/CmdWithOneArgs', command, "sync")

CmdUtils.pl(response.responseBodyContent)

re = CmdUtils.Parse(response.responseBodyContent).get("definition").getAsJsonObject()
WS.verifyEqual(re.get("name").getAsString(), u.serviceName)
WS.verifyEqual(re.get("chain_id").getAsString(), GlobalVariable.chainId)
WS.verifyEqual(re.get("description").getAsString(), u.td.getValue("service-description", 1))
WS.verifyEqual(re.get("author_description").getAsString(), u.td.getValue("author-description", 1))

tags_array = re.get("tags").getAsJsonArray()
String[] tdStrArray = u.td.getValue("tags", 1).split(",");
for(int i=0;i<tags_array.size();i++){
	subObject = tags_array.get(i).getAsString();
	WS.verifyEqual(subObject, tdStrArray[i])
}

/* CmdUtils.pl(response.responseBodyContent)
 * utils
 */

class Utils {
	public TestData td;
	public String command;
	public String serviceName;
	public String v0;
	
	public Utils(){
		td = findTestData('service/define/definition/IRISHUB-728')
		TestData faucet = findTestData('base/faucet')
		v0 = faucet.getValue('name', 1)
		
		serviceName = ServiceUtils.defineService(v0)		
		command = 'iriscli service definition'.concat(' --node=').concat(GlobalVariable.node)
	}
}

