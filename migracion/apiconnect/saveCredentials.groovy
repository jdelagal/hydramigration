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

println credentials

def getShell(pScript){
	GroovyShell shell = new GroovyShell()
	return shell.parse(new File(pScript))
}

println "FINAL GET APPSAVECREDENTIALS"