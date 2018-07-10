import groovy.json.*

println "INICIO GET ADD CLIENTS"

println addCredentials()

def addCredentials(){
	//obtenemos el access token del admin
	def shellAdminToken = getShell('getAccesAdminToken.groovy')
	def accessAdminToken =  shellAdminToken.getAccessAdminToken()
	
	// obtenemos todos los clientes de hydra
	def shellAllHydraClients = getShell('getAllClientsFromHydra.groovy')
	def allHydraClients =  shellAllHydraClients.getAllClientsFromHydra()

	//obtenemos todos los clientes de redis
	def shellAllRedisClients = getShell('getAllClientsFromRedis.groovy')
	def allRedisClients =  shellAllRedisClients.getAllClientsFromRedis()
	return allRedisClients
	def cadAddCredentials = """

						    """
}



def getShell(pScript){
	GroovyShell shell = new GroovyShell()
	return shell.parse(new File(pScript))
}


println "FINAL GET ADD CLIENTS"