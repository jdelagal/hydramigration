	import groovy.json.*
println "INICIO GET APPS"

println args[0]
println args[1]
println args[2]
println args[3]
println args[4]

def paramArray = []
paramArray.add(args[0])
paramArray.add(args[1])
paramArray.add(args[2])
paramArray.add(args[3])
paramArray.add(args[4])

//println getIdApps(paramArray)

String[] getIdApps(paramArray){
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

	/*parámatros de salida*/
	def sRet = []
	
	GroovyShell shell = new GroovyShell()
	def getOrgs = shell.parse(new File('portal/getOrgs.groovy'))
	def idConsumerOrg = getOrgs.getIdConsumerOrg(paramArray)


	def tokenautorization = (consumerOwner+':'+passwordOwner).bytes.encodeBase64().toString()

	def cadenaAEjecutarApps = """
		curl -k -v  -H "Content-Type: application/json" \
				 -H "Authorization:Basic $tokenautorization" \
				 -H "X-IBM-APIManagement-Context: $contextOrgProvider" \
				 -X GET  https://$hostManager/v1/portal/orgs/$idConsumerOrg/apps
						  """

	def jsonSlurper = new JsonSlurper()
	def jsonApps = jsonSlurper.parseText(cadenaAEjecutarApps.execute().text)

	for(i=0;i<jsonApps.size;i++){
		def app = jsonApps[i];
		if (app.id!=null){
			sRet.add(app.id)
		}
	}

	//println jsonApps
	return sRet
}
println "FINAL GET APPS"