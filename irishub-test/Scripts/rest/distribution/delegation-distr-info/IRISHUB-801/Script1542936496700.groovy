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
import rest.GetAddressByKey
import rest.GetValJson
import groovy.json.JsonSlurper

TestData faucet = findTestData('base/faucet')
String name = faucet.getValue('name', 1)
String delegatorAddr = GetAddressByKey.getAddressByKey(name)
String validatorAddr = GetValJson.getFirstValAddress()

TestData data = findTestData('distribution/delegation-distr-info/IRISHUB-801')

response = WS.sendRequest(findTestObject('rest/distribution/ICS24_get_distribution_delegatorAddr_distrInfo_validatorAddr', [ ('delegatorAddr') : delegatorAddr, ('validatorAddr') : validatorAddr, ('lcdIP') : GlobalVariable.lcdIP]))
System.out.println(response.responseBodyContent)
WS.verifyResponseStatusCode(response, 200)

String result = data.getValue("cmd_result",1)
JsonSlurper slurper = new JsonSlurper()
Map parsedJson = slurper.parseText(response.responseBodyContent)
WS.verifyEqual(parsedJson.containsKey(result), true)
