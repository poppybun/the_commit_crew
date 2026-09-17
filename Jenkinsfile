pipeline {
    agent any
    tools {
        jdk "JDK21"
        maven "Maven3"
    }
    stages {  
        stage('Checkout') { 
            steps { 
                checkout scm 
            } 
        }
        stage('Build') {
            steps {
                sh 'mvn -B clean package -f app/pom.xml'
            }
        }
        stage('Build Image') {
            steps { sh 'docker build -t the-commit-crew:${BUILD_NUMBER} .' }
        }
        stage('Smoke Test') {
            steps { sh 'docker run --rm the-commit-crew:${BUILD_NUMBER}' }
        }
        stage('Archive') {
            steps {
                archiveArtifacts artifacts: 'app/target/*.jar', fingerprint: true
            }
        }
        stage('Test') {
            steps { sh 'mvn -B test -f app/pom.xml' }
                post { always { junit 'app/target/surefire-reports/*.xml' } }
        }

    }
}