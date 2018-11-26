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
import com.kms.katalon.core.webservice.keyword.WSBuiltInKeywords as WS
import com.kms.katalon.core.webui.keyword.WebUiBuiltInKeywords as WebUI
import internal.GlobalVariable as GlobalVariable
import utils.CmdUtils
import utils.StringUtils as StringUtils
import com.kms.katalon.core.testobject.ResponseObject as ResponseObject

Utils u = new Utils();
String[] cmdArray=new String[6];

//1）指定--only-from-validator为一个不存在委托关系的fva账户，执行withdraw-rewards。（不指定--is-validator）
cmdArray[0] = " --only-from-validator="+u.td.getValue('data', 1)

//2）指定--only-from-validator为一个faa账户，执行withdraw-rewards。（不指定--is-validator） 
cmdArray[1] = " --only-from-validator="+u.td.getValue('data', 2)

//3） 指定一个非validator账户且--is-validator设置为true，执行withdraw-rewards。  （不指定--only-from-validator）
user = CmdUtils.createNewAccount(u.name,"1iris")
cmdArray[2] = " --is-validator="+u.td.getValue('data', 3)+" --from="+user

//4）指定--is-validator为空，执行withdraw-rewards。  （不指定--only-from-validator） 
cmdArray[3] = " --is-validator="+u.td.getValue('data', 4)

//5）指定--is-validator为test，执行withdraw-rewards。  （不指定--only-from-validator）
cmdArray[4] = " --is-validator="+u.td.getValue('data', 5)

//6）同时指定--is-validator指定为true和--only-from-validator 
cmdArray[5] = " --is-validator=true --only-from-validator="+u.td.getValue('data', 6)

for (int i = 0; i < cmdArray.length ; i++) {
	u.Prepare(i)
	cmd = u.command+cmdArray[i]
	response = CmdUtils.sendRequest('cmd/CmdWithOneArgs', cmd, 0)
	WS.verifyEqual(StringUtils.stringContains(response.responseBodyContent,u.td.getValue('cmd_result', i+1)), true)	
	//CmdUtils.printLog(response.responseBodyContent)
}


class Utils {
	public TestData td;
	public String command;
	public String name;
	
	public Utils(){
		td = findTestData('distribution/withdraw-rewards/IRISHUB-811')
		name =  findTestData('base/faucet').getValue('name', 1)


		command = 'iriscli distribution withdraw-rewards --chain-id='.concat(GlobalVariable.chainId).concat(' --node=').concat(GlobalVariable.node).concat(
			' --from=').concat(name)
		command = CmdUtils.addTxFee(command, findTestData('base/tx'), 1)			
	}
	

		//JUST FOR　DEBUG
	public Prepare (int i){
		switch(i){
			case 1:
				println "--- TESTCASE 1 --- "
				break;
			case 2:
				println "--- TESTCASE 2 --- "
				break;
			case 3:
				println "--- TESTCASE 3 --- "
				break;
			case 4:
				println "--- TESTCASE 4 --- "
				break;
			}
	}
}
