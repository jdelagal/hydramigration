#!/bin/bash
#set -x
STATE=`uuidgen`
CLIENT_ID=testing
CLIENT_SECRET=944849b2-c038-4e08-839e-6671b452b115
FINAL_REDIRECT_URI=https://oauth2-oidc-debugger-hydraserver.192.168.99.104.nip.io/callback
USERNAME_=usuariosincliente
PASSWORD_=password123
IDP_BASE_URL=https://secure-keycloak-hydraserver.192.168.99.104.nip.io/auth/realms/authcode/protocol/openid-connect
CURL_OUTPUT=`curl -X GET "${IDP_BASE_URL}/auth?client_id=${CLIENT_ID}&response_type=code&scope=openid%20profile%20email&state=${STATE}&redirect_uri=${FINAL_REDIRECT_URI}" --insecure -D headers.out`
echo CURL_OUTPUT=${CURL_OUTPUT}
PASSWORD_SUBMIT_URL=`echo $CURL_OUTPUT | awk '{print $100 }' | cut -c 8- | sed 's/\"//g'`
echo PASSWORD_SUBMIT_URL=${PASSWORD_SUBMIT_URL}
CURL_OUTPUT=`curl -X POST "${PASSWORD_SUBMIT_URL}" -d "username=${USERNAME_}&password=${PASSWORD_}" --insecure -D /var/tmp/headers.out`
echo CURL_OUTPUT=$CURL_OUTPUT
echo /var/tmp/headers.out
REDIRECT_URL=`cat /var/tmp/headers.out | grep ^Location | cut -c10-`
echo $REDIRECT_URL
CODE=`echo $REDIRECT_URL | awk -F"?" '{ print $2 }' | awk -F"&" '{print $2}' | awk -F"=" '{print $2}' | sed 's/\r//g'`
echo CODE=$CODE
CURL_OUTPUT=`curl -X POST "${IDP_BASE_URL}/token" -d "state=${STATE}&code=${CODE}&grant_type=authorization_code&client_id=${CLIENT_ID}&client_secret=${CLIENT_SECRET}&redirect_uri=${FINAL_REDIRECT_URI}" --insecure -D headers.out`
echo CURL_OUTPUT=${CURL_OUTPUT}
ACCESS_TOKEN=`echo ${CURL_OUTPUT} | python -c "import sys, json; print json.load(sys.stdin)['access_token']"`
echo "-------------------------------------"
echo ACCESS_TOKEN=${ACCESS_TOKEN}
echo "-------------------------------------"
ID_TOKEN=`echo ${CURL_OUTPUT} | python -c "import sys, json; print json.load(sys.stdin)['id_token']"`
echo "-------------------------------------"
echo ID_TOKEN=${ID_TOKEN}
echo "-------------------------------------"
REFRESH_TOKEN=`echo ${CURL_OUTPUT} | python -c "import sys, json; print json.load(sys.stdin)['refresh_token']"`
echo "-------------------------------------"
echo REFRESH_TOKEN=${REFRESH_TOKEN}
echo "-------------------------------------"