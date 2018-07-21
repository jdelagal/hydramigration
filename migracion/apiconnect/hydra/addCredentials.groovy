import groovy.json.*

println "INICIO GET ADD CLIENTS"
println args[0]

def paramArray = []
paramArray.add(args[0])


println addCredentials(paramArray)

def addCredentials(paramArray){
	def hostHydra = paramArray[0]
	//def hostHydra = 'hydra-hydraserver.192.168.99.104.nip.io'
	
	// obtenemos todos los clientes de hydra
	//def allHydraClients =  getShell('getAllClientsFromHydra.groovy').getAllClientsFromHydra(paramArray)
	//obtenemos todos los clientes de redis
	
	def allRedisClients =  getShell('getAllClientsFromRedis.groovy').getAllClientsFromRedis()

	for(i=0;i<allRedisClients.size;i++){
		def client = allRedisClients[i]
		def jsonSlurper = new JsonSlurper()
		def clientAdd = jsonSlurper.parseText(client)

		def pFind = []
		pFind.add(clientAdd.client.id)
		pFind.add(hostHydra)
		def findClientInsideHydra =  getShell('findClientInsideHydra.groovy').findClientInsideHydra(pFind)
		if(findClientInsideHydra.message){
			if(findClientInsideHydra.message.contains("Not found")){
				def pAlta = []
				pAlta.add(clientAdd.client.id)
				pAlta.add(clientAdd.client.client_secret)
				pAlta.add(hostHydra)
				def altaRet =  getShell('addClient.groovy').addClientToHydra(pAlta)
			}
		}
	}
	return "OK"
}

/* 
	11-jul-2018
	Funcion que retorna el codigo disponible en el script groovy de 
	entrada. Recibe en nombre del script con extension groovy
*/
def getShell(pScript){
	GroovyShell shell = new GroovyShell()
	return shell.parse(new File('hydra/'+pScript))
}


println "FINAL GET ADD CLIENTS"