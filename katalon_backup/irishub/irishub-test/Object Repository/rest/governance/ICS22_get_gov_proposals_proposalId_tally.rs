<?xml version="1.0" encoding="UTF-8"?>
<WebServiceRequestEntity>
   <description></description>
   <name>ICS22_get_gov_proposals_proposalId_tally</name>
   <tag></tag>
   <elementGuidId>def44e70-723f-45bb-ab25-22a2790bd711</elementGuidId>
   <selectorMethod>BASIC</selectorMethod>
   <useRalativeImagePath>false</useRalativeImagePath>
   <httpBody></httpBody>
   <httpBodyContent></httpBodyContent>
   <httpBodyType></httpBodyType>
   <migratedVersion>5.4.1</migratedVersion>
   <restRequestMethod>GET</restRequestMethod>
   <restUrl>http://${lcdIP}/gov/proposals/${proposalId}/tally_result</restUrl>
   <serviceType>RESTful</serviceType>
   <soapBody></soapBody>
   <soapHeader></soapHeader>
   <soapRequestMethod></soapRequestMethod>
   <soapServiceFunction></soapServiceFunction>
   <variables>
      <defaultValue>'localhost:1317'</defaultValue>
      <description></description>
      <id>d434237e-a5d1-4929-be33-0956359c84bf</id>
      <masked>false</masked>
      <name>lcdIP</name>
   </variables>
   <variables>
      <defaultValue>'14'</defaultValue>
      <description></description>
      <id>f0cce3ae-d62d-4084-bb14-1f604e4c0ca5</id>
      <masked>false</masked>
      <name>proposalId</name>
   </variables>
   <verificationScript>import static org.assertj.core.api.Assertions.*

import com.kms.katalon.core.testobject.RequestObject
import com.kms.katalon.core.testobject.ResponseObject
import com.kms.katalon.core.webservice.keyword.WSBuiltInKeywords as WS
import com.kms.katalon.core.webservice.verification.WSResponseManager

import groovy.json.JsonSlurper
import internal.GlobalVariable as GlobalVariable

RequestObject request = WSResponseManager.getInstance().getCurrentRequest()

ResponseObject response = WSResponseManager.getInstance().getCurrentResponse()
</verificationScript>
   <wsdlAddress></wsdlAddress>
</WebServiceRequestEntity>
