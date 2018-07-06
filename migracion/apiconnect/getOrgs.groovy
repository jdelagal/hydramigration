import groovy.json.*

println "INICIO GET ORGS"
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

//println getIdConsumerOrg(paramArray)

String getIdConsumerOrg(paramArray){
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

	def cadenaAEjecutarOrgs = """
	curl -k -v   -H "Content-Type: application/json"
				 -H "Authorization:Basic $tokenautorization"
				 -H "X-IBM-APIManagement-Context: $contextOrgProvider" 
				 -X GET  https://$hostManager/v1/portal/orgs
						  """
	def jsonSlurper = new JsonSlurper()
	def jsonOrgs = jsonSlurper.parseText(cadenaAEjecutarOrgs.execute().text)
		/*buscamos el id de nuestra organizacion*/
	for(i=0;i<jsonOrgs.size;i++){
		def org = jsonOrgs[i];
		if (orgConsumer.equals(org.name)){
			return org.id
		}
	}
}
println "FINAL GET ORGS"