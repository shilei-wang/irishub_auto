<?xml version="1.0" encoding="UTF-8"?>
<WebServiceRequestEntity>
   <description></description>
   <name>get_service_response</name>
   <tag></tag>
   <elementGuidId>6980d1d1-dd0e-419f-96ba-91e3ae5ea089</elementGuidId>
   <selectorMethod>BASIC</selectorMethod>
   <useRalativeImagePath>false</useRalativeImagePath>
   <httpBody></httpBody>
   <httpBodyContent></httpBodyContent>
   <httpBodyType></httpBodyType>
   <migratedVersion>5.4.1</migratedVersion>
   <restRequestMethod>GET</restRequestMethod>
   <restUrl>http://${lcdIP}/service/response/${reqChainId}/${reqId}</restUrl>
   <serviceType>RESTful</serviceType>
   <soapBody></soapBody>
   <soapHeader></soapHeader>
   <soapRequestMethod></soapRequestMethod>
   <soapServiceFunction></soapServiceFunction>
   <variables>
      <defaultValue>'shilei-qa'</defaultValue>
      <description></description>
      <id>3524c734-7046-4a79-948b-7a310085a1cd</id>
      <masked>false</masked>
      <name>reqChainId</name>
   </variables>
   <variables>
      <defaultValue>'6935-6930-0'</defaultValue>
      <description></description>
      <id>f6b2ecd0-b14e-440e-973d-2962dc99699b</id>
      <masked>false</masked>
      <name>reqId</name>
   </variables>
   <variables>
      <defaultValue>'localhost:1317'</defaultValue>
      <description></description>
      <id>dc4efa81-1c94-44bc-aa7a-f59a96ae1d5f</id>
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
