import groovy.json.*

println "INICIO FIND CLIENT INSIDE HYDRA"
println args[0]
println args[1]

def paramArray = []
paramArray.add(args[0])
paramArray.add(args[1])

println findClientInsideHydra(paramArray)

/* 
	11-jul-2018
	Funcion que busca un cliente dado de alta en Hydra Server
*/
def findClientInsideHydra(paramArray){
	def id = paramArray[0]
	def hostHydra = paramArray[1]
	def paramFind=[]
	paramFind.add(hostHydra)
	//def hostHydra = 'hydra-hydraserver.192.168.99.104.nip.io'

	def accessAdminToken =  getShell('getAccesAdminToken.groovy').getAccessAdminToken(paramFind)

	def cadenaAEjecutarFindClient = 
						"""
	curl -s -k -X GET \\
        -H \"Content-Type: application/json\" \\
        -H \"Authorization: bearer $accessAdminToken\" \\
        https://$hostHydra/clients/$id 
						  """
			
	//println cadenaAEjecutarFindClient
	/*devolvemos un json con todos los clientes*/
    def jsonSlurper = new JsonSlurper()
	def jsonAddClient = jsonSlurper.parseText(cadenaAEjecutarFindClient.execute().text)
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
	return shell.parse(new File('hydra/'+pScript))
}

println "FIN FIND CLIENT INSIDE HYDRA"