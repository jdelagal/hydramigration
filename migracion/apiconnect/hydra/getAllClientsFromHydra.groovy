import groovy.json.*

println "INICIO GET ALL CLIENTS"
println args[0]

def paramArray = []
paramArray.add(args[0])


println getAllClientsFromHydra(paramArray)

/* 
	11-jul-2018
	Funcion que retorna todos los clientes añadidos al Hydra Server
*/
def getAllClientsFromHydra(paramArray){
	def hostHydra = paramArray[0]
	//def hostHydra = 'hydra-hydraserver.192.168.99.104.nip.io'

	def accessAdminToken =  getShell('getAccesAdminToken.groovy').getAccessAdminToken()

	def cadenaAEjecutarGetAllClients = 
						"""
		curl -s -k -X GET \\
		    -H \"Content-Type: application/json\" \\
		    -H \"Authorization:bearer $accessAdminToken\" \\
		    https://$hostHydra/clients
						  """
	println cadenaAEjecutarGetAllClients
	/*devolvemos un json con todos los clientes*/

	return cadenaAEjecutarGetAllClients.execute().text
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

println "FINAL GET CLIENTS"