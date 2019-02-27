pipeline {
	agent any
	environment { 
        WAR_NAME = 'stopwatcher.war'
        WAR_PATH = 'build/libs'
        BRANCH_TO_DEPLOY = 'master'

    }
	options {
		gitLabConnection('njros1lz539')
	}
	triggers {
		gitlab(
			triggerOnPush: true,
			triggerOnMergeRequest: false,
			triggerOnNoteRequest: false,
			noteRegex: ".*[Jj]enkins rebuild.*",
			setBuildDescription: true,
			addNoteOnMergeRequest: false,
			addCiMessage: true,
			branchFilterType: 'All'
		)
	}
	stages {
		stage("build") {
			steps {
				script {
					echo "-----------------------------------------------------------------------------------------------"
					echo "Build WAR"
					sh "df -h"
					sh "bash ./gradlew --version"
					sh "bash ./gradlew clean build -x check"
				}
			}
		}
		stage("SCA") {
            steps {
                script {
                    echo "-----------------------------------------------------------------------------------------------"
                    sh "bash ./gradlew checkstyleMain checkstyleTest pmdMain pmdTest spotbugsMain"
                }
            }
        }
        stage("Unit tests") {
            steps {
                script {
                    echo "-----------------------------------------------------------------------------------------------"
                    sh "bash ./gradlew test"
                }
            }
        }
	}
	post {
		failure {
			updateGitlabCommitStatus name: 'build', state: 'failed'
		}
		success {
			updateGitlabCommitStatus name: 'build', state: 'success'
			echo "Publishing artifacts of $WAR_PATH/* - WAR and configs"
            archiveArtifacts artifacts: "$WAR_PATH/$WAR_NAME", fingerprint: true

		}
		always {
			junit 'build/test-results/test/*.xml'
		}
	}
}
