
/**
 * This class is generated automatically by Katalon Studio and should not be modified or deleted.
 */

import java.lang.String

import com.kms.katalon.core.testdata.TestData


def static "rest.GetAddressByKey.getAddressByKey"(
    	String keyName	) {
    (new rest.GetAddressByKey()).getAddressByKey(
        	keyName)
}

def static "init.Faucet.initCmd"() {
    (new init.Faucet()).initCmd()
}

def static "init.Faucet.initRest"() {
    (new init.Faucet()).initRest()
}

def static "utils.CmdUtils.generateCmd"(
    	String cmd	
     , 	TestData data	
     , 	int dindex	) {
    (new utils.CmdUtils()).generateCmd(
        	cmd
         , 	data
         , 	dindex)
}

def static "utils.CmdUtils.addTxFee"(
    	String cmd	
     , 	TestData data	
     , 	int dindex	) {
    (new utils.CmdUtils()).addTxFee(
        	cmd
         , 	data
         , 	dindex)
}

def static "utils.CmdUtils.sendRequest"(
    	String obj	
     , 	String cmd	
     , 	int delay	) {
    (new utils.CmdUtils()).sendRequest(
        	obj
         , 	cmd
         , 	delay)
}

def static "utils.CmdUtils.printLog"(
    	String msg	) {
    (new utils.CmdUtils()).printLog(
        	msg)
}

def static "utils.CmdUtils.generateRandomID"() {
    (new utils.CmdUtils()).generateRandomID()
}

def static "utils.CmdUtils.Parse"(
    	String msg	) {
    (new utils.CmdUtils()).Parse(
        	msg)
}

def static "utils.CmdUtils.getAddressFromName"(
    	String msg	
     , 	String type	) {
    (new utils.CmdUtils()).getAddressFromName(
        	msg
         , 	type)
}

def static "utils.CmdUtils.sendIris"(
    	String source	
     , 	String dest	
     , 	String amount	) {
    (new utils.CmdUtils()).sendIris(
        	source
         , 	dest
         , 	amount)
}

def static "utils.CmdUtils.getBalance"(
    	String dest	
     , 	String type	) {
    (new utils.CmdUtils()).getBalance(
        	dest
         , 	type)
}

def static "utils.CmdUtils.createNewAccount"(
    	String faucet	
     , 	String amount	) {
    (new utils.CmdUtils()).createNewAccount(
        	faucet
         , 	amount)
}

def static "utils.StringUtils.isNullOrEmpty"(
    	String msg	) {
    (new utils.StringUtils()).isNullOrEmpty(
        	msg)
}

def static "utils.StringUtils.stringContains"(
    	String resp	
     , 	String msg	) {
    (new utils.StringUtils()).stringContains(
        	resp
         , 	msg)
}

def static "rest.BaseTx.baseTxProduce"(
    	String name	
     , 	String password	) {
    (new rest.BaseTx()).baseTxProduce(
        	name
         , 	password)
}

def static "rest.GetValJson.getFirstValAddress"() {
    (new rest.GetValJson()).getFirstValAddress()
}
