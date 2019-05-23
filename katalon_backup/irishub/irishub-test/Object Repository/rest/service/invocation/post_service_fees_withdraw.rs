<?xml version="1.0" encoding="UTF-8"?>
<WebServiceRequestEntity>
   <description></description>
   <name>post_service_fees_withdraw</name>
   <tag></tag>
   <elementGuidId>17e63517-54bc-408b-849e-b077d4d7cea0</elementGuidId>
   <selectorMethod>BASIC</selectorMethod>
   <useRalativeImagePath>false</useRalativeImagePath>
   <httpBody></httpBody>
   <httpBodyContent>{
  &quot;text&quot;: &quot;${HttpBody}&quot;,
  &quot;contentType&quot;: &quot;application/json&quot;,
  &quot;charset&quot;: &quot;UTF-8&quot;
}</httpBodyContent>
   <httpBodyType>text</httpBodyType>
   <httpHeaderProperties>
      <isSelected>true</isSelected>
      <matchCondition>equals</matchCondition>
      <name>Content-Type</name>
      <type>Main</type>
      <value>application/json</value>
   </httpHeaderProperties>
   <migratedVersion>5.4.1</migratedVersion>
   <restRequestMethod>POST</restRequestMethod>
   <restUrl>http://${lcdIP}/service/fees/${address}/withdraw?commit=true</restUrl>
   <serviceType>RESTful</serviceType>
   <soapBody></soapBody>
   <soapHeader></soapHeader>
   <soapRequestMethod></soapRequestMethod>
   <soapServiceFunction></soapServiceFunction>
   <variables>
      <defaultValue>'localhost:1317'</defaultValue>
      <description></description>
      <id>4591034a-e980-4c2c-a2e9-a52db7235f5d</id>
      <masked>false</masked>
      <name>lcdIP</name>
   </variables>
   <variables>
      <defaultValue>'faa1wfjguhakqqs0wkr7wk865pm8qyk32wfy5ldx9d'</defaultValue>
      <description></description>
      <id>1b6e74d2-76a6-4061-9143-39619ec7119a</id>
      <masked>false</masked>
      <name>address</name>
   </variables>
   <variables>
      <defaultValue>'{&quot;base_tx&quot;:{&quot;sequence&quot;:&quot;7&quot;,&quot;account_number&quot;:&quot;0&quot;,&quot;password&quot;:&quot;1234567890&quot;,&quot;chain_id&quot;:&quot;shilei-qa&quot;,&quot;gas_adjustment&quot;:&quot;1.2&quot;,&quot;fee&quot;:&quot;0.004iris&quot;,&quot;gas&quot;:&quot;200000&quot;,&quot;name&quot;:&quot;v0&quot;}}'</defaultValue>
      <description></description>
      <id>a5ed426a-af5a-4528-90ef-b96741e80504</id>
      <masked>false</masked>
      <name>HttpBody</name>
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
