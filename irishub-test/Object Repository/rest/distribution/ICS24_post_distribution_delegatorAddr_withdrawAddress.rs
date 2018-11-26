<?xml version="1.0" encoding="UTF-8"?>
<WebServiceRequestEntity>
   <description></description>
   <name>ICS24_post_distribution_delegatorAddr_withdrawAddress</name>
   <tag></tag>
   <elementGuidId>c1274ca9-1585-487d-b02b-9254b9df7d23</elementGuidId>
   <selectorMethod>BASIC</selectorMethod>
   <useRalativeImagePath>false</useRalativeImagePath>
   <httpBody></httpBody>
   <httpBodyContent>{
  &quot;text&quot;: &quot;{\n  \&quot;base_tx\&quot;: ${basetx},\n  \&quot;withdraw_address\&quot;: \&quot;${withdraw_address}\&quot;\n}&quot;,
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
   <httpHeaderProperties>
      <isSelected>true</isSelected>
      <matchCondition>equals</matchCondition>
      <name>Content-type</name>
      <type>Main</type>
      <value>application/json</value>
   </httpHeaderProperties>
   <httpHeaderProperties>
      <isSelected>true</isSelected>
      <matchCondition>equals</matchCondition>
      <name>Accept</name>
      <type>Main</type>
      <value>application/json</value>
   </httpHeaderProperties>
   <migratedVersion>5.4.1</migratedVersion>
   <restRequestMethod>POST</restRequestMethod>
   <restUrl>http://${lcdIP}/distribution/${delegatorAddr}/withdrawAddress</restUrl>
   <serviceType>RESTful</serviceType>
   <soapBody></soapBody>
   <soapHeader></soapHeader>
   <soapRequestMethod></soapRequestMethod>
   <soapServiceFunction></soapServiceFunction>
   <variables>
      <defaultValue>'faa12u0d68kwwpp0v62dqywys6rufz8zyl4zmvqgcn'</defaultValue>
      <description></description>
      <id>59c37bde-153c-4f04-b2b7-052a7857495f</id>
      <masked>false</masked>
      <name>delegatorAddr</name>
   </variables>
   <variables>
      <defaultValue>'{&quot;account_number&quot;:&quot;5&quot;,&quot;sequence&quot;:&quot;27&quot;,&quot;password&quot;:&quot;1234567890&quot;,&quot;chain_id&quot;:&quot;irishub-test&quot;,&quot;gas_adjustment&quot;:&quot;1.2&quot;,&quot;fee&quot;:&quot;0.004iris&quot;,&quot;name&quot;:&quot;lxtest0&quot;,&quot;gas&quot;:&quot;20000&quot;}'</defaultValue>
      <description></description>
      <id>ab3b134e-8e48-4f5b-bff3-54875ff62a9b</id>
      <masked>false</masked>
      <name>basetx</name>
   </variables>
   <variables>
      <defaultValue>'faa12u0d68kwwpp0v62dqywys6rufz8zyl4zmvqgcn'</defaultValue>
      <description></description>
      <id>4972a4e5-f026-457c-a48c-5d2ccd225c73</id>
      <masked>false</masked>
      <name>withdraw_address</name>
   </variables>
   <variables>
      <defaultValue>''</defaultValue>
      <description></description>
      <id>349eb2c4-6e55-4291-96da-f8933f97087b</id>
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
