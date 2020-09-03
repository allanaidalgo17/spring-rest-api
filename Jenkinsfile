#!groovy

envSonar = "my sonarqube server"
slack_channel = "my_chanel"

pipeline {
    
    agent any
    tools {
        maven 'maven-4.0.0'
        jdk 'java11'
    }

    stages {
        
        stage('Initialize') {
            steps {
                checkout scm
                script {
                    echo "PATH = ${env.PATH}"
                    echo "BRANCH = ${env.BRANCH}"
                    echo "REVISION = ${env.COMMIT}"
                    echo "WORKSPACE = ${env.WORKSPACE}"
                }
            }
        }

        stage('Clean') {
            steps {
                sh 'make clean'
            }
        }

        stage('Build') {
            steps {
                sh 'make build'
            }
        }

        stage('Test') {
            steps {
                script {
                    sh 'make test'
                    junit allowEmptyResults: true, keepLongStdio: true, testResults: 'target/surefire-reports/*/.xml'
                }
            }
        }

        stage('SonarQube') {
            steps {
                echo "Publishing to SonarQube..."
                withSonarQubeEnv(envSonar) {
                    script {
                        try {
                            sh 'mvn sonar:sonar'
                        } catch(exc) {
                            error "Quality Gate fail"
                        }
                    }
                }
            }
        }

        stage('Quality Gate') {
            steps {
                echo "Running Quality Gate..."
                script {
                    timeout(time: 3, unit: 'MINUTES') {
                        def qg = waitForQualityGate()
                        if(qg.status == 'OK') {
                            currentBuild.result = 'SUCCESS'
                        } else {
                            currentBuild.result = 'UNSTABLE'
                            error "Pipeline aborted due to Quality Gate failure: ${qg.status}"
                        }
                    }
                }
                echo "Finished passing through Quality Gate"
            }
        }

        stage('Upgrade Version') {
            when {
                branch 'master'
            }
            steps {
                script {
                    echo "Upgrading project version..."
                    upgradeVersion()
                }
            }
        }

        stage('Docker Build') {
            when {
                anyOf {
                    branch 'develop'; branch 'master'
                }
            }
            steps {
                sh 'make build_docker'
            }
        }

        stage('Docker Push') {
            when {
                anyOf {
                    branch 'develop'; branch 'master'
                }
            }
            steps {
                withCredentials([usernamePassword(credentialsId: 'myDockerLogin', usernameVariable: 'USERNAME', passwordVariable: 'PASSWORD')]) {
                    sh 'make push_docker USER=$USERNAME PASS=$PASSWORD'
                }
            }
        }

        // if there is a branch other than master \/
        stage('Deploy QA') {
            when {
                branch 'develop'
            }
            steps {
                script {
                    pom = readMavenPom file: 'pom.xml'
                    sh 'sed -i '/image/s/\$/:${pom.version}/' deploy/deploymentQA.yml'
                    sh 'kubectl create -f deploy/deploymentQA.yml'
                }
            }
        }
        // if there is a branch other than master /\

        stage('Deploy PROD') {
            when {
                branch 'master'
            }
            steps {
                script {
                    pom = readMavenPom file: 'pom.xml'
                    sh 'sed -i '/image/s/\$/:${pom.version}/' deploy/deploymentPROD.yml'
                    sh 'kubectl create -f deploy/deploymentPROD.yml'
                }
            }
        }
    }

    post {
        always {
            echo "Pipeline completed"
            script {
                if(currentBuild.result == 'SUCCESS') {
                    sendMessage("Process completed with SUCCESS =D")
                } else {
                    sendMessage("Process completed with ERROR =(")
                }
            }
        }
    }
}

def sendMessage(String buildMessage) {
    slackSend channel: ${slack_channel}, message: "JOB ${env.JOB_NAME} - [${env.BRANCH_NAME}](<${env.BUILD_URL}>): $buildMessage"
}

def upgradeVersion() {
    sh 'mvn --batch-mode release:update-version'
    sh 'git commit -am \"New Version\"'

    withCredentials([usernamePassword(credentialsId: 'myGitLogin', usernameVariable: 'GIT_USERNAME', passwordVariable: 'GIT_PASSWORD')]) {
        sh 'git push https://${GIT_USERNAME}:${GIT_PASSWORD}@github.com/allanaidalgo17/spring-rest-api master'

        // if there is a branch other than master \/
        sh 'git checkout --orphan develop'
        sh 'git reset --hard'
        sh 'git pull https://${GIT_USERNAME}:${GIT_PASSWORD}@github.com/allanaidalgo17/spring-rest-api develop'
        sh 'git merge master'
        sh 'git push https://${GIT_USERNAME}:${GIT_PASSWORD}@github.com/allanaidalgo17/spring-rest-api develop:develop'
        sh 'git checkout master'
        sh 'git branch -D develop'
        // if there is a branch other than master /\

        sh 'mvn clean install -DskipTests'
    }
}