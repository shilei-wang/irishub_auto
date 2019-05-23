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
import utils.CmdUtils as CmdUtils
import utils.StringUtils as StringUtils
import cmd.ServiceUtils as ServiceUtils
import com.google.gson.JsonObject as JsonObject;
	
Utils u = new Utils();

// returned fee ， income fee都没有的情况下， 查询fees值
ServiceUtils.cleanFees(u.v0)

actual   = Double.valueOf(ServiceUtils.fees(u.v0, "returned_fee").replace("iris", ""))
expected = Double.valueOf(u.td.getValue("returned_fee", 1).replace("iris", ""))
WS.verifyEqual(CmdUtils.compareIgnoreFee(actual, expected), true)
actual   = Double.valueOf(ServiceUtils.fees(u.v0, "incoming_fee").replace("iris", ""))
expected = Double.valueOf(u.td.getValue("incoming_fee", 1).replace("iris", ""))
WS.verifyEqual(CmdUtils.compareIgnoreFee(actual, expected), true)


// 只有 returned fee ，没有 income fee的情况下， 查询fees值
ServiceUtils.cleanFees(u.v0)
request_id = ServiceUtils.call(u.v0, u.v0, u.serviceName)
if (!CmdUtils.isMainnet()) {
	CmdUtils.waitUntilSeveralBlock(5)
} else {
	CmdUtils.waitUntilSeveralBlock(21)
}

actual   = Double.valueOf(ServiceUtils.fees(u.v0, "returned_fee").replace("iris", ""))
expected = Double.valueOf(u.td.getValue("returned_fee", 2).replace("iris", ""))

// 调试 ：这里总是出问题
CmdUtils.pl("compareIgnoreFee")
CmdUtils.pl(actual)
CmdUtils.pl(expected)
//

WS.verifyEqual(CmdUtils.compareIgnoreFee(actual, expected), true)
actual   = Double.valueOf(ServiceUtils.fees(u.v0, "incoming_fee").replace("iris", ""))
expected = Double.valueOf(u.td.getValue("incoming_fee", 2).replace("iris", ""))
WS.verifyEqual(CmdUtils.compareIgnoreFee(actual, expected), true)

// 只有 income fee ，没有returned fee的情况下， 查询fees值
ServiceUtils.cleanFees(u.v0)
request_id = ServiceUtils.call(u.v0, u.v0, u.serviceName)
ServiceUtils.respond(u.v0, request_id)

actual   = Double.valueOf(ServiceUtils.fees(u.v0, "returned_fee").replace("iris", ""))
expected = Double.valueOf(u.td.getValue("returned_fee", 3).replace("iris", ""))
WS.verifyEqual(CmdUtils.compareIgnoreFee(actual, expected), true)
actual   = Double.valueOf(ServiceUtils.fees(u.v0, "incoming_fee").replace("iris", ""))
expected = Double.valueOf(CmdUtils.tax(u.td.getValue("incoming_fee", 3), 0.02).replace("iris", ""))
WS.verifyEqual(CmdUtils.compareIgnoreFee(actual, expected), true)

// returned fee和 income fee 都有的情况下， 查询fees值
ServiceUtils.cleanFees(u.v0)
ServiceUtils.call(u.v0, u.v0, u.serviceName)
if (!CmdUtils.isMainnet()) {
	CmdUtils.waitUntilSeveralBlock(5)
} else {
	CmdUtils.waitUntilSeveralBlock(21)
}
request_id = ServiceUtils.call(u.v0, u.v0, u.serviceName)
ServiceUtils.respond(u.v0, request_id)

actual   = Double.valueOf(ServiceUtils.fees(u.v0, "returned_fee").replace("iris", ""))
expected = Double.valueOf(u.td.getValue("returned_fee", 4).replace("iris", ""))
WS.verifyEqual(CmdUtils.compareIgnoreFee(actual, expected), true)
actual   = Double.valueOf(ServiceUtils.fees(u.v0, "incoming_fee").replace("iris", ""))
expected = Double.valueOf(CmdUtils.tax(u.td.getValue("incoming_fee", 4), 0.02).replace("iris", ""))
WS.verifyEqual(CmdUtils.compareIgnoreFee(actual, expected), true)


/* //CmdUtils.pl(u.command)
 * utils
 */

class Utils {
	public TestData td;
	public String serviceName;
	public String v0;

	
	public Utils(){
		td = findTestData('service/invocation/fees/IRISHUB-833')
		TestData faucet = findTestData('base/faucet')
		v0 = faucet.getValue('name', 1)

		serviceName = ServiceUtils.defineService(v0)
		ServiceUtils.bindService(v0, serviceName)	
	}
}
