package utils

import static com.kms.katalon.core.checkpoint.CheckpointFactory.findCheckpoint
import static com.kms.katalon.core.testcase.TestCaseFactory.findTestCase
import static com.kms.katalon.core.testdata.TestDataFactory.findTestData
import static com.kms.katalon.core.testobject.ObjectRepository.findTestObject

import com.google.gson.JsonObject
import com.kms.katalon.core.annotation.Keyword
import com.kms.katalon.core.checkpoint.Checkpoint
import com.kms.katalon.core.cucumber.keyword.CucumberBuiltinKeywords as CucumberKW
import com.kms.katalon.core.mobile.keyword.MobileBuiltInKeywords as Mobile
import com.kms.katalon.core.model.FailureHandling
import com.kms.katalon.core.testcase.TestCase
import com.kms.katalon.core.testdata.TestData
import com.kms.katalon.core.testobject.TestObject
import com.kms.katalon.core.testobject.ResponseObject as ResponseObject
import com.kms.katalon.core.webservice.keyword.WSBuiltInKeywords as WS
import com.kms.katalon.core.webui.keyword.WebUiBuiltInKeywords as WebUI

import internal.GlobalVariable

import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;
import com.google.gson.JsonIOException;
import com.google.gson.JsonObject;


public class CmdUtils {
	@Keyword
	public static String generateCmd(String cmd, TestData data, int dindex) {
		data.getColumnNames().each{columnName ->
			if (columnName.equals("summary")||columnName.equals("result")){
				return
			}

			if (!StringUtils.isNullOrEmpty(data.getValue(columnName, dindex))) {
				cmd = cmd.concat(' --').concat(columnName).concat('=').concat(data.getValue(columnName, dindex))
			}
		}
		return cmd
	}

	@Keyword
	public static ResponseObject sendRequest(String obj, String cmd, int delay) {
		String password = findTestData('keys/faucet').getValue('password', 1)
		ResponseObject response = WS.sendRequest(findTestObject(obj, [('command') : cmd, ('args1') : password, ('commanderIP'):GlobalVariable.commanderIP]));
		sleep(delay)
		return response
	}

	private static String beginLog = '\n*****************************\n'
	private static String endLog = '\n*****************************\n'

	@Keyword
	public static printLog(String msg) {
		println beginLog+msg+endLog
	}

	@Keyword
	public static String generateRandomID() {
		String sources = "0123456789"
		Random rand = new Random()
		StringBuffer flag = new StringBuffer()
		for (int j = 0; j < 6; j++) {
			flag.append(sources.charAt(rand.nextInt(9)))
		}
		return flag.toString();
	}

	@Keyword
	public static JsonObject Parse(String msg){
		JsonParser parse =new JsonParser();  //创建json解析器
		try {
			JsonObject json=(JsonObject) parse.parse(msg);  //创建jsonObject对象

			//System.out.println("resultcode:"+json.get("resultcode").getAsInt());  //将json数据转为为int型的数据
			//JsonObject result=json.get("result").getAsJsonObject();
			//System.out.println("temperature:"+today.get("temperature").getAsString());
			//CmdUtils.printLog(json.get("id").getAsString())

			return json
		} catch (JsonIOException e) {
			e.printStackTrace();
		} catch (JsonSyntaxException e) {
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}
}
