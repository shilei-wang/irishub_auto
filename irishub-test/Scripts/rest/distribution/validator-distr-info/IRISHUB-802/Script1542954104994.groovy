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
import rest.StakeUtils
import rest.GetAddressByKey
import groovy.json.JsonSlurper
import utils.StringUtils as StringUtils

TestData faucet = findTestData('base/faucet')
String name = faucet.getValue('name', 1)
String delegatorAddr = GetAddressByKey.getAddressByKey(name)
Map map = StakeUtils.getValidatorList()
String validatorAddr = map.get(name)

TestData data = findTestData('distribution/validator-distr-info/IRISHUB-802')

response = WS.sendRequest(findTestObject('rest/distribution/ICS24_get_distribution_validatorAddr_valDistrInfo', [ ('validatorAddr') : validatorAddr, ('lcdIP') : GlobalVariable.lcdIP]))
System.out.println(response.responseBodyContent)
WS.verifyResponseStatusCode(response, 200)

String result = data.getValue("cmd_result",1)
//JsonSlurper slurper = new JsonSlurper()
//def parsedJson = slurper.parseText(response.responseBodyContent)
WS.verifyEqual(StringUtils.stringContains(response.responseBodyContent, result), true)

