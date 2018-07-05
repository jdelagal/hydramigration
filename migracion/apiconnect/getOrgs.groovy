import groovy.json.*

println "INICIO GET ORGS"

def tokenautorization = 'amRlbGFnYWxAZ21haWwuY29tOiFuMHIxdDVAQw=='
def contextOrgProveedora = 'factoriaustglobal.sb'
def hostManager = 'apim'

def cadenaAEjecutarOrgs = """
curl -k -v   -H "Content-Type: application/json"
			 -H "Authorization:Basic $tokenautorization"
			 -H "X-IBM-APIManagement-Context: $contextOrgProveedora" 
			 -X GET  https://$hostManager/v1/portal/orgs
					  """
def jsonSlurper = new JsonSlurper()
def jsonOrgs = jsonSlurper.parseText(cadenaAEjecutarOrgs.execute().text)

println jsonOrgs

println "FINAL GET ORGS"