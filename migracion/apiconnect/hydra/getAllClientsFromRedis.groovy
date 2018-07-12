import groovy.json.*

println "INICIO GET ALL CLIENTS"

println getAllClientsFromRedis()

/* 
	11-jul-2018
	Funcion que retorna todos los clientes guardados en REDIS
*/
def getAllClientsFromRedis(){
	def command = """
	redis-cli \
	keys *
			  """
	def cadenaClients =  command.execute().text

	def cadenaClientsPartida = cadenaClients.split("\r\n")
	def cadenaClientsConSecreto = [];

	for(i=0;i<cadenaClientsPartida.length ;i++){
		def client = cadenaClientsPartida[i];

		def ret = buscaClientConSecreto(client)

		if(ret.contains("1") ){
			String secretoId = getSecreto(client)
			secretoId = secretoId.split("\r\n")[0]
			def someMap = [
				'id': client,
				'client_secret': secretoId
			]
			def json = new groovy.json.JsonBuilder()
			json client: someMap
			def clientAdd =  groovy.json.JsonOutput.prettyPrint(json.toString())

			cadenaClientsConSecreto.add(clientAdd)	
		}
	}

	return cadenaClientsConSecreto
}

/* 
	11-jul-2018
	Funcion que retorna el secreto del cliente
*/
def getSecreto(def client){
	def cadenaClientsConSecretoEncontrado = [];
	def commandBuscaSecreto = """
					redis-cli \
					hget $client secretodev
							  """
    return commandBuscaSecreto.execute().text 
}

/* 
	11-jul-2018
	Funcion que retorna si existe secreto de cliente
*/
def buscaClientConSecreto(def client){
	def cadenaClientsConSecretoEncontrado = [];
	def commandBuscaSecreto = """
					redis-cli \
					hexists $client secretodev
							  """
    return commandBuscaSecreto.execute().text 
}

/* 
	11-jul-2018
	Funcion que retorna el codigo disponible en el script groovy de 
	entrada. Recibe en nombre del script con extension groovy
*/
def getShell(pScript){
	GroovyShell shell = new GroovyShell()
	return shell.parse(new File(pScript))
}

println "FINAL GET CLIENTS"