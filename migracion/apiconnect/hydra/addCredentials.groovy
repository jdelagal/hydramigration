import groovy.json.*

println "INICIO GET ADD CLIENTS"
println args[0]

def paramArray = []
paramArray.add(args[0])

println addCredentials(paramArray)

def addCredentials(paramArray){
	def hostHydra = paramArray[0]
	//def hostHydra = 'hydra-hydraserver.192.168.99.104.nip.io'
	
	//obtenemos el access token del admin
	def accessAdminToken =  getShell('getAccesAdminToken.groovy').getAccessAdminToken()

	// obtenemos todos los clientes de hydra
	def allHydraClients =  getShell('getAllClientsFromHydra.groovy').getAllClientsFromHydra(paramArray)
	println allHydraClients
	//obtenemos todos los clientes de redis
	def allRedisClients =  getShell('getAllClientsFromRedis.groovy').getAllClientsFromRedis()

	for(i=0;i<allRedisClients.size;i++){
		def client = allRedisClients[i];
		println client.id
		println client.client_secret
	} 

		
	def cadAddCredentials = """

						    """
}



def getShell(pScript){
	GroovyShell shell = new GroovyShell()
	return shell.parse(new File(pScript))
}


println "FINAL GET ADD CLIENTS"