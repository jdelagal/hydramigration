	import groovy.json.*
    println "INICIO GET APPS"
	/*parametros de entrada*/
	//def consumerOwner = args[0]
	def comsumerOwner = 'jdelagal@gmail.com'
	//def consumerOwner = args[1]
	def passwordOwner = '!n0r1t5@C'
	//def consumerOwner = args[2]
	def hostManager = 'apim'
	//def consumerOwner = args[3]
	def contextOrgProvider = 'factoriaustglobal.sb'
	//def orgConsumer = args[4]
	def orgConsumer='myorganization'
	/*parametros de salida*/
	def idOrgConsumidora=''
	
	GroovyShell shell = new GroovyShell()
	def getOrgs = shell.parse(new File('getOrgs.groovy'))
	def idConsumerOrg = getOrgs.getIdConsumerOrg()


	def tokenautorization = (comsumerOwner+':'+passwordOwner).bytes.encodeBase64().toString()

	def cadenaAEjecutarApps = """
		curl -k -v  -H "Content-Type: application/json" \
				 -H "Authorization:Basic $tokenautorization"
				 -H "X-IBM-APIManagement-Context: $contextOrgProvider" 
				 -X GET  https://$hostManager/v1/portal/orgs/$idConsumerOrg/apps
						  """

	def jsonSlurper = new JsonSlurper()
	def jsonApps = jsonSlurper.parseText(cadenaAEjecutarApps.execute().text)

	println jsonApps

println "FINAL GET APPS"