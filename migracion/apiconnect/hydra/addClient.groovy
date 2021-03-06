import groovy.json.*

println "INICIO GET ADD ONECLIENT"
println args[0]
println args[1]
println args[2]

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
	def mode = paramArray[3]

	def paramFind=[]
	paramFind.add(hostHydra)
	//def hostHydra = 'hydra-hydraserver.192.168.99.104.nip.io'

	def accessAdminToken =  getShell('getAccesAdminToken.groovy').getAccessAdminToken(paramFind)
	def sRet = escribeJSON(paramArray)

	def cadenaAEjecutarAddClient = 
						"""
	curl -s -k -X POST \\
        -H \"Content-Type: application/json\" \\
        -H \"Authorization: bearer $accessAdminToken\" \\
        -d @hydra/volcado.json \\
        https://$hostHydra/clients 
						  """
			
	//println cadenaAEjecutarAddClient
	/*devolvemos un json con todos los clientes*/
    def jsonSlurper = new JsonSlurper()
	def jsonAddClient = jsonSlurper.parseText(cadenaAEjecutarAddClient.execute().text)
	boolean fileSuccessfullyDeleted =  new File("hydra/volcado.json").delete()  
	return jsonAddClient
}
/* 
	11-jul-2018
	Funcion que escribe un fichero JSON que es la entrada del curl
	Recibe un array con el clientID y secreto, devuelve OK si logro
	escribir correctamente con el nombre del clientID
*/
def escribeJSON(def paramArray){
	def jsonSlurper = new JsonSlurper()
	def id = paramArray[0]
	def client_secret = paramArray[1]
	def mode = paramArray[3]

	def clientFile=new File("hydra/client.json")

	def objetoClient=jsonSlurper.parse(clientFile) 
	if(objetoClient !=null){
		if(objetoClient.id != null && objetoClient.client_secret != null){
			objetoClient.id=paramArray[0]
			objetoClient.client_secret=paramArray[1]
			if('code'.equals(paramArray[3]) ){
				objetoClient.owner = paramArray[1]
			}
			def clientID = objetoClient.id
			def obJSON = new JsonBuilder(objetoClient).toPrettyString()
			def target = new File("hydra/volcado.json")
				target.withWriter { file ->
					obJSON.eachLine { line ->
						file.writeLine(line)
					}
			}	
		}
	}

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

println "FINAL GET ADD ONECLIENT"