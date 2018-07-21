println "INICIO MIGRATION"
println args[0]

def paramArray = []
paramArray.add(args[0])

println migration(paramArray)

def migration(paramArray){
    def migration =  getShell('hydra/hydraMigration.groovy').migration(paramArray)
}




println "FIN MIGRATION"

/* 
	11-jul-2018
	Funcion que retorna el codigo disponible en el script groovy de 
	entrada. Recibe en nombre del script con extension groovy
*/
def getShell(pScript){
	GroovyShell shell = new GroovyShell()
	return shell.parse(new File(pScript))
}