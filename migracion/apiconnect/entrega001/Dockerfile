FROM jdelagal/savecredentials:version1

MAINTAINER jose.delagala@ust-global.com

ADD *.groovy /scripts/
ADD *.json /scripts/
RUN chmod +x /scripts

WORKDIR scripts

CMD [ "groovy", "getOrgs.groovy jdelagal@gmail.com \!n0r1t5@C apim factoriaustglobal.sb myorganization" ]