import groovy.json.*

println "INICIO GET APPS"

def cadenaAEjecutarApps = '''
curl -k -v  -H "Content-Type: application/json" \
			-H "Authorization:Basic amRlbGFnYWxAZ21haWwuY29tOiFuMHIxdDVAQw==" \
			-H "X-IBM-APIManagement-Context: factoriaustglobal.sb" \
			-X GET  https://apim/v1/portal/orgs/5ae9cdbce4b002e84d9e0d25/apps
					  '''
def jsonSlurper = new JsonSlurper()
def jsonApps = jsonSlurper.parseText(cadenaAEjecutarApps.execute().text)

println jsonApps

println "FINAL GET APPS"