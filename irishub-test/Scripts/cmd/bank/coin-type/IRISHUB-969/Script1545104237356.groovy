import static com.kms.katalon.core.checkpoint.CheckpointFactory.findCheckpoint
import static com.kms.katalon.core.testcase.TestCaseFactory.findTestCase
import static com.kms.katalon.core.testdata.TestDataFactory.findTestData
import static com.kms.katalon.core.testobject.ObjectRepository.findTestObject

import java.util.Map

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
import com.google.gson.JsonArray as JsonArray;
import com.google.gson.JsonObject as JsonObject;

Utils u = new Utils();

response = CmdUtils.sendRequest('cmd/CmdWithOneArgs', u.command, "sync")
u.setUnits(response.responseBodyContent)
BigDecimal bigDecimal = new BigDecimal(Long.MAX_VALUE);

// 确认所有coin-type 位数正确且可用 都转账1iris的数额
for (String key : u.units_map.keySet()) {	
	bigDecimal = Math.pow(10,u.units_map.get(key))
	amount = bigDecimal.toString()+key;
	balance_before = CmdUtils.getBalance(u.user_faa, "faa")
	CmdUtils.sendIris(u.v0,u.user_faa,amount)
	balance_after = CmdUtils.getBalance(u.user_faa, "faa")
	WS.verifyEqual((balance_after-balance_before), 1)	
}	

//


/*	//CmdUtils.pl(response.responseBodyContent)
 * utils
 */

class Utils {
	public String command;
	public String v0;
	public String v0_faa;
	public String user;
	public String user_faa;
	public Map<String, Integer> units_map = new HashMap<String, Integer>();
	
	public Utils(){
		TestData faucet = findTestData('base/faucet')
		v0 = faucet.getValue('name', 1)
		v0_faa = CmdUtils.getAddressFromName(v0, "faa")
		user = CmdUtils.createNewAccount(v0, "1iris")
		user_faa = CmdUtils.getAddressFromName(user, "faa")
		
		command = 'iriscli bank coin-type iris --chain-id='.concat(GlobalVariable.chainId).concat(' --node=').concat(GlobalVariable.node)
	}
	
	public setUnits(String msg){
		JsonArray units_array = CmdUtils.Parse(msg).get("units").getAsJsonArray()	
		WS.verifyEqual(units_array.size(), 7)
		for (int i = 0; i < units_array.size() ; i++) {
			JsonObject item = units_array.get(i).getAsJsonObject()
			units_map.put(item.get("denom").getAsString(), Integer.parseInt(item.get("decimal").getAsString()))
		}
	}
}
