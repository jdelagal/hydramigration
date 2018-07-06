import groovy.json.*

println "INICIO GET APPCREDRESET"

def paramArray = []
paramArray.add(args[0])
paramArray.add(args[1])
paramArray.add(args[2])
paramArray.add(args[3])
paramArray.add(args[4])

println getCredentials(paramArray)

String[] getCredentials(paramArray){
	/*parametros de entrada*/
	def consumerOwner = paramArray[0]
	//def comsumerOwner = 'jdelagal@gmail.com'
	def passwordOwner = paramArray[1]
	//def passwordOwner = '\!n0r1t5@C'
	def hostManager = paramArray[2]
	//def hostManager = 'apim'
	def contextOrgProvider = paramArray[3]
	//def contextOrgProvider = 'factoriaustglobal.sb'
	def orgConsumer = paramArray[4]
	//def orgConsumer='myorganization'

	def tokenautorization = (consumerOwner+':'+passwordOwner).bytes.encodeBase64().toString()

	/*parámatros de salida*/
	def sRetConsumers = []
	
	GroovyShell shell = new GroovyShell()
	def getOrgs = shell.parse(new File('getOrgs.groovy'))
	def idConsumerOrg = getOrgs.getIdConsumerOrg(paramArray)

	def getApps = shell.parse(new File('getApps.groovy'))
	def aApps = getApps.getIdApps(paramArray)

	for(i=0;i<2;i++){
		def appId = aApps[i];
		if (appId!=null){
			sRetConsumers.add(addConsumer(appId, idConsumerOrg, tokenautorization, paramArray ))
		}
	} 


	return sRetConsumers
		
}
def addConsumer(appId, idConsumerOrg, tokenautorization, paramArray){

	/*parametros de entrada*/
	def consumerOwner = paramArray[0]
	//def comsumerOwner = 'jdelagal@gmail.com'
	def passwordOwner = paramArray[1]
	//def passwordOwner = '\!n0r1t5@C'
	def hostManager = paramArray[2]
	//def hostManager = 'apim'
	def contextOrgProvider = paramArray[3]
	//def contextOrgProvider = 'factoriaustglobal.sb'
	def orgConsumer = paramArray[4]
	//def orgConsumer='myorganization'

	def command = """
		curl -k -v -X PUT \
			-H "Content-Type: application/json" \
			-H "Authorization:Basic $tokenautorization" \
			-H "X-IBM-APIManagement-Context: $contextOrgProvider" \
			-d @clientSecretTrue.json \
			https://$hostManager/v1/portal/orgs/$idConsumerOrg/apps/$appId/credentials/reset
					  """
					  
	def jsonSlurper = new JsonSlurper()
	return jsonSlurper.parseText(command.execute().text)
}


println "FINAL GET APPCREDRESET"