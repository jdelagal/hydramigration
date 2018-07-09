import groovy.json.*

println "INICIO GET ALL CLIENTS"

println getAllClientsFromRedis()

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
			cadenaClientsConSecreto.add(client)	
		}		
	}
	return cadenaClientsConSecreto
}

def buscaClientConSecreto(def client){
	def cadenaClientsConSecretoEncontrado = [];
	def commandBuscaSecreto = """
					redis-cli \
					hexists $client secretodev
							  """
    return commandBuscaSecreto.execute().text 
}

def getShell(pScript){
	GroovyShell shell = new GroovyShell()
	return shell.parse(new File(pScript))
}

println "FINAL GET CLIENTS"