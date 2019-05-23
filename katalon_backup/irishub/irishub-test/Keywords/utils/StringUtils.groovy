package utils

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
import com.kms.katalon.core.testobject.TestObject
import com.kms.katalon.core.webservice.keyword.WSBuiltInKeywords as WS
import com.kms.katalon.core.webui.keyword.WebUiBuiltInKeywords as WebUI

import internal.GlobalVariable

import java.util.Map
import java.util.regex.Matcher
import java.util.regex.Pattern


public class StringUtils {
	@Keyword
	public static boolean isNullOrEmpty(String msg) {
		return msg == null || "".equals(msg)
	}

	@Keyword
	public static boolean stringContains(String resp, String msg) {
		if (msg.equals("")) {
			msg = "null"
		}
		return (resp.indexOf(msg) != -1)
	}

	@Keyword
	public static boolean arrayContains(String[] array, String item) {
		for(int i=0;i<array.length;i++){
			if (array[i].equals(item)) {
				array[i] = null
				return true
			}
		}
		return false
	}

	@Keyword
	public static String findValueInMap(Map<String, String> map, String key_src) {
		for (String key : map.keySet()) {
			if (key_src.equals(key)){
				return map.get(key)
			}
		}

		return "";
	}

	//正则表达式 提取子字符串
	@Keyword
	public static String getValueFromKey(String soap,String key){
		String rgex = "\""+key+"\": \"(.*?)\"";
		return matchPattern(soap, rgex)
	}

	@Keyword
	public static String matchPattern(String soap, String rgex){
		Pattern pattern = Pattern.compile(rgex);
		Matcher m = pattern.matcher(soap);
		while(m.find()){
			return m.group(1);
		}
		return "";
	}

	public static String generateRandomString(int length) {
		StringBuffer sb = new StringBuffer();
		String ALLCHAR = "0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";
		Random random = new Random();
		for (int i = 0; i < length; i++) {
			sb.append(ALLCHAR.charAt(random.nextInt(ALLCHAR.length())));
		}

		return sb.toString();
	}
}
