<?xml version="1.0" encoding="UTF-8"?>
<WebServiceRequestEntity>
   <description></description>
   <name>ICS24_get_distribution_delegatorAddr_withdrawAddress</name>
   <tag></tag>
   <elementGuidId>d5528245-3d28-4d99-ac8b-10b64ea55119</elementGuidId>
   <selectorMethod>BASIC</selectorMethod>
   <useRalativeImagePath>false</useRalativeImagePath>
   <httpBody></httpBody>
   <httpBodyContent></httpBodyContent>
   <httpBodyType></httpBodyType>
   <migratedVersion>5.4.1</migratedVersion>
   <restRequestMethod>GET</restRequestMethod>
   <restUrl>HTTP://${lcdIP}/distribution/${delegatorAddr}/withdrawAddress</restUrl>
   <serviceType>RESTful</serviceType>
   <soapBody></soapBody>
   <soapHeader></soapHeader>
   <soapRequestMethod></soapRequestMethod>
   <soapServiceFunction></soapServiceFunction>
   <variables>
      <defaultValue>'faa1tv6awzp83xcz2gaywjcefujduck5690xy64zma'</defaultValue>
      <description></description>
      <id>8fb823c1-144b-426b-ba66-3f3c303dc483</id>
      <masked>false</masked>
      <name>delegatorAddr</name>
   </variables>
   <variables>
      <defaultValue>'localhost:1317'</defaultValue>
      <description></description>
      <id>70b1e41e-7e98-4c9c-9e43-0bb84bd64b58</id>
      <masked>false</masked>
      <name>lcdIP</name>
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
