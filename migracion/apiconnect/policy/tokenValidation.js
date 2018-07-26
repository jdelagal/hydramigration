var apic = require('local:isp/policy/apim.custom.js');
var urlopen = require('urlopen');
var access_token = "";
const dbglog = console.options({'category':'apiconnect'});
var props = apic.getPolicyProperty();
var hydra = props.hydra
var keycloak = props.keycloak

introspection(hydra);

/* 
	22-jul-2018
	Funcion que inicia el proceso de validacion
*/
function introspection(hydra) {
    //busca el client ID en la peticion
    var clientID =  JSON.stringify(apic.getvariable('client.app.id') ).split("\"")[1].split("\"")[0]  ;
    
    //ofuscamos el clientID
    var ofusquedClientID = "code:"+clientID;
    var bufferMessageClientID = new Buffer(ofusquedClientID).toString('base64');
    dbglog.error("bufferMessageClientID: "+bufferMessageClientID);
    var adminUser = 'admin';
    var passwordAdminUser = 'admin-password'; 
    var tokenautorization = (adminUser+':'+passwordAdminUser);

    var bufferMessage = new Buffer(tokenautorization).toString('base64');

    var headersaccess =
     { "Content-Type": "application/json","Authorization": "Basic "+bufferMessage} ;

    var target_token= hydra+"/oauth2/token";
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

/* 
	22-jul-2018
	Funcion que obteniene el token del administrador hydra
*/
function callAccessAdminTokenUrl(options, clientID, bufferMessageClientID){
    urlopen.open(options, function(error, response) {
        if(error) {		
            session.output.write("urlopen error: "+JSON.stringify(error));
        } else {
            // get the response status code
            var responseStatusCode = response.statusCode;
            var responseReason = response.reason;

        response.readAsJSON(function(error, responseData){
        if (error){
            throw error ;
        } else {         
                var access_token_request =  JSON.stringify(apic.getvariable('request.headers.authorization') ) ;
                access_token_request = access_token_request.split(" ")[1].split("\"")[0]

                var headerintrospect =
                { "Content-Type": "application/json","Authorization": "bearer "+responseData.access_token} ;

                var headerintrospectKeycloak =
                { "Content-Type": "application/x-www-form-urlencoded"} ; 

                var target_introspect= hydra+"/oauth2/introspect";
                var target_introspect_keycloak= keycloak;
                var target_getClientID = hydra+"/clients/"+clientID;
                var target_getBufferMessageClientID = hydra+"/clients/"+bufferMessageClientID;

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

/* 
	22-jul-2018
	Funcion que busca los clientes en hydra y keycloak
*/
function findClient(optionsGetClientID, optionsGetBufferMessageClientID, 
                                    optionsintrospection, optionsintrospectionKeycloak, access_token_request){

    urlopen.open(optionsGetClientID, function(error, response) {
        if(error) {		
            //session.output.write("urlopen error: "+JSON.stringify(error));
        } else {
            // get the response status code
            var responseStatusCode = response.statusCode;
            var responseReason = response.reason;

            //dbglog.error("optionsGetClientID.target: "+optionsGetClientID.target);
            response.readAsJSON(function(error, responseData){
        if (error){
                apic.error("name", 401, "Unauthorized", "Access Token NO fue validado en Hydra.");
            } else {

                    callIntrospection(optionsintrospection, 
                                    optionsGetBufferMessageClientID, optionsintrospectionKeycloak, access_token_request);
                }
            });		
        }
    });
}

/* 
	22-jul-2018
	Funcion que ejecuta introspeccion hydra
*/
function callIntrospection(optionsintrospection, 
                                optionsGetBufferMessageClientID, optionsintrospectionKeycloak, access_token_request){

    urlopen.open(optionsintrospection, function(error, response) {
        if(error) {		
            session.output.write("urlopen error: "+JSON.stringify(error));
        } else {
            // get the response status code
            var responseStatusCode = response.statusCode;
            var responseReason = response.reason;

            response.readAsJSON(function(error, responseData){
        if (error){
            throw error ;
            } else {
                   if(responseData.active!==true){
                        //apic.error("name", 401, "Unauthorized", "Access Token");
                        //buscamos en Keycloak antes de lanzar 401
                        findClientKeycloak(optionsGetBufferMessageClientID, optionsintrospectionKeycloak, access_token_request);  
                   }else{
                        dbglog.error("OK"); 
                        apic.error("resuesta", 200, "Hydra OK", "Access Token fue validado en Hydra."); 
                   }
                }
            });		
        }
    });
}

/* 
	22-jul-2018
	Funcion que busca cliente keycloak
*/
function findClientKeycloak(optionsGetBufferMessageClientID, optionsintrospectionKeycloak,
                                    access_token_request){

    urlopen.open(optionsGetBufferMessageClientID, function(error, response) {
        if(error) {		
            session.output.write("urlopen error: "+JSON.stringify(error));
        } else {
            // get the response status code
            var responseStatusCode = response.statusCode;
            var responseReason = response.reason;

            response.readAsJSON(function(error, responseData){
            if (error){
                apic.error("name", 401, "Unauthorized", "Access Token NO validado en Keycloak.");
            } else {
                //hay que recuperar el secreto
                //dbglog.error("hay que recuperar el secreto");
                var secreto = responseData.owner;
                var clientID = responseData.id;

                callIntrospectionKeycloak(optionsintrospectionKeycloak,clientID, secreto, access_token_request);
            } 
    
        });		
        }
    });
}

/* 
	22-jul-2018
	Funcion que ejecuta la introspeccion keycloak
*/

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

            response.readAsJSON(function(error, responseData){
        if (error){
            throw error ;
            } else {
                   if(responseData.active!==true){
                        apic.error("name", 401, "Unauthorized", "Access Token NO validado en Keycloak.");
                   }else{
                        dbglog.error("OK");  
                        apic.error("resuesta", 200, "Keycloak OK", "Access Token fue validado en Keycloak.");
                   }
                }
            });		
        }
    });
}
