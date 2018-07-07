import groovy.json.*

println "INICIO GET APPSAVECREDENTIALS"

def paramArray = []
paramArray.add(args[0])
paramArray.add(args[1])
paramArray.add(args[2])
paramArray.add(args[3])
paramArray.add(args[4])

def shellCredentials = getShell('addConsumers.groovy')
def credentials  = shellCredentials.getCredentials(paramArray)

for(i=0;i<credentials.length;i++){
	def cred = credentials[i]
	def jsonSlurper = new JsonSlurper()
	def jsonCred = jsonSlurper.parseText(cred)

	if (jsonCred!=null){
		def clientID = jsonCred.clientID
		def clientSecret = jsonCred.clientSecret

		def command = """
			redis-cli \
			hmset $clientID secreto $clientSecret entorno dev
			  		  """
	 	command.execute().text
	 	println clientID + ': secreto y entorno guardado'
	}
	
} 

def getShell(pScript){
	GroovyShell shell = new GroovyShell()
	return shell.parse(new File(pScript))
}

println "FINAL GET APPSAVECREDENTIALS"