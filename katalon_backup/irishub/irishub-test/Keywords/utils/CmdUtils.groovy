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
import com.google.gson.JsonArray;

public class CmdUtils {
	@Keyword
	public static String generateCmd(String cmd, TestData data, int dindex) {
		data.getColumnNames().each{columnName ->
			if (columnName.equals("cmd_result")||columnName.equals("rest_result")){
				return
			}

			String value = data.getValue(columnName, dindex)

			if ((columnName.equals("path") || columnName.equals("file")) && !value.equals("")){
				value = GlobalVariable.cmdFilePath+value
			}

			cmd = cmd.concat(' --').concat(columnName).concat('=').concat(value)
		}

		return cmd
	}

	@Keyword
	public static ResponseObject sendRequest(String obj, String cmd, String type) {
		String password = findTestData('base/faucet').getValue('password', 1)
		ResponseObject response = WS.sendRequest(findTestObject(obj, [('command') : cmd, ('args1') : password, ('commanderIP'):GlobalVariable.commanderIP]));
		if (type.equals("wait")) {
			waitUntilSeveralBlock(1)
		}
		return response
	}

	@Keyword
	public static ResponseObject sendRequest(String obj, String cmd, String password, String type) {
		ResponseObject response = WS.sendRequest(findTestObject(obj, [('command') : cmd, ('args1') : password, ('commanderIP'):GlobalVariable.commanderIP]));
		return response
	}

	@Keyword
	public static ResponseObject sendRequest(String obj, String cmd, String args1, String args2, String type) {
		ResponseObject response = WS.sendRequest(findTestObject(obj, [('command') : cmd, ('args1') : args1, ('args2') : args2,('commanderIP'):GlobalVariable.commanderIP]));
		return response
	}

	@Keyword
	public static ResponseObject sendRequest(String obj, String cmd, String args1, String args2, String args3, String type) {
		ResponseObject response = WS.sendRequest(findTestObject(obj, [('command') : cmd, ('args1') : args1, ('args2') : args2, ('args3') : args3, ('commanderIP'):GlobalVariable.commanderIP]));
		return response
	}

	/*@Keyword
	 public static ResponseObject sendRequest(String obj, String cmd, String password, String mnemonic, String type) {
	 ResponseObject response = WS.sendRequest(findTestObject(obj, [('command') : cmd, ('args1') : password, ('args2') : mnemonic,('commanderIP'):GlobalVariable.commanderIP]));
	 return response
	 }*/

	@Keyword
	public static boolean isMainnet() {
		return GlobalVariable.mainnet
	}

	@Keyword
	public static String addTxFee(String cmd, TestData data, int dindex) {
		data.getColumnNames().each{columnName ->
			if (columnName.equals("gas_adjustment")){
				return
			}

			if (!StringUtils.isNullOrEmpty(data.getValue(columnName, dindex))) {
				cmd = cmd.concat(' --').concat(columnName).concat('=').concat(data.getValue(columnName, dindex))
			}
		}

		cmd = cmd + " --commit"
		return cmd
	}

	@Keyword
	public static waitUntilSeveralBlock(int num){
		String cmd ="iriscli status".concat(' --node=').concat(GlobalVariable.node)
		ResponseObject response = CmdUtils.sendRequest('cmd/CmdWithOneArgs', cmd, "sync")
		JsonObject re = CmdUtils.Parse(response.responseBodyContent).get("sync_info").getAsJsonObject()
		int next_height = re.get("latest_block_height").getAsInt() + num

		while(true){
			response = CmdUtils.sendRequest('cmd/CmdWithOneArgs', cmd, "sync")
			re = CmdUtils.Parse(response.responseBodyContent).get("sync_info").getAsJsonObject()
			if (re.get("latest_block_height").getAsInt() >= next_height){
				break
			}
			sleep(1000)
		}
	}

	@Keyword
	public static pl(String msg) {
		String beginLog = '\n************** mark ***************\n'
		String endLog   = '\n*****************************\n'
		println beginLog+msg+endLog
	}

	@Keyword
	public static pl(Double data) {
		String beginLog = '\n************** mark ***************\n'
		String endLog   = '\n*****************************\n'
		println beginLog+String.valueOf(data)+endLog
	}

	@Keyword
	public static String generateRandomID() {
		String sources = "0123456789"
		Random rand = new Random()
		StringBuffer flag = new StringBuffer()
		for (int j = 0; j < 8; j++) {
			flag.append(sources.charAt(rand.nextInt(9)))
		}
		return flag.toString();
	}

	@Keyword
	public static JsonObject Parse(String msg){
		JsonParser parse =new JsonParser();  //创建json解析器
		try {
			JsonObject json=(JsonObject) parse.parse(msg);  //创建jsonObject对象
			return json
		} catch (JsonIOException e) {
			e.printStackTrace();
		} catch (JsonSyntaxException e) {
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}

	@Keyword
	public static JsonArray ParseArray(String msg){
		JsonParser parse =new JsonParser();  //创建json解析器
		try {
			JsonArray jsonArray=(JsonArray) parse.parse(msg);  //创建jsonObject对象
			return jsonArray
		} catch (JsonIOException e) {
			e.printStackTrace();
		} catch (JsonSyntaxException e) {
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}

	@Keyword
	public static String getAddressFromName(String msg, String type){
		String cmd
		if (type == "faa") {
			cmd = "iriscli keys show".concat(GlobalVariable.json)+" "+msg
		} else if (type == "fva") {
			cmd = "iriscli keys show --bech val".concat(GlobalVariable.json)+" "+msg
		} else {
			return "Error"
		}
		ResponseObject response = WS.sendRequest(findTestObject("cmd/CmdWithOneArgs", [('command') : cmd, ('commanderIP'):GlobalVariable.commanderIP]));
		JsonObject re = CmdUtils.Parse(response.responseBodyContent).getAsJsonObject()

		return re.get("address").getAsString()
	}

	@Keyword
	public static sendIris(String source, String dest, String amount){
		String cmd ="iriscli bank send".concat(GlobalVariable.chainId).concat(GlobalVariable.node)+" --amount="+amount+" --from="+source+" --to="+dest
		cmd = CmdUtils.addTxFee(cmd, findTestData('base/tx'), 1)
		ResponseObject response = CmdUtils.sendRequest('cmd/CmdWithOneArgs', cmd, "wait")
		WS.verifyEqual(StringUtils.stringContains(response.responseBodyContent,"tx hash"), true)
		return
	}

	@Keyword
	public static Double getBalance(String dest, String type){
		if (type == "name"){
			dest=getAddressFromName(dest, "faa")
		}

		String cmd ="iriscli bank account".concat(GlobalVariable.chainId).concat(GlobalVariable.node).concat(GlobalVariable.json).concat(" "+dest)
		ResponseObject response = CmdUtils.sendRequest('cmd/CmdWithOneArgs', cmd, "sync")
		WS.verifyEqual(StringUtils.stringContains(response.responseBodyContent,"coins"), true)

		JsonObject re = Parse(response.responseBodyContent).getAsJsonObject()
		String amount = re.get("value").getAsJsonObject().get("coins").getAsJsonArray().get(0).getAsJsonObject().get("amount").getAsString()
		amount = IrisattoToIris(amount).replace("iris", "")
		return Double.valueOf(amount)
	}

	@Keyword
	public static String getTxHash(String msg){
		String rgex = "tx hash: (.*?),";
		String TxHash = StringUtils.matchPattern(msg, rgex)
		return TxHash
	}

	@Keyword
	public static String createNewAccount(String faucet, String amount){
		String name = "user_"+generateRandomID()
		String cmd ="iriscli keys add "+name
		ResponseObject response = CmdUtils.sendRequest('cmd/CmdWithOneArgs', cmd, "sync")
		WS.verifyEqual(StringUtils.stringContains(response.responseBodyContent, name), true)
		if (!amount.equals("0iris")) {
			sendIris(faucet, getAddressFromName(name,"faa"), amount)
		}

		return name
	}

	@Keyword
	public static String plusIris(String x, String y){
		BigDecimal x_decimal = new BigDecimal(x.replace("iris", ""))
		BigDecimal y_decimal = new BigDecimal(y.replace("iris", ""))
		x_decimal = x_decimal*1000000000000000000
		y_decimal = y_decimal*1000000000000000000
		BigDecimal result = x_decimal+y_decimal
		result = result.setScale(0)

		return result.toString()
	}

	@Keyword
	public static boolean compareIgnoreFee(Double actual, Double expected){
		//Double fee = Double.valueOf(findTestData('base/tx').getValue("fee", 1).replace("iris", ""))
		//每个块都会增发， 所以现在把容错值放大到+1
		return ((expected+1)>actual) && (actual>(expected-1))
	}

	@Keyword
	public static String getValueFromTag(String msg, String key){
		//注意先要去掉换行符，方括号需要转义
		msg = msg.replace("\n","").replace(" ","")
		String rgex = "\"tags\":\\[(.*?)\\]}";
		String tags = "["+StringUtils.matchPattern(msg, rgex)+"]"

		Map<String, String> map = new HashMap<String, String>()
		JsonArray array = CmdUtils.ParseArray(tags)
		for (int i = 0; i < array.size() ; i++) {
			JsonObject item = array.get(i).getAsJsonObject()
			map.put(item.get("key").getAsString(), item.get("value").getAsString())
		}

		return map.get(key)
	}

	@Keyword
	public static String IrisToIrisatto(String x){
		BigDecimal decimal = new BigDecimal(x.replace("iris", ""))
		decimal = decimal*1000000000000000000
		decimal = decimal.setScale(0)

		return decimal.toString()
	}

	@Keyword
	public static String IrisattoToIris(String x){
		if (x.equals("null")){
			return "0iris"
		}

		BigDecimal decimal = new BigDecimal(x.replace("iris-atto", ""))
		decimal = decimal/1000000000000000000
		decimal = decimal.setScale(4, BigDecimal.ROUND_HALF_UP)

		return decimal.toString()+"iris"
	}

	public static double round(double value, int scale, int roundingMode) {
		BigDecimal bd = new BigDecimal(value);
		bd = bd.setScale(scale, roundingMode);
		double d = bd.doubleValue();
		bd = null;
		return d;
	}

	@Keyword
	public static String tax(String x, float rate){
		BigDecimal decimal = new BigDecimal(x.replace("iris", ""))
		decimal = decimal* (1-rate)
		decimal = decimal.setScale(4, BigDecimal.ROUND_HALF_UP)

		return decimal.toString()+"iris"
	}

	@Keyword
	public static double GetJsonLength(JsonObject json) {
		double jsonLength = 0

		for(String str:json.keySet()){
			jsonLength++
		}

		return jsonLength
	}

	//查询json数组
	@Keyword
	public static int findItemIndex(JsonArray array, String key, String value){
		for (int i = 0; i < array.size() ; i++) {
			JsonObject item = array.get(i).getAsJsonObject()
			if (item.get(key).getAsString() == value){
				return i
			}
		}

		return -1
	}
}
