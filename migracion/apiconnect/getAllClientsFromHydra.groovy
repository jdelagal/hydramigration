import groovy.json.*

println "INICIO GET ALL CLIENTS"

println getAllClientsFromHydra()

def getAllClientsFromHydra(){
	def shellAdminToken = getShell('getAccesAdminToken.groovy')
	def accessAdminToken =  shellAdminToken.getAccessAdminToken()

	def cadenaAEjecutarGetAllClients = 
						"""
		curl -s -k -X GET \\
		    -H \"Content-Type: application/json\" \\
		    -H \"Authorization:bearer $accessAdminToken\" \\
		    https://hydra-hydraserver.192.168.99.104.nip.io/clients
						  """
	//println cadenaAEjecutarGetAllClients
    def jsonSlurper = new JsonSlurper()
	def jsonAllClients = jsonSlurper.parseText(cadenaAEjecutarGetAllClients.execute().text)
	return jsonAllClients
}


def getShell(pScript){
	GroovyShell shell = new GroovyShell()
	return shell.parse(new File(pScript))
}

println "FINAL GET CLIENTS"