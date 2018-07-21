import groovy.json.*

println "INICIO GET APPSAVECREDENTIALS"

def paramArray = []
paramArray.add(args[0])
paramArray.add(args[1])
paramArray.add(args[2])

def save(paramArray){

	def clientID = paramArray[0]
	def clientSecret = paramArray[1]
    def preclientID = paramArray[2]
    def obfuscatedClientID = (preclientID+':'+clientID).bytes.encodeBase64().toString()

	println clientID

	def command = """
				redis-cli \
				hmset $obfuscatedClientID secretodev $clientSecret
						"""
	//println command
    command.execute().text

}


/* 
	11-jul-2018
	Funcion que retorna el codigo disponible en el script groovy de 
	entrada. Recibe en nombre del script con extension groovy
*/
def getShell(pScript){
	GroovyShell shell = new GroovyShell()
	return shell.parse(new File('keycloak/'+pScript))
}


println "FINAL GET APPSAVECREDENTIALS"