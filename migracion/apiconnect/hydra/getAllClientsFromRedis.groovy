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
	cadenaClients = cadenaClients.split("\\r")
	def cadenaClientsConSecreto = [];

	for(i=0;i<cadenaClients.length;i++){
		def client = cadenaClients[i];
		def ret = buscaClientConSecreto(client)
		if(ret.contains("1")){
			def jsonClient = {}

			def secretoId = getSecreto(client)
			jsonClient.id = client
			jsonClient.client_secret = secretoId
			//println jsonClient.id
			//println jsonClient.client_secret
			cadenaClientsConSecreto.add(jsonClient)	
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