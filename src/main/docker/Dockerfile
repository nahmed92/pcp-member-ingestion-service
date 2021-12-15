#ARG DELTA_DOCKER_REGISTRY=dockerdev.deltads.ent:7070
#ARG BASE_IMAGE_NAME=delta
#ARG BASE_IMAGE_TAG=jdk11-jre-alpine-appd-contrast

FROM openjdk:11-jre

MAINTAINER Delta Dental Group Member Online - DCM
VOLUME /tmp
ADD contrast.jar contrast.jar
ADD pcp-member-ingestion-service-1.0.0.jar pcp-member-ingestion-service-1.0.0.jar
RUN sh -c 'touch /pcp-member-ingestion-service-1.0.0.jar'
RUN sh -c 'touch /contrast.jar'
ENV JAVA_OPTS=""
ENTRYPOINT java $JAVA_OPTS \
    -javaagent:$APPD_ROOT/javaagent.jar \
    -Dcontrast.dir=/opt/docker/logs/contrast \
	-Dcontrast.override.appname=${SERVICE_NAME} \
	-Dpcp.ingestion.service.tibcoPcpMemberUrl=${PCP_INGESTION_SERVICE_TIBCOPCPMEMBERURL}\
	-Dpcp.ingestion.service.basicAuthUser=${PCP_INGESTION_SERVICE_BASICAUTHUSER}\
	-Dpcp.ingestion.service.basicAuthPassword=${PCP_INGESTION_SERVICE_BASICAUTHPASSWORD}\
	-Dpcp.calculation.service.endpoint=${PCP_CALCULATION_SERVICE_ENDPOINT}\
	-Dcontrast.enabled=${CONTRAST_ENABLED} \
	-Dcontrast.env=${CONTRAST_ENV} \
	-Dcontrast.server.name=${CONTRAST_SERVER_NAME} \
	-Dappdynamics.controller.hostName=${APPD_CONTROLLER_HOSTNAME} \
	-Dappdynamics.controller.port=${APPD_PORT} \
	-Dappdynamics.agent.runtime.dir=${APPD_LOG_LOCATION} \
	-Dappdynamics.agent.applicationName=${APPD_APPLICATION_NAME} \
	-Dappdynamics.agent.nodeName=${APPD_NODE_NAME} \
	-Dappdynamics.agent.tierName=${APPD_TIER_NAME} \
	-Dappdynamics.agent.accountName=${APPD_ACCOUNT_NAME} \
	-Dappdynamics.agent.accountAccessKey=${APPD_ACCESS_KEY} \
	-Dappdynamics.agent.runtime.dir=${APPD_LOG_LOCATION} \
	-Dappdynamics.agent.uniqueHostId=${HOST_NAME} \
	-Dappdynamics.controller.ssl.enabled=${APPD_SSL_ENABLED} \
	-DplatformLogLevel=${PLATFORM_LOG_LEVEL} \
	-DsystemLogLevel=${SYSTEM_LOG_LEVEL} \
	-Dserver.port=${SERVER_PORT} \
	-Dservice.name=${SERVICE_NAME} \
	-Dspring.datasource.url=${DATASOURCE_URL} \
	-Dspring.datasource.username=${DATASOURCE_USERNAME} \
	-Dspring.datasource.password=${DATASOURCE_PASSWORD} \
	-Dspring.datasource.driver-class=${DATASOURCE_DRIVER_CLASS} \
    -Dspring.jpa.hibernate.ddl-auto=${JPA_HIBERNATE_DLL_AUTO} \
    -Dspring.jpa.properties.hibernate.format_sql=${JPA_HIBERNATE_FORMAT_SQL} \
    -Dspring.jpa.properties.hibernate.dialect=${JPA_PROPERTIES_DIALECT} \
    -Dspring.jpa.hibernate.c3p0.min.size=${JPA_MIN_SIZE} \
    -Dspring.jpa.hibernate.c3p0.max.size=${JPA_MAX_SIZE} \
    -Dspring.jpa.hibernate.c3p0.timeout=${JPA_TIMEOUT} \
    -Dspring.datasource.driverClassName=${DATASOURCE_DRIVER_CLASS} \
    -Dspring.jpa.hibernate.naming.implicit-strategy=${JPA_NAMING_IMPLICIT_STRATEGY} \
    -Dspring.jpa.hibernate.naming.physical-strategy=${JPA_NAMING_PHYSICAL_STRATEGY} \
    -Denvironment=${ENVIRONMENT} \
    -Dserver.monitor.file=${SERVICE_MONITOR_FILE} \
    -Djava.security.egd=file:/dev/./urandom \
    -jar /pcp-member-ingestion-service-1.0.0.jar
