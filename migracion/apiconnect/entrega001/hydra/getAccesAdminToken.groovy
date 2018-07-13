import groovy.json.*

println "INICIO GET ACCESSADMINTOKEN"
def paramArray = []
paramArray.add(args[0])
//println getAccessAdminToken()
/* 
	11-jul-2018
	Funcion que retorna el access token del usuario administrador
	de Hydra Server
*/
def getAccessAdminToken(paramArray){
	/*parametros de entrada*/
	def hostHydra = paramArray[0]
	//println "hostHydra "+hostHydra
	def adminUser = 'admin'
	def passwordAdminUser = 'admin-password'
	def tokenautorization = (adminUser+':'+passwordAdminUser).bytes.encodeBase64().toString()

	def cadenaAEjecutarGetAccessAdminToken = 
						"""
	curl -s -k -X POST \\
        -H \"Content-Type: application/x-www-form-urlencoded\" \\
        -H \"Authorization: Basic $tokenautorization\" \\
        -d grant_type=client_credentials \\
        -d scope=hydra \\
        https://$hostHydra/oauth2/token 
						  """
	//println cadenaAEjecutarGetAccessAdminToken
	/*devolvemos el campo access token del json de salida*/
	def jsonSlurper = new JsonSlurper()
	def jsonAccessAdminToken = jsonSlurper.parseText(cadenaAEjecutarGetAccessAdminToken.execute().text)
	return jsonAccessAdminToken.access_token
}




println "FINAL GET ACCESSADMINTOKEN"