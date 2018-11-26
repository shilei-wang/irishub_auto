package internal

import com.kms.katalon.core.configuration.RunConfiguration
import com.kms.katalon.core.testobject.ObjectRepository as ObjectRepository
import com.kms.katalon.core.testdata.TestDataFactory as TestDataFactory
import com.kms.katalon.core.testcase.TestCaseFactory as TestCaseFactory
import static com.kms.katalon.core.testobject.ObjectRepository.findTestObject
import static com.kms.katalon.core.testdata.TestDataFactory.findTestData
import static com.kms.katalon.core.testcase.TestCaseFactory.findTestCase

/**
 * This class is generated automatically by Katalon Studio and should not be modified or deleted.
 */
public class GlobalVariable {
     
    /**
     * <p></p>
     */
    public static Object node
     
    /**
     * <p></p>
     */
    public static Object chainId
     
    /**
     * <p></p>
     */
    public static Object commanderIP
     
    /**
     * <p></p>
     */
    public static Object lcdIP
     

    static {
        def allVariables = [:]        
        allVariables.put('default', ['node' : 'localhost:26657', 'chainId' : 'shilei-qa', 'commanderIP' : '10.1.2.168', 'lcdIP' : '10.1.4.202:1317'])
        allVariables.put('dev', allVariables['default'] + ['node' : '192.168.150.7:30657', 'chainId' : 'irishub-dev'])
        allVariables.put('qa', allVariables['default'] + ['node' : '192.168.150.7:31657', 'chainId' : 'irishub-dev'])
        allVariables.put('stage', allVariables['default'] + ['node' : '192.168.150.7:30657', 'chainId' : 'irishub-dev'])
        
        String profileName = RunConfiguration.getExecutionProfile()
        
        def selectedVariables = allVariables[profileName]
        node = selectedVariables['node']
        chainId = selectedVariables['chainId']
        commanderIP = selectedVariables['commanderIP']
        lcdIP = selectedVariables['lcdIP']
        
    }
}
