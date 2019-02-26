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
import rest.ServiceUtils
import rest.BaseTx
import rest.AccountUtils
import rest.GetAddressByKey
import utils.CmdUtils as CmdUtils

import com.google.gson.JsonArray
import com.google.gson.JsonObject


defineData = findTestData('service/define/define/IRISHUB-624')
bindData = findTestData('service/bind/bind/IRISHUB-726')
bindingData = findTestData('service/bind/binding/IRISHUB-730')

faucet = findTestData('base/faucet')
String name = faucet.getValue('name', 1)
String password = faucet.getValue('password', 1)
JsonArray prices

serviceName = defineData.getValue("service-name", 1).concat("_").concat(CmdUtils.generateRandomID())
ServiceUtils.serviceDefineCorrectEg(name, password, serviceName, defineData)
ServiceUtils.serviceBindCorrectEg(name, password, serviceName, bindData)

provider = GetAddressByKey.getAddressByKey(name)

response = ServiceUtils.getServiceBinding(serviceName, GlobalVariable.chainId, GlobalVariable.chainId, provider)

println response.responseBodyContent

re = CmdUtils.Parse(response.responseBodyContent).getAsJsonObject()
WS.verifyEqual(re.get("def_name").getAsString(), serviceName)
WS.verifyEqual(re.get("def_chain_id").getAsString(), GlobalVariable.chainId)
WS.verifyEqual(re.get("bind_chain_id").getAsString(), GlobalVariable.chainId)
WS.verifyEqual(re.get("provider").getAsString(), provider)
WS.verifyEqual(re.get("binding_type").getAsString(), bindingData.getValue("binding_type", 1))
deposit_array = re.get("deposit").getAsJsonArray()
WS.verifyEqual(deposit_array.get(0).getAsJsonObject().get("amount").getAsString(), bindingData.getValue("deposit", 1))
deposit_array = re.get("price").getAsJsonArray()
WS.verifyEqual(deposit_array.get(0).getAsJsonObject().get("amount").getAsString(), bindingData.getValue("price", 1))
WS.verifyEqual(deposit_array.get(1).getAsJsonObject().get("amount").getAsString(), bindingData.getValue("price", 1))
level = re.get("level").getAsJsonObject()
WS.verifyEqual(level.get("avg_rsp_time").getAsString(), bindingData.getValue("avg_rsp_time", 1))
WS.verifyEqual(level.get("usable_time").getAsString(), bindingData.getValue("usable_time", 1))
WS.verifyEqual(re.get("available").asBoolean(), true)


