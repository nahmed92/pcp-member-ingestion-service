pipeline {
	agent { label 'cx-agent' }
	options {
    	disableConcurrentBuilds()
		timeout(time: 24, unit: 'HOURS')
		buildDiscarder(logRotator(numToKeepStr: '10'))
  		}
    stages {
		stage('Load Poperties') {
			steps{ script{ globalTemplate.loadVariables() }
			     }
			}
		stage('Build') {
			steps {
				script{ mavenTemplate("mvn clean install -X -P coverage,jacoco:prepare-agent test jacoco:report jxr:jxr checkstyle:checkstyle") }
					}
			}
	    stage('Code Quality') {
		    parallel {
				stage('Code Coverage')
				{
         			steps
           			{
             			sh '''#RUN CODE COVERAGE ANALYSIS
 						${SCANNER_HOME}/bin/sonar-scanner'''
					}
        		}
			    stage('Code Security') {
				    steps {
             			sh '''#RUN fortify scan
 						echo fortify scan'''
          			}
        		}
		    }
	    }
    	stage('Archive Artifacts') {
      		steps {
          		archiveArtifacts(artifacts: '**/target/*.jar', allowEmptyArchive: true)
     			}
    		}
		stage('Publish Reports') {
      		parallel {
       			stage('Checkstyle Report' ) {
         			steps {
           				script
						{
							checkstyle(pattern: '**/target/checkstyle-result.xml', canRunOnFailed: true)
         				}
          			}
        		}
				stage('Junit report') {
					steps {
					 	junit(testResults: '**/target/surefire-reports/*.xml', allowEmptyResults: true, healthScaleFactor: 1)
					}
				}
				stage(' Jacoco Report') {
					steps {
						jacoco(
      						execPattern: 'target/*.exec',classPattern: 'target/classes',sourcePattern: 'src/main/java',exclusionPattern: 'src/test*')
						}
					}
				}
			}
		stage ('Build Docker') {
			steps {
				sh '''
				#Contrast-Security
				 curl -o contrast.jar -X GET https://app.contrastsecurity.com/Contrast/api/ng/6baceb38-de69-4696-b97f-7e60cf196c5b/agents/default/JAVA -H 'Authorization: UEd1cHRhQGRlbHRhLm9yZzpYM1lXVU5QN0ZMSE0yM1lL' -H 'API-Key: 5vz0MU1Ey5vcJCLN0ui1H1tSB7NQx9X1' -H 'Accept: application/json' -OJ
				#BUILD DOCKER IMAGE
				cp -f target/*.jar .
				cp src/main/docker/Dockerfile .
				docker build -t ${DOCKER_REGISTRY}/${JOB_NAME%%/*}:${SERVICE}_${BRANCH_NAME}_${BUILD_NUMBER} .
				#verify docker image
				docker images
				#push docker image
				docker login -p ${DOCKER_REGISTRY_PASSWORD} -u develop ${DOCKER_REGISTRY}
				docker push ${DOCKER_REGISTRY}/${JOB_NAME%%/*}:${SERVICE}_${BRANCH_NAME}_${BUILD_NUMBER}
				docker rmi ${DOCKER_REGISTRY}/${JOB_NAME%%/*}:${SERVICE}_${BRANCH_NAME}_${BUILD_NUMBER}'''
			}
		}
		stage('Deploy - DEV') {
			environment{
				RUNDECK_INSTANCE="${RUNDECK_DEPLOY_INSTANCE}"
				DEPLOYMENT_ENVIRONMENT = 'DEV'
				DOCKER_SERVER = 'aw-lx1045'
				JOB_ID="${RUNDECK_CX_DEPLOYEMNTS}"
				OPTIONS="""buildversion=${SERVICE}_${BRANCH_NAME}_${BUILD_NUMBER}
            			Application=${SERVICE}
            			DEPLOYMENT_ENV=${DEPLOYMENT_ENVIRONMENT}
						DOCKER_SERVER=${DOCKER_SERVER}
            			DOCKER_VARIABLES_ORG=${DOCKER_VARIABLES_DEV_ORG}
            			DOCKER_VARIABLES_REPO=${DOCKER_VARIABLES_DEV_REPO}"""
			}
			steps {
				script {

					 gitTagging("${SERVICE}_${BRANCH_NAME}_${BUILD_NUMBER}","Development/dd_cx_docker-dev")
					 gitTagging("${SERVICE}_${BRANCH_NAME}_${BUILD_NUMBER}","Development-private/dd_cx_docker-dev")
				     rundeckLibrary.deployJob()
                    }
			}
		}
		stage('Test Automation- Dev')
		{
			environment{
				DEPLOYMENT_ENVIRONMENT = 'DEV'
			}
			steps{
				script{
					 //testAutomationUtility.setupWorkspace("${TEST_REPO}")
					 //testAutomationUtility.testJasmine("npm run ${SERVICE}_${DEPLOYMENT_ENVIRONMENT} -- --suite=${SERVICE} -- --ZAPI")
					echo "DEV Testing"
					}
			}
		}
		stage('Deploy - DIT') {
			environment{
				RUNDECK_INSTANCE="${RUNDECK_DEPLOY_INSTANCE}"
				DEPLOYMENT_ENVIRONMENT = 'DIT'
				DOCKER_SERVER = "${CX_MS_DIT_SERVERS}"
				JOB_ID="${RUNDECK_CX_DEPLOYEMNTS}"
				OPTIONS="""buildversion=${SERVICE}_${BRANCH_NAME}_${BUILD_NUMBER}
						Application=${SERVICE}
						DEPLOYMENT_ENV=${DEPLOYMENT_ENVIRONMENT}
					DOCKER_SERVER=${DOCKER_SERVER}
						DOCKER_VARIABLES_ORG=${DOCKER_VARIABLES_DIT_ORG}
						DOCKER_VARIABLES_REPO=${DOCKER_VARIABLES_DIT_REPO}"""
			}
			steps {
         		timeout(time: 30, unit: 'MINUTES') {
            	input 'Code is being deployed to DIT, click PROCEED or ABORT ?' }
				script {
				gitTagging("${SERVICE}_${BRANCH_NAME}_${BUILD_NUMBER}","DIT/dd_cx_docker")
				gitTagging("${SERVICE}_${BRANCH_NAME}_${BUILD_NUMBER}","DIT-private/dd_cx_docker")
				rundeckLibrary.deployJob()
				}
			}
		}
		stage('Test Automation- DIT')
		{
			environment{
				DEPLOYMENT_ENVIRONMENT = 'DIT'
			}
			steps{
				script{
				//testAutomationUtility.setupWorkspace("${TEST_REPO}")
				//testAutomationUtility.testJasmine("npm run ${SERVICE}_${DEPLOYMENT_ENVIRONMENT} -- --suite=${SERVICE} -- --ZAPI")
					echo "DIT Testing"
					}
			}
		}
	    stage('Pre Deployment ') {
			parallel {
				stage('Create Build Note') {
					when {
					anyOf {
					branch 'develop'
					branch 'release'
					}
					}
					steps {
					    timeout(time: 120, unit: 'MINUTES') {
							script{
								CMVALIDATOR=input(message: "Do you want to Create Build Note, click PROCEED or PAUSE ?", submitter: 'DDC_BuildAndRelease_Jenkins_developer_users', submitterParameter: 'approver', id:"userInput",parameters: [
                                string(defaultValue: '',
                                description: 'Release Notes :Features / Bug Fixes / Comments',
                                name: 'DESCRIPTION')
                            ],)
							}
						}
						script {
							def parameterMap=new HashMap();
							def tag=DOCKER_REGISTRY+"/"+JOB_NAME+":"+SERVICE+"_"+BRANCH_NAME+"_"+BUILD_NUMBER
							def ApplicationArea="GMMO"
							def APP="gmmo-dcu-cu"
							parameterMap.put("PROJECT","CM");
							parameterMap.put("TAG",tag);
							parameterMap.put("APP",APP);
							parameterMap.put("STAGE","MOT3");
							parameterMap.put("APPLICATION_AREA",ApplicationArea);
							parameterMap.put("FEATURES",CMVALIDATOR.DESCRIPTION);
							release="MNT."+cmdb.jiraIssue("Build Note",parameterMap)
						}
					}
					post{
					    always{
					        script{
					            println "${currentBuild.result}"
					            def parameterMap=new HashMap();
					            parameterMap.put("ISSUE_ID",release);
								cmdb.jiraIssue("Transition Issues",parameterMap)
					        }
					    }
					}
				}
				stage('SCM Tagging') {
					steps {
						script {
							//tag the source code repository
							gitTagging("${SERVICE}_${BRANCH_NAME}_${BUILD_NUMBER}","DEVPROJECTS/${SERVICE}")
						}
					}
				}
			}
	    }
		stage('Build promotion') {

			when {
				anyOf {
				branch 'develop'
				branch 'release'
				}
			}
			environment {
				RELEASE = "${release}"
			}
			steps {
				build(job: 'Promote_Build', parameters: [string(name: 'RELEASE', value: "$release"), string(name: 'docker_app', value: "$SERVICE"), string(name: 'docker_tag', value: "${SERVICE}_${BRANCH_NAME}_${BUILD_NUMBER}"), string(name: 'SERVICE_TYPE', value: "${SERVICE_TYPE}")])
			}

		}




    		stage('Deploy -PIT')
		{
			environment {
				DEPLOYMENT_ENVIRONMENT = 'PIT'
				RELEASE = "${release}"
				SERVICE = "${SERVICE}"
				DOCKER_TAG = "${SERVICE}_${BRANCH_NAME}_${BUILD_NUMBER}"
				RUNDECK_INSTANCE="prod-rundeck"
			}
			steps {
		   		checkpoint 'Deploy - PIT';
				script {
					def parameterMap=new HashMap();
					def tag=DOCKER_REGISTRY+"/"+JOB_NAME+":"+SERVICE+"_"+BRANCH_NAME+"_"+BUILD_NUMBER
					def ApplicationArea="GMMO"
					def APP="gmmo-dcu-cu"
					parameterMap.put("PROJECT","CM");
					parameterMap.put("TAG",tag);
					parameterMap.put("APP",APP);
					parameterMap.put("STAGE","${DEPLOYMENT_ENVIRONMENT}");
					parameterMap.put("APPLICATION_AREA",ApplicationArea);
					//CM Request
					parameterMap.put("BUILD_NOTE",RELEASE);
					parameterMap.put("DEPLOYMENT_ENVIRONMENT",DEPLOYMENT_ENVIRONMENT);
					cmrecord_pit=cmdb.jiraIssue("CM Request",parameterMap)
					issueId+=","+cmrecord_pit
					parameterMap.put("INWARD_ISSUE_ID",cmrecord_pit);
					cmdb.jiraIssue("link Issues",parameterMap);
				}
				timeout(time: 1020, unit: 'MINUTES') {
					retry(510)
						{
						script{
							def parameterMap=new HashMap();
            				parameterMap.put("CM Request",cmrecord_pit);
            				def status=cmdb.jiraIssue("status",parameterMap)
							}
						}
				}
				script {
					rundeckLibrary.deployBlueGreen("$PIT_SECONDARY_BATCH_JOB","$PIT_PRIMARY_BATCH_JOB","$PIT_PRIMARY_SECONDARY_UP_BATCH_JOB")
				}
			}
			post{
				always {
					script {
							if("${currentBuild.result}" != "SUCCESS")
							{
							def parameterMap=new HashMap();
							parameterMap.put("comment","Deployment ${currentBuild.result} in "+DEPLOYMENT_ENVIRONMENT+"\n"+"${BUILD_URL}/console");
							parameterMap.put("CM Request",cmrecord_pit);
							cmdb.jiraIssue("comment",parameterMap)
							parameterMap.put("ISSUE_ID",cmrecord_pit);
							cmdb.jiraIssue("Transition Issues",parameterMap)
							}
					}
				}
			}
		}

		stage('Test Automation- PIT')
		{
			environment{
				DEPLOYMENT_ENVIRONMENT = 'PIT'
			}
			steps{
				script{
					//testAutomationUtility.setupWorkspace("${TEST_REPO}")
					//testAutomationUtility.testJasmine("npm run ${SERVICE}_${DEPLOYMENT_ENVIRONMENT} -- --suite=${SERVICE} -- --ZAPI")
                    echo "PIT Testing"
					}
			}
            post{
				always {
					script {
							def parameterMap=new HashMap();
							parameterMap.put("comment","Deployment ${currentBuild.result} in "+DEPLOYMENT_ENVIRONMENT+"\n"+"${BUILD_URL}/console");
							parameterMap.put("CM Request",cmrecord_pit);
							cmdb.jiraIssue("comment",parameterMap)
							parameterMap.put("ISSUE_ID",cmrecord_pit);
							cmdb.jiraIssue("Transition Issues",parameterMap)

					}
				}
			}
		}
		stage('Deploy - MOT')
		{
			environment {
				DEPLOYMENT_ENVIRONMENT = 'MOT'
				RELEASE = "${release}"
				SERVICE = "${SERVICE}"
				DOCKER_TAG = "${SERVICE}_${BRANCH_NAME}_${BUILD_NUMBER}"
				RUNDECK_INSTANCE="prod-rundeck"
			}
			steps {
		   		checkpoint 'Deploy - MOT';
				script {
					def parameterMap=new HashMap();
					def ApplicationArea="GMMO"
					def APP="gmmo-dcu-cu"
					def tag=DOCKER_REGISTRY+"/"+JOB_NAME+":"+SERVICE+"_"+BRANCH_NAME+"_"+BUILD_NUMBER
					parameterMap.put("PROJECT","CM");
					parameterMap.put("TAG",tag);
					parameterMap.put("APP",APP);
					parameterMap.put("STAGE","${DEPLOYMENT_ENVIRONMENT}");
					parameterMap.put("APPLICATION_AREA",ApplicationArea);
					//CM Request
					parameterMap.put("BUILD_NOTE",RELEASE);
					parameterMap.put("DEPLOYMENT_ENVIRONMENT",DEPLOYMENT_ENVIRONMENT);
					cmrecord_mot=cmdb.jiraIssue("CM Request",parameterMap)
					issueId+=","+cmrecord_mot
					parameterMap.put("INWARD_ISSUE_ID",cmrecord_mot);
					cmdb.jiraIssue("link Issues",parameterMap);
				}
				timeout(time: 720, unit: 'MINUTES') {
					retry(360)
						{
						script{
							def parameterMap=new HashMap();
            				parameterMap.put("CM Request",cmrecord_mot);
            				def status=cmdb.jiraIssue("status",parameterMap)
						}
					}
				}
				script {
				    rundeckLibrary.deployBlueGreen("$MOT_SECONDARY_BATCH_JOB","$MOT_PRIMARY_BATCH_JOB","$MOT_PRIMARY_SECONDARY_UP_BATCH_JOB")
				}
			}
			post{
				always {
					script {
							if("${currentBuild.result}" != "SUCCESS")
							{
							def parameterMap=new HashMap();
							parameterMap.put("comment","Deployment ${currentBuild.result} in "+DEPLOYMENT_ENVIRONMENT+"\n"+"${BUILD_URL}/console");
							parameterMap.put("CM Request",cmrecord_mot);
							cmdb.jiraIssue("comment",parameterMap)
							parameterMap.put("ISSUE_ID",cmrecord_mot);
							cmdb.jiraIssue("Transition Issues",parameterMap)
							}
					}
				}
			}
		}

		stage('Test Automation- MOT')
		{
			environment{
				DEPLOYMENT_ENVIRONMENT = 'MOT'
			}
			steps{
				script{
					//testAutomationUtility.setupWorkspace("${TEST_REPO}")
					//testAutomationUtility.testJasmine("npm run ${SERVICE}_${DEPLOYMENT_ENVIRONMENT} -- --suite=${SERVICE} -- --ZAPI")
                    echo "MOT Testing"
					}
			}
            post{
				always {
					script {

							def parameterMap=new HashMap();
							parameterMap.put("comment","Deployment ${currentBuild.result} in "+DEPLOYMENT_ENVIRONMENT+"\n"+"${BUILD_URL}/console");
							parameterMap.put("CM Request",cmrecord_mot);
							cmdb.jiraIssue("comment",parameterMap)
							parameterMap.put("ISSUE_ID",cmrecord_mot);
							cmdb.jiraIssue("Transition Issues",parameterMap)

					}
				}
			}
		}

		stage('Create CM Record-PROD') {
			environment{
				RELEASE="${release}"
				DEPLOYMENT_ENVIRONMENT="PROD"

			}
			steps {
				checkpoint 'Create CM Record-PROD';
				script {
					CMIMPLEMENTOR=input(message: "Do you want to promote build to Higher Enviornment , click PROCEED or ABORT ?", submitterParameter: 'approver')
					def ApplicationArea="GMMO"
					def APP="gmmo-dcu-cu"
            		def parameterMap=new HashMap();
					parameterMap.put("PROJECT","CM");
           	 		parameterMap.put("APP","${APP}");
					parameterMap.put("APPLICATION_AREA","${ApplicationArea}");
					//CM Request
           			parameterMap.put("BUILD_NOTE",RELEASE);
           			parameterMap.put("CMIMPLEMENTOR",CMIMPLEMENTOR);
           			parameterMap.put("CMVALIDATOR",CMVALIDATOR.approver);
					parameterMap.put("FEATURES",CMVALIDATOR.DESCRIPTION);
           			parameterMap.put("PO",PO);
					parameterMap.put("DEPLOYMENT_ENVIRONMENT",DEPLOYMENT_ENVIRONMENT);
					cmrecord=cmdb.jiraIssue("CM Request",parameterMap)
					parameterMap.put("OUTWARD_ISSUE_ID",issueId)
					parameterMap.put("INWARD_ISSUE_ID",cmrecord);
					cmdb.jiraIssue("link Issues",parameterMap);
					gitTagging("${RELEASE}","PROD/dd_cx_docker")
				}
			}
		}
		stage('PROD Readiness')
		{
			environment{
				RELEASE="${release}"
				DEPLOYMENT_ENVIRONMENT="PROD"
			}
			steps {
			    checkpoint "PROD Readiness"
			    timeout(time: 30, unit: 'MINUTES') {
					retry(16)
					{
						script{
							def parameterMap=new HashMap();
            				parameterMap.put("CM Request",cmrecord);
            				def status=cmdb.jiraIssue("status",parameterMap)
						}
					}
				}
			}
		}
		stage('PROD Secondary')
		{
			environment{
				RELEASE="${release}"
				DEPLOYMENT_ENVIRONMENT="PROD"
			}
			steps {
				checkpoint 'PROD Primary';
				timeout(time: 120, unit: 'MINUTES') {
					script {
					notificationUtility.emailNotification("Waiting","${SEND_MAIL}");
					notificationUtility.teamsNotification("Waiting")
					}
					input(message: "Do you want to Deploy ${RELEASE} to PROD Primary, click PROCEED or ABORT ?", submitter: 'DDC_BuildAndRelease_Jenkins_release_Users')
				}
				script{
				rundeckLibrary.deployJobWithPram($PROD_SECONDARY_PLATFORM_JOB, """buildversion=${RELEASE} application=${SERVICE} environment=${DEPLOYMENT_ENVIRONMENT}""")
				}
				}
			}

		stage('PROD Primary')
		{
			environment{
			RELEASE="${release}"
			DEPLOYMENT_ENVIRONMENT="PROD"
			}
			steps {
				checkpoint 'PROD Primary';
				timeout(time: 240, unit: 'MINUTES') {
					script {
					notificationUtility.emailNotification("Waiting","${SEND_MAIL}");
					notificationUtility.teamsNotification("Waiting")
					}
				input(message: "Do you want to Deploy ${RELEASE} to PROD Secondary, click PROCEED or ABORT ?", submitter: 'DDC_BuildAndRelease_Jenkins_release_Users')
				}
				script {
					rundeckLibrary.deployJobWithPram("${PROD_PRIMARY_PLATFORM_JOB}", """buildversion=${RELEASE}
					 application=${SERVICE}
					 environment=${DEPLOYMENT_ENVIRONMENT}""")
			}
			}
			}

		stage('PROD F5 Enable')
		{
			environment{
				RELEASE="${release}"
				DEPLOYMENT_ENVIRONMENT="PROD"
			}
			steps {
				checkpoint 'PROD F5 Enable';
				timeout(time: 120, unit: 'MINUTES') {
					script { notificationUtility.emailNotification("Waiting","${SEND_MAIL}");
					notificationUtility.teamsNotification("Waiting")
					}
					input(message: "Do you want to Deploy ${RELEASE} to PROD F5 Enable, click PROCEED or ABORT ?", submitter: 'DDC_BuildAndRelease_Jenkins_release_Users')
				}
				script {
					rundeckLibrary.deployJobWithPram("${PROD_PRIMARY_SECONDARY_UP_PLATFORM_JOB}", """buildversion=${RELEASE}
					 application=${SERVICE}
					 environment=${DEPLOYMENT_ENVIRONMENT}""")
			}
		}
			post{
				always {
					script {

							def parameterMap=new HashMap();
							parameterMap.put("comment","Deployment ${currentBuild.result} in "+DEPLOYMENT_ENVIRONMENT+"\n"+"${BUILD_URL}/console");
							parameterMap.put("CM Request",cmrecord);
							cmdb.jiraIssue("comment",parameterMap)
							parameterMap.put("ISSUE_ID",cmrecord);
							cmdb.jiraIssue("Transition Issues",parameterMap)

					}
				}
			}
		}
    }
	post {
		always {
			script {
				notificationUtility.emailNotification("${currentBuild.result}","${SEND_MAIL}");
				notificationUtility.teamsNotification("${currentBuild.result}")}
		}
	}
    tools {
		maven 'maven_pipeline'
		nodejs 'NodeJS 10.16.0'
		jdk 'java-11'
	}
    environment {
	SCANNER_HOME= tool 'SonarQubeScanner'
    //Application Area  and App as defined in Jira Change Management Project

	ApplicationArea="GMMO"
    APP="gmmo-dcu-cu"
    //team Dl and Release Management Team
	SEND_MAIL = "ITSSReleaseTeam@delta.org,KBasireddy@delta.org,skalahasthi2@delta.org,pkumar2@delta.org,nahmed@delta.org"
    //Application type
		SERVICE_TYPE="JAVA"
    //PO who will approve Prod CM Record in new Jira Workflow Meg
	    PO="ca27567"
    //Github Repository which contains automation test suites
   //TEST_REPO="https://rc-github.deltads.ent/DEVPROJECTS/jasmine-apitest-automation"
	 TEST_REPO="https://rc-github.deltads.ent/DEVPROJECTS/GroupConnectServiceTestAutomationJS"
         TEST_BRANCH="develop"
        issueId="--"
	cmrecord="--"
        cmrecord_mot3="--"
        cmrecord_mot="--"
        cmrecord_pit="--"
	release="--"
    }
}