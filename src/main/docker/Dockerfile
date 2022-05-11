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
    -Dspring.jpa.hibernate.ddl-auto=${JPA_HIBERNATE_DLL_AUTO} \
    -Dspring.jpa.properties.hibernate.format_sql=${JPA_HIBERNATE_FORMAT_SQL} \
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
    -Dspring.mssql.datasource.username=${SPRING_MSSQL_DATASOURCE_USERNAME} \
    -Dspring.mssql.datasource.password=${SPRING_MSSQL_DATASOURCE_PASSWORD} \    
    -Dmetavance.database.jdbcUrl=${METAVANCE_DATABASE_JDBC_URL} \
    -Dmetavance.database.userName=${METAVANCE_DATABASE_USERNAME} \
    -Dmetavance.database.password=${METAVANCE_DATABASE_PASSWORD} \
    -Dmetavance.database.driverClassName=${METAVANCE_DATABASE_DRIVERCLASS_NAME} \
    -Dmetavance.database.hibernate.dialect=${METAVANCE_DATABASE_HIBERNATE_DIALECT} \    
    -Dpcp.ingestion.service.tibco.ur=${PCP_INGESTION_SERVICE_TIBCOPCPMEMBER_URL} \
    -Dpcp.ingestion.service.basicAuthUser=${PCP_INGESTION_SERVICE_BASIC_AUTH_USER} \
    -Dpcp.ingestion.service.basicAuthPassword=${PCP_INGESTION_SERVICE_BASIC_AUTH_PASSWORD} \
    -Dpcp.ingestion.service.isUsedTibco=${PCP_INGESTION_SERVICE_IS_USED_TIBCO} \
    -Dpcp.ingestion.service.numOfDays=${PCP_INGESTION_SERVICE_NUMOFDAYS} \
    -Dpcp.calculation.service.endpoint=${PCP_CALCULATION_SERVICE_ENDPOINT} \
    -Dpcp.config.service.endpoint=${PCP_CONFIG_SERVICE_ENDPOINT} \
    -Dpcp.tibco.service.retry.maxattempts=${PCP_TIBCO_SERVICE_RETRY_MAXATTEMPTS}\
    -Dpcp.calculation.service.retry.maxattempts=${PCP_CALCULATION_SERVICE_RETRY_MAXATTEMPTS}\
    -Djob.pcp.contract.initial.delay=${JOB_PCP_CONTRACT_INITIAL_DELAY} \
    -Djob.pcp.contract.fixed.delay=${JOB_PCP_CONTRACT_FIXED_DELAY} \
    -Djob.post.contract.tocalculation.initial.delay=${JOB_POST_CONTRACT_TO_CALCULATON_INITIAL_DELAY} \
    -Djob.post.contract.tocalculation.fixed.delay=${JOB_POST_CONTRACT_TO_CALCULATON_FIXED_DELAY} \
    -jar /pcp-member-ingestion-service-1.0.0.jar
