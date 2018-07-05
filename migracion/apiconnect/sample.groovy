import groovy.json.JsonSlurper
 
def authorization = "Authorization: Basic XXXXXXXXXXXXXXXXXXXXXXXXXXXXXX"
def contentType = "Content-Type: application/json"
def acceptEncoding = "Accept-Encoding: gzip,deflate"
def url = "https://jira.myserver.com/rest/api/2/"
 
def suites = manager.build.getTestResultAction().getResult().getSuites()
 
//select all tests by component (=TestSuite)
def searchBody = new groovy.json.JsonBuilder()
searchBody{
  jql "project = TEST AND type = Test AND component = test"
  startAt "0"
  maxResults "300"
  fields (["id","key","summary","status"])
}
 
def proc = ["curl", "-X", "POST", "-H",  authorization,"-H", contentType , "-H", acceptEncoding, url+"search", "-d", searchBody.toString()].execute()
def out = new StringBuilder()
def err = new StringBuilder()
proc.waitForProcessOutput(out, err)
if (out) manager.listener.logger.println "out:\n$out"
if (err) manager.listener.logger.println "err:\n$err"
 
jiraTests = new JsonSlurper().parseText(out.toString())
manager.listener.logger.println  "total: " + jiraTests.total + "jiraTests: " + jiraTests.maxResults
 
def  builderFailedToOpen = new groovy.json.JsonBuilder()
     builderFailedToOpen.transition{id "711"}
def  builderPassedToOpen = new groovy.json.JsonBuilder()
     builderPassedToOpen.transition{id "721"}
def  builderClosedToOpen = new groovy.json.JsonBuilder()
     builderClosedToOpen.transition{id "3"}
def  builderBlockedToOpen = new groovy.json.JsonBuilder()
     builderBlockedToOpen.transition{id "821"}
 
//set all test to Open
jiraTests.issues.each{
  manager.listener.logger.println it.fields.status.name
  switch(it.fields.status.name){
    case "Test passed":
      proc = ["curl", "-X", "POST", "-H",  authorization,"-H", contentType ,it.self +"/transitions", "-d", builderPassedToOpen.toString()].execute()
      proc.waitFor()
      manager.listener.logger.println proc.text
      break
    case "Test failed":
      proc = ["curl", "-X", "POST", "-H",  authorization,"-H", contentType ,it.self +"/transitions", "-d", builderFailedToOpen.toString()].execute()
      proc.waitFor()
      manager.listener.logger.println proc.text
      break
    case "Test Blocked":
      proc = ["curl", "-X", "POST", "-H",  authorization,"-H", contentType ,it.self +"/transitions", "-d", builderBlockedToOpen.toString()].execute()
      proc.waitFor()
      manager.listener.logger.println proc.text
      break
    case "Closed":
      proc = ["curl", "-X", "POST", "-H",  authorization,"-H", contentType ,it.self +"/transitions", "-d", builderClosedToOpen.toString()].execute()
      proc.waitFor()
      manager.listener.logger.println proc.text
      break
  }
}
 
def  builderOpenToPassed = new groovy.json.JsonBuilder()
     builderOpenToPassed.transition{id "741"}
def  builderOpenToFailed = new groovy.json.JsonBuilder()
     builderOpenToFailed.transition{id "731"}
 
def updatedTestCount = 0
suites.each(){
  manager.listener.logger.println it.getName()
  it.getCases().each{
      manager.listener.logger.println  it.getDisplayName() + " :" + it.isPassed()
      displayName = it.getDisplayName()
      testMatch  = jiraTests.issues.findAll(){it.fields.summary.toString() == displayName}
      manager.listener.logger.println "testMatch: " + testMatch.key + " " + testMatch.self
      switch (testMatch.size()){
         case 1:
            manager.listener.logger.println testMatch.self[0]
            if (it.isPassed()){
              proc = ["curl", "-X", "POST", "-H",  authorization,"-H", contentType ,testMatch.self[0] +"/transitions", "-d", builderOpenToPassed.toString()].execute()
            } else {
              proc = ["curl", "-X", "POST", "-H",  authorization,"-H", contentType ,testMatch.self[0] +"/transitions", "-d", builderOpenToFailed.toString()].execute()
            }
            proc.waitFor()
            manager.listener.logger.println proc.text
            manager.listener.logger.println  "Test found: " + it.getDisplayName()
            updatedTestCount++
           break
         case 0:
            manager.listener.logger.println  "Test not found: " + it.getDisplayName()
           break
         default:
            manager.listener.logger.println "Too much matches: " + it.getDisplayName()
           break
      }
  }
}
if (jiraTests.total > updatedTestCount){
 
  manager.listener.logger.println "WARNING: Jira total($jiraTests.total) is more than updated test quantity ($updatedTestCount)"
 
}