var apic = require('local:isp/policy/apim.custom.js');
var urlopen = require('urlopen');
var access_token = "";
const dbglog = console.options({'category':'apiconnect'});

introspection();

function introspection() {
    var adminUser = 'admin';
    var passwordAdminUser = 'admin-password'; 
    var tokenautorization = (adminUser+':'+passwordAdminUser);

    var bufferMessage = new Buffer(tokenautorization).toString('base64');
    
    var headersaccess =
     { "Content-Type": "application/json","Authorization": "Basic "+bufferMessage} 
    //dbglog.error("headersload: "+JSON.stringify(headersload));
    var optionsgetaccess = {
        target: "http://nodered:1880/oauth2/token",
        sslClientProfile : 'webapi-sslcli-mgmt',
        method: 'get',
        headers: headersaccess,
        contentType: 'application/json',
        timeout: 60,
        data: {"scope": "hydra", "grant_types":["client_credentials"]}
    };
    callAccessAdminTokenUrl(optionsgetaccess);
}
function callAccessAdminTokenUrl(options){
    urlopen.open(options, function(error, response) {
        if(error) {		
            session.output.write("urlopen error: "+JSON.stringify(error));
        } else {
            // get the response status code
            var responseStatusCode = response.statusCode;
            var responseReason = response.reason;
            dbglog.error("Response status code: " + responseStatusCode);
            dbglog.error("Response reason: " + responseReason);

        response.readAsJSON(function(error, responseData){
        if (error){
            throw error ;
        } else {         
                var access_token_request =  JSON.stringify(apic.getvariable('request.headers.authorization') ) ;
                access_token_request = access_token_request.split(" ")[1].trim().split("\"")[0]
                //dbglog.error("access_token_request: "+access_token_request);
                var headerintrospect =
                { "Content-Type": "application/json","Authorization": "bearer "+responseData.access_token}            
                var optionsintrospection = {
                    target: "http://nodered:1880/oauth2/introspect",
                    sslClientProfile : 'webapi-sslcli-mgmt',
                    method: 'post',
                    headers: headerintrospect,
                    contentType: 'application/json',
                    timeout: 60,
                    data: {"token": access_token_request}
                };
                callIntrospection(optionsintrospection)
            }});		
        }
    });
}
function callIntrospection(options){

    urlopen.open(options, function(error, response) {
        if(error) {		
            session.output.write("urlopen error: "+JSON.stringify(error));
        } else {
            // get the response status code
            var responseStatusCode = response.statusCode;
            var responseReason = response.reason;
            dbglog.error("Response status code: " + responseStatusCode);
            dbglog.error("Response reason: " + responseReason);

            response.readAsJSON(function(error, responseData){
        if (error){
            throw error ;
            } else {
                   if(responseData.active!==true){
                        apic.error("name", 401, "Unauthorized", "Access Token");      
                   }else{
                        //dbglog.error("responseData token: "+responseData.token);
                        dbglog.error("OK");  
                   }
                }
            });		
        }
    });
}
