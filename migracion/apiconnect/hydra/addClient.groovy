import groovy.json.*

println "INICIO GET ADD ONECLIENT"
println args[0]
println args[1]

def paramArray = []
paramArray.add(args[0])
paramArray.add(args[1])
paramArray.add(args[2])

println addClientToHydra(paramArray)

/* 
	11-jul-2018
	Funcion que retorna todos los clientes añadidos al Hydra Server
*/
def addClientToHydra(paramArray){
	def id = paramArray[0]
	def client_secret = paramArray[1]
	def hostHydra = paramArray[2]
	//def hostHydra = 'hydra-hydraserver.192.168.99.104.nip.io'

	def accessAdminToken =  getShell('getAccesAdminToken.groovy').getAccessAdminToken()

	def cadenaAEjecutarAddClient = 
						"""
		curl -s -k -X POST \\
		    -H "Content-Type: application/json" \\
		    -H "Authorization:bearer $accessAdminToken" \\
            -d '{ \\
                 id: $id, \\
                 client_secret:$client_secret, \\
                 scope:hydra.$id, \\
                 grant_types: [client_credentials, authorization_code] \\
                }' \\   
		    "https://$hostHydra/clients"
						  """
	println cadenaAEjecutarAddClient
	/*devolvemos un json con todos los clientes*/
    def jsonSlurper = new JsonSlurper()
	def jsonAddClient = jsonSlurper.parseText(cadenaAEjecutarAddClient.execute().text)
	return jsonAddClient
}

/* 
	11-jul-2018
	Funcion que retorna el codigo disponible en el script groovy de 
	entrada. Recibe en nombre del script con extension groovy
*/
def getShell(pScript){
	GroovyShell shell = new GroovyShell()
	/*parsea el codigo del script parseado*/
	return shell.parse(new File(pScript))
}

println "FINAL GET ADD ONECLIENT"