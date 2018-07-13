import groovy.json.*

println "INICIO GET ADD CLIENTS"
println args[0]

def paramArray = []
paramArray.add(args[0])


println migration(paramArray)

def migration(paramArray){
	def hostHydra = paramArray[0]
	//def hostHydra = 'hydra-hydraserver.192.168.99.104.nip.io'
	
	def migration =  getShell('addCredentials.groovy').addCredentials(paramArray)
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


println "FINAL GET ADD CLIENTS"