
/**
 * This class is generated automatically by Katalon Studio and should not be modified or deleted.
 */

import java.lang.String

import com.kms.katalon.core.testdata.TestData


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
