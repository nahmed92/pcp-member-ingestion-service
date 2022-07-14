#ARG DELTA_DOCKER_REGISTRY=dockerdev.deltads.ent:7070
#ARG BASE_IMAGE_NAME=delta
#ARG BASE_IMAGE_TAG=jdk11-jre-alpine-appd-contrast

FROM openjdk:11-jre

MAINTAINER Delta Dental Group Member Online - DCM
VOLUME /tmp
ADD pcp-member-ingestion-service-1.0.0.jar pcp-member-ingestion-service-1.0.0.jar
ADD contrast.jar contrast.jar
RUN sh -c 'touch /pcp-member-ingestion-service-1.0.0.jar'
RUN sh -c 'touch /contrast.jar'
ENV JAVA_OPTS=""
ENTRYPOINT java $JAVA_OPTS \
    -javaagent:$APPD_ROOT/javaagent.jar \
    -javaagent:/contrast.jar \
    -Dcontrast.dir=/opt/docker/logs/contrast \
	-Dcontrast.override.appname=${SERVICE_NAME} \
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
    -Dspring.jpa.properties.hibernate.dialect=${JPA_PROPERTIES_DIALECT} \
    -Dspring.jpa.hibernate.c3p0.min.size=${JPA_MIN_SIZE} \
    -Dspring.jpa.hibernate.c3p0.max.size=${JPA_MAX_SIZE} \
    -Dspring.jpa.hibernate.c3p0.timeout=${JPA_TIMEOUT} \
    -Dservice.cert.file=${DD_KEYSTORE_FILE} \
	-Dservice.cert.password=${DD_KEYSTORE_PASSWORD} \
    -Dspring.jpa.hibernate.naming.implicit-strategy=${JPA_NAMING_IMPLICIT_STRATEGY} \
    -Dspring.jpa.hibernate.naming.physical-strategy=${JPA_NAMING_PHYSICAL_STRATEGY} \
    -Dspring.mssql.datasource.driver-class=${SPRING_MSSQL_DATASOURCE_DRIVE_CLASS} \
    -Dspring.mssql.datasource.jdbc-url=${SPRING_MSSQL_DATASOURCE_JDBC_URL} \
    -Dspring.mssql.datasource.url=${SPRING_MSSQL_DATASOURCE_JDBC_URL} \
    -Dspring.mssql.datasource.username=${SPRING_MSSQL_DATASOURCE_USERNAME} \
    -Dspring.mssql.datasource.password=${SPRING_MSSQL_DATASOURCE_PASSWORD} \    
    -Dmetavance.database.jdbcUrl=${METAVANCE_DATABASE_JDBC_URL} \
    -Dmetavance.database.userName=${METAVANCE_DATABASE_USERNAME} \
    -Dmetavance.database.password=${METAVANCE_DATABASE_PASSWORD} \
    -Dmetavance.database.driverClassName=${METAVANCE_DATABASE_DRIVERCLASS_NAME} \
    -Dmetavance.database.hibernate.dialect=${METAVANCE_DATABASE_HIBERNATE_DIALECT} \
    -Dtibco.client.url=${TIBCO_CLIENT_URL} \
    -Dtibco.client.user=${TIBCO_CLIENT_USER} \
    -Dtibco.client.password=${TIBCO_CLIENT_PASSWORD} \
    -Dtibco.call.retry.max.attempts=${TIBCO_CALL_RETRY_MAX_ATTEMPTS} \
    -Dpcp.calculation.service.endpoint=${PCP_CALCULATION_SERVICE_ENDPOINT} \
    -Dpcp.calculation.service.retry.max.attempts=${PCP_CALCULATION_SERVICE_RETRY_MAX_ATTEMPTS} \
    -Dpcp.config.service.endpoint=${PCP_CONFIG_SERVICE_ENDPOINT} \
    -Dpcp.config.service.retry.max.attempts=${PCP_CONFIG_SERVICE_RETRY_MAX_ATTEMPTS} \
    -Dservice.nodes=${SERVICE_NODES} \
    -Dcontroller.allowed.instance.id=${CONTROLLER_ALLOWED_INSTANCE_ID} \
    -Dservice.instance.id=${SERVICE_INSTANCE_ID} \
    -Denable.ingestion.controller=${ENABLE_INGESTION_CONTROLLER} \
    -Dpcp.ingestion.service.num.of.days=${PCP_INGESTION_SERVICE_NUM_OF_DAYS} \
    -Dingestion.workers.count=${INGESTION_WORKERS_COUNT} \
    -Dpublisher.workers.count=${PUBLISHER_WORKERS_COUNT} \
    -Dscheduling.job.ingestion.cron=${SCHEDULING_JOB_INGESTION_CRON} \
    -Dscheduling.job.ingestion.controller.cron=${SCHEDULING_JOB_INGESTION_CONTROLLER_CRON} \
    -Dscheduling.job.publisher.cron=${SCHEDULING_JOB_PUBLISHER_CRON} \
    -Djavax.net.ssl.trustStore=/usr/java/latest/jre/lib/security/cacerts \
    -jar /pcp-member-ingestion-service-1.0.0.jar
