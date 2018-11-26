package init

import static com.kms.katalon.core.checkpoint.CheckpointFactory.findCheckpoint
import static com.kms.katalon.core.testcase.TestCaseFactory.findTestCase
import static com.kms.katalon.core.testdata.TestDataFactory.findTestData
import static com.kms.katalon.core.testobject.ObjectRepository.findTestObject

import com.kms.katalon.core.annotation.Keyword
import com.kms.katalon.core.checkpoint.Checkpoint
import com.kms.katalon.core.cucumber.keyword.CucumberBuiltinKeywords as CucumberKW
import com.kms.katalon.core.mobile.keyword.MobileBuiltInKeywords as Mobile
import com.kms.katalon.core.model.FailureHandling
import com.kms.katalon.core.testcase.TestCase
import com.kms.katalon.core.testdata.TestData
import com.kms.katalon.core.testobject.ResponseObject
import com.kms.katalon.core.testobject.TestObject
import com.kms.katalon.core.webservice.keyword.WSBuiltInKeywords as WS
import com.kms.katalon.core.webui.keyword.WebUiBuiltInKeywords as WebUI

import internal.GlobalVariable

public class Faucet {
	@Keyword
	public static void initCmd() {
		//		TestData faucet = findTestData('base/faucet')
		//		ResponseObject response = WS.sendRequest(findTestObject('cmd/CmdWithNoArgs', [('command') : 'iriscli keys show ' + faucet.getValue('name', 1)]))
		//		boolean exists = (response.getStatusCode() == 200)
		//		boolean match = response.getResponseText().contains(faucet.getValue('address', 1))
		//		if (!exists) {
		//			WS.sendRequest(findTestObject('cmd/CmdWithTwoArgs', [('command') : 'iriscli keys add '.concat(faucet.getValue('name', 1)).concat(' --recover'), ('args1'): faucet.getValue('password', 1), ('args2'): faucet.getValue('seed', 1)]))
		//		} else if (!match){
		//			WS.sendRequest(findTestObject('cmd/CmdWithThreeArgs', [('command') : 'iriscli keys add '.concat(faucet.getValue('name', 1)).concat(' --recover'), ('args1'): 'y', ('args2'): faucet.getValue('password', 1), ('args3'): faucet.getValue('seed', 1)]))
		//		}
	}

	@Keyword
	public static void initRest() {
		//		TestData faucet = findTestData('base/faucet')
		//		ResponseObject response = WS.sendRequest(findTestObject('rest/keys/query/get key by name', [('name') : faucet.getValue('name', 1)]))
		//		boolean exists = (response.getStatusCode() == 200)
		//		boolean match = response.getResponseText().contains(faucet.getValue('address', 1))
		//		if (!exists) {
		//			WS.sendRequest(findTestObject('rest/keys/add/recover by seed', [('name') : faucet.getValue('name', 1), ('password'): faucet.getValue('name', 1), ('seed'): faucet.getValue('seed', 1)]))
		//		} else if (!match){
		//			// TODO there is no rest api for rewriting an existing key
		//		}
	}
}
