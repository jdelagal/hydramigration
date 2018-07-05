GroovyShell shell = new GroovyShell()
def getOrgs = shell.parse(new File('getOrgs.groovy'))
def idConsumerOrg = getOrgs.getIdConsumerOrg()
if (idConsumerOrg.equals('5ae9cdbce4b002e84d9e0d25')){
	println idConsumerOrg
}
