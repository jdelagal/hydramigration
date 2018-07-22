var apic = require('local:isp/policy/apim.custom.js');
var urlopen = require('urlopen');
var access_token = "";
const dbglog = console.options({'category':'apiconnect'});
var props = apic.getPolicyProperty();
var hydra = props.hydra
var keycloak = props.keycloak
//dbglog.error("props.hydra: "+props.hydra);
introspection(hydra);

function introspection(hydra) {
    //busca el client ID en la peticion
    var clientID =  JSON.stringify(apic.getvariable('client.app.id') ).split("\"")[1].split("\"")[0]  ;
    
    //ofuscamos el clientID
    var ofusquedClientID = "code:"+clientID;
    var bufferMessageClientID = new Buffer(ofusquedClientID).toString('base64');
    //dbglog.error("bufferMessageClientID: "+bufferMessageClientID);

    var adminUser = 'admin';
    var passwordAdminUser = 'admin-password'; 
    var tokenautorization = (adminUser+':'+passwordAdminUser);

    var bufferMessage = new Buffer(tokenautorization).toString('base64');
    dbglog.error("host: "+hydra);
    var headersaccess =
     { "Content-Type": "application/json","Authorization": "Basic "+bufferMessage} 
    //dbglog.error("headersload: "+JSON.stringify(headersload));
    var target_token= "http://"+hydra+"/oauth2/token";
    var optionsgetaccess = {
        target: target_token,
        sslClientProfile : 'webapi-sslcli-mgmt',
        method: 'get',
        headers: headersaccess,
        contentType: 'application/json',
        timeout: 60,
        data: {"scope": "hydra", "grant_types":["client_credentials", "authorization_code"]}
    };
    callAccessAdminTokenUrl(optionsgetaccess, clientID, bufferMessageClientID);
}
function callAccessAdminTokenUrl(options, clientID, bufferMessageClientID){
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
                access_token_request = access_token_request.split(" ")[1].split("\"")[0]
                //dbglog.error("access_token_request: "+access_token_request);
                var headerintrospect =
                { "Content-Type": "application/json","Authorization": "bearer "+responseData.access_token} ;

                var headerintrospectKeycloak =
                { "Content-Type": "application/x-www-form-urlencoded"} ; 

                var target_introspect= "http://"+hydra+"/oauth2/introspect";
                var target_introspect_keycloak= "http://"+keycloak+"/auth/realms/authcode/protocol/openid-connect/token/introspect";
                var target_getClientID = "http://"+hydra+"/clients/"+clientID;
                var target_getBufferMessageClientID = "http://"+hydra+"/clients/"+bufferMessageClientID;

                var optionsGetClientID = {
                    target: target_getClientID,
                    sslClientProfile : 'webapi-sslcli-mgmt',
                    method: 'get',
                    headers: headerintrospect,
                    contentType: 'application/json',
                    timeout: 60,
                    data: {}
                };

                var optionsGetBufferMessageClientID = {
                    target: target_getBufferMessageClientID,
                    sslClientProfile : 'webapi-sslcli-mgmt',
                    method: 'get',
                    headers: headerintrospect,
                    contentType: 'application/json',
                    timeout: 60,
                    data: {}
                };

                var optionsintrospection = {
                    target: target_introspect,
                    sslClientProfile : 'webapi-sslcli-mgmt',
                    method: 'post',
                    headers: headerintrospect,
                    contentType: 'application/json',
                    timeout: 60,
                    data: {"token": access_token_request}
                };

                var optionsintrospectionKeycloak = {
                    target: target_introspect_keycloak,
                    sslClientProfile : 'webapi-sslcli-mgmt',
                    method: 'post',
                    headers: headerintrospectKeycloak,
                    contentType: 'application/x-www-form-urlencoded',
                    timeout: 60,
                    data: {}
                };

                findClient(optionsGetClientID, optionsGetBufferMessageClientID, 
                                    optionsintrospection, optionsintrospectionKeycloak, access_token_request);
                
            }});		
        }
    });
}

function findClient(optionsGetClientID, optionsGetBufferMessageClientID, 
                                    optionsintrospection, optionsintrospectionKeycloak, access_token_request){

    urlopen.open(optionsGetClientID, function(error, response) {
        if(error) {		
            session.output.write("urlopen error: "+JSON.stringify(error));
        } else {
            // get the response status code
            var responseStatusCode = response.statusCode;
            var responseReason = response.reason;
            dbglog.error("Response status code: " + responseStatusCode);
            dbglog.error("Response reason: " + responseReason);
            //dbglog.error("optionsGetClientID.target: "+optionsGetClientID.target);
            response.readAsJSON(function(error, responseData){
        if (error){
            throw error ;
            } else {
                    //dbglog.error("responseData token: "+responseData.token);
                    callIntrospection(optionsintrospection, 
                                    optionsGetBufferMessageClientID, optionsintrospectionKeycloak, access_token_request);
                }
            });		
        }
    });
}

function findClientKeycloak(optionsGetBufferMessageClientID, optionsintrospectionKeycloak,
                                    access_token_request){

    urlopen.open(optionsGetBufferMessageClientID, function(error, response) {
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
                //hay que recuperar el secreto
                dbglog.error("hay que recuperar el secreto");
                var secreto = responseData.owner;
                var clientID = responseData.id;
                //dbglog.error("secreto: "+secreto+ " clientID: "+clientID);
                callIntrospectionKeycloak(optionsintrospectionKeycloak,clientID, secreto, access_token_request);
            } 
    
        });		
        }
    });
}

function callIntrospection(optionsintrospection, 
                                optionsGetBufferMessageClientID, optionsintrospectionKeycloak, access_token_request){

    urlopen.open(optionsintrospection, function(error, response) {
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
                        //apic.error("name", 401, "Unauthorized", "Access Token");
                        //buscamos en Keycloak antes de lanzar 401
                        findClientKeycloak(optionsGetBufferMessageClientID, optionsintrospectionKeycloak, access_token_request);  
                   }else{
                        //dbglog.error("responseData token: "+responseData.token);
                        dbglog.error("OK");  
                   }
                }
            });		
        }
    });
}


function callIntrospectionKeycloak( optionsintrospectionKeycloak,clientID, secreto, access_token_request){
    optionsintrospectionKeycloak.data = "token="+access_token_request+"&client_id="+clientID+"&"+ "client_secret="+secreto;

    //dbglog.error("optionsintrospectionKeycloak.data: "+optionsintrospectionKeycloak.data);  

    urlopen.open(optionsintrospectionKeycloak, function(error, response) {
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
