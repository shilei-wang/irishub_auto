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
import rest.AccountUtils

password = findTestData('base/faucet').getValue('password', 1)
String userName1 = "user_"+ CmdUtils.generateRandomID()
Map user1 = AccountUtils.addNewKey(userName1, password)
data = findTestData('keys/update/IRISHUB-949')

//case 1 更新该本地账户密码 使用新密码删除该本地账户
i = 1
response = AccountUtils.updateKey(userName1, data.getValue("password",i), password)
WS.verifyEqual(response.getStatusCode(), 200)
response = AccountUtils.deleteKey(userName1, data.getValue("password",i))
WS.verifyEqual(response.getStatusCode(), 200)


//case 2 创建一个新的本地账户 使用创建时相同的密码来更新这个本地账户，检查是否能成功更新
i = 2
String userName2 = "user_"+ CmdUtils.generateRandomID()
Map user2 = AccountUtils.addNewKey(userName2, data.getValue("password",i))
response = AccountUtils.updateKey(userName2, data.getValue("password",i), data.getValue("password",i))
WS.verifyEqual(response.getStatusCode(), 200)

//case 3 更新测试内容2的本地账户密码 再次更新本地账户密码
i = 3
response = AccountUtils.updateKey(userName2, data.getValue("password",i), data.getValue("password",2))
WS.verifyEqual(response.getStatusCode(), 200)
response = AccountUtils.updateKey(userName2, data.getValue("password",i+1), data.getValue("password",i))
WS.verifyEqual(response.getStatusCode(), 200)
