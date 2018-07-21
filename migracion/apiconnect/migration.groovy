println "INICIO MIGRATION"

Properties props = new Properties()
File propsFile = new File('parametros.properties')
props.load(propsFile.newDataInputStream())
println props.getProperty('hydraHost')

//println args[0]

def paramArray = []
paramArray.add(props.getProperty('consumerOwner'))
paramArray.add(props.getProperty('passConsumerOwner'))
paramArray.add(props.getProperty('apiconnectHost'))
paramArray.add(props.getProperty('orgOwnerCatalog'))
paramArray.add(props.getProperty('orgConsumer'))
def paramArrayHydra = []
paramArrayHydra.add(props.getProperty('hydraHost'))
def paramArrayKeycloak= []
paramArrayKeycloak.add(props.getProperty('clientID'))
paramArrayKeycloak.add(props.getProperty('secret'))
paramArrayKeycloak.add(props.getProperty('preclientID'))
paramArrayKeycloak.add(props.getProperty('mode'))

migration(paramArray, paramArrayHydra, paramArrayKeycloak)

def migration(paramArray, paramArrayHydra, paramArrayKeycloak){
    def keycloak
    if('code'.equals(paramArrayKeycloak[3]) ){
         keycloak  =  getShell('keycloakMigration.groovy','keycloak').migration(paramArrayKeycloak)
    }
    
    def migration =  getShell('hydraMigration.groovy','hydra').migration(paramArrayHydra)

    def apiconnect =  getShell('apiconnectMigration.groovy', 'apiconnect').migration(paramArray)
}

println "FIN MIGRATION"

/* 
	11-jul-2018
	Funcion que retorna el codigo disponible en el script groovy de 
	entrada. Recibe en nombre del script con extension groovy
*/
def getShell(pScript, server){
	GroovyShell shell = new GroovyShell()
    def ret
    if('hydra'.equals(server) ){
        ret = shell.parse(new File('hydra/'+pScript))
    }
	if('apiconnect'.equals(server) ){
        ret =  shell.parse(new File('portal/'+pScript))
    }
	if('keycloak'.equals(server) ){
        ret =  shell.parse(new File('keycloak/'+pScript))
    }
    return ret
}