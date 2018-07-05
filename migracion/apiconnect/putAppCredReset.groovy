import groovy.json.*

println "INICIO GET APPCREDRESET"
/*
def clientSecretTrue = new groovy.json.JsonSlurper()

clientSecretTrue={
	"clientSecret": true
}
*/
def command = '''
 curl -k -v -X PUT \
		-H "Content-Type: application/json" \
		-H "Authorization:Basic amRlbGFnYWxAZ21haWwuY29tOiFuMHIxdDVAQw==" \
		-H "X-IBM-APIManagement-Context: factoriaustglobal.sb" \
		-d @clientSecretTrue.json \
		 https://apim/v1/portal/orgs/5ae9cdbce4b002e84d9e0d25/apps/5b0bdfaae4b002e84d9e811e/credentials/reset
					  '''
def jsonSlurper = new JsonSlurper()
def json = jsonSlurper.parseText(command.execute().text)

println json


println "FINAL GET APPCREDRESET"