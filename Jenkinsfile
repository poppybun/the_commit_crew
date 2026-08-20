pipeline {
    agent any
    stages {
        stage('Setup') {
            tools {
                jdk "JDK21"
                maven "Maven3"
            }
        }
        stage('Checkout') { 
            steps { 
                checkout scm 
            } 
        }
        stage('Build') {
            steps {
                sh 'mvn -B clean package'
            }
        }
        stage('Build Image') {
            steps { sh 'docker build -t the-commit-crew .' }
        }
        stage('Smoke Test') {
            steps { sh 'docker run --rm the-commit-crew' }
        }
        stage('Archive') {
            steps {
                archiveArtifacts artifacts: 'target/*.jar', fingerprint: true
            }
        }
    }
}