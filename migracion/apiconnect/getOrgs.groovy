import groovy.json.*

println "INICIO GET ORGS"

String getIdConsumerOrg(){
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

	def tokenautorization = (comsumerOwner+':'+passwordOwner).bytes.encodeBase64().toString()

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