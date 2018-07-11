import groovy.json.*

println "INICIO GET CONFIG"

def cadenaAEjecutarConf = '''
curl -k -v  -H "Content-Type: application/json" -H -X GET  https://apim/v1/portal/config?originURL=https://ibmportal/factoriaustglobal/sb
					  '''
def jsonSlurper = new JsonSlurper()
def jsonConf = jsonSlurper.parseText(cadenaAEjecutarConf.execute().text)

println jsonConf

println "FINAL GET CONFIG"