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
import utils.StringUtils
import com.google.gson.JsonArray
import com.google.gson.JsonObject

defineData = findTestData('service/define/define/IRISHUB-624')
bindData = findTestData('service/bind/bind/IRISHUB-726')

serviceName = defineData.getValue("service-name", 1).concat("_").concat(CmdUtils.generateRandomID())

bindingsData = findTestData('service/bind/bindings/IRISHUB-732')
TestData faucet = findTestData('base/faucet')
v0 = faucet.getValue('name', 1)
v1 = faucet.getValue('name', 2)
password = faucet.getValue('password', 1)

ServiceUtils.serviceDefineCorrectEg(v0, password, serviceName, defineData)
ServiceUtils.serviceBindCorrectEg(v0, password, serviceName, bindData)
ServiceUtils.serviceBindCorrectEg(v1, password, serviceName, bindData)

providers = new String[2];
providers[0] = GetAddressByKey.getAddressByKey(v0)
providers[1] = GetAddressByKey.getAddressByKey(v1)

response = ServiceUtils.getServiceBindings(serviceName, GlobalVariable.chainId)
binding_array = CmdUtils.ParseArray(response.responseBodyContent)

for (int i = 0; i < binding_array.size() ; i++) {
	binding_item = binding_array.get(i).getAsJsonObject()
	WS.verifyEqual(binding_item.get("def_name").getAsString(), serviceName)
	WS.verifyEqual(binding_item.get("def_chain_id").getAsString(), GlobalVariable.chainId)
	WS.verifyEqual(binding_item.get("bind_chain_id").getAsString(), GlobalVariable.chainId)
	WS.verifyEqual(StringUtils.arrayContains(providers, binding_item.get("provider").getAsString()), true)
	WS.verifyEqual(binding_item.get("binding_type").getAsString(), bindingsData.getValue("binding_type", 1))
	deposit_array = binding_item.get("deposit").getAsJsonArray()
	WS.verifyEqual(deposit_array.get(0).getAsJsonObject().get("amount").getAsString(), bindingsData.getValue("deposit", 1))
	deposit_array = binding_item.get("price").getAsJsonArray()
	WS.verifyEqual(deposit_array.get(0).getAsJsonObject().get("amount").getAsString(), bindingsData.getValue("price", 1))
	WS.verifyEqual(deposit_array.get(1).getAsJsonObject().get("amount").getAsString(), bindingsData.getValue("price", 1))
	level = binding_item.get("level").getAsJsonObject()
	WS.verifyEqual(level.get("avg_rsp_time").getAsString(), bindingsData.getValue("avg_rsp_time", 1))
	WS.verifyEqual(level.get("usable_time").getAsString(), bindingsData.getValue("usable_time", 1))
	WS.verifyEqual(binding_item.get("available").asBoolean(), true)
}

