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
import com.kms.katalon.core.webservice.keyword.WSBuiltInKeywords as WS
import com.kms.katalon.core.webui.keyword.WebUiBuiltInKeywords as WebUI
import internal.GlobalVariable as GlobalVariable

import utils.CmdUtils
import utils.StringUtils as StringUtils
import cmd.KeysUtils as KeysUtils

data = findTestData('keys/update/IRISHUB-949')
Utils u = new Utils()

//case 1 更新该本地账户密码 使用新密码删除该本地账户
i = 1
String cmd = u.command + u.user_map.get("name")
response = CmdUtils.sendRequest('cmd/CmdWithTwoArgs', cmd,  u.password,  data.getValue("password",i), "sync")

println response.responseBodyContent
WS.verifyEqual(StringUtils.stringContains(response.responseBodyContent, "Password successfully updated"), true)
KeysUtils.delete(u.user_map.get("name"), data.getValue("password",i))

//case 2 创建一个新的本地账户 使用创建时相同的密码来更新这个本地账户，检查是否能成功更新
i = 2
user_map2 = KeysUtils.add(data.getValue("password",i))
cmd = u.command + user_map2.get("name")
response = CmdUtils.sendRequest('cmd/CmdWithTwoArgs', cmd,  data.getValue("password",i),  data.getValue("password",i), "sync")
WS.verifyEqual(StringUtils.stringContains(response.responseBodyContent, "Password successfully updated"), true)
println response.responseBodyContent

//case 3 更新测试内容2的本地账户密码 再次更新本地账户密码
i = 3
response = CmdUtils.sendRequest('cmd/CmdWithTwoArgs', cmd,  data.getValue("password",2),  data.getValue("password",i), "sync")
WS.verifyEqual(StringUtils.stringContains(response.responseBodyContent, "Password successfully updated"), true)
response = CmdUtils.sendRequest('cmd/CmdWithTwoArgs', cmd,  data.getValue("password",i),  data.getValue("password",i+1), "sync")
WS.verifyEqual(StringUtils.stringContains(response.responseBodyContent, "Password successfully updated"), true)


class Utils {
	public String command
	public String password
	public Map<String, String> user_map
	public String faa_before
	public String faa_after
	
	public Utils(){
		password = findTestData('base/faucet').getValue('password', 1)
		user_map = KeysUtils.add(password)
		faa_before = CmdUtils.getAddressFromName(user_map.get("name"), "faa")

		command = 'iriscli keys update '
	}
}
