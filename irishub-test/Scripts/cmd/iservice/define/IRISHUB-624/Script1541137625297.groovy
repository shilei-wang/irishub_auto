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
import com.kms.katalon.core.testobject.TestObject as TestObject
import com.kms.katalon.core.checkpoint.Checkpoint as Checkpoint
import internal.GlobalVariable as GlobalVariable
import utils.CmdUtils as CmdUtils
import utils.StringUtils as StringUtils


	
Utils u = new Utils();
	
for (int i = 1; i < (u.td.getRowNumbers() + 1); i++) {
	u.Prepare(i)
    if (i == 4) {
		//这里有bug
        continue
    }

    cmd = CmdUtils.generateCmd(u.command, u.td, i)	
	cmd = u.UseRandomServiceName(cmd) //为service name添加随机ID， 避免每次重启新链
	response = CmdUtils.sendRequest('cmd/CmdWithOneArgs', cmd, 5000)
	WS.verifyResponseStatusCode(response, 200)

	//CmdUtils.printLog(response.responseBodyContent)   
}


/*
 * utils
 */

class Utils {
	public TestData td;
	public String command;
	public String password;
	
	public Utils(){
		td = findTestData('iservice/define/IRISHUB-624')
		TestData faucet = findTestData('keys/faucet')
		String name = faucet.getValue('name', 1)
		password = faucet.getValue('password', 1)
		command = 'iriscli iservice define --chain-id='.concat(GlobalVariable.chainId).concat(' --node=').concat(GlobalVariable.node).concat(
				' --from=').concat(name)
	}
	
	public String UseRandomServiceName(String cmd){
		String RandomID =  CmdUtils.generateRandomID()
		return cmd.replace("_service", "_service_"+RandomID)
	}
	
	//JUST FOR　DEBUG
	public Prepare (int i){
		switch(i){
			case 1:
				println "--- TESTCASE 1 --- 创建一个全新的包含全部非必填字段的服务定义, idl内容符合protocolbuf 3标准，广播模式Unicast"
				break;
			case 2:
				println "--- TESTCASE 2 --- 创建一个全新的包含全部非必填字段的服务定义，服务名称、描述、作者描述、标签全部包含多语言和特殊符号，idl内容符合protocolbuf 3标准，广播模式Multicast"
				break;
			case 3:
				println "--- TESTCASE 3 --- 创建一个全新的没有非必填字段的服务定义，idl内容符合protocolbuf 3标准，广播模式Unicast"
				break;
			case 4:
				println "--- TESTCASE 4 --- 创建一个全新的有5个不同非空标签的服务定义，idl内容符合protocolbuf 3标准，广播模式Multicast"
				break;
			case 5:
				println "--- TESTCASE 5 --- 创建一个全新的同时指定字符串和文件位置的idl内容的服务定义，idl内容符合protocolbuf 3标准，广播模式Unicast"
				break;

			}
	}

}

