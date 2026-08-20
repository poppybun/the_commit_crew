pipeline {
    agent any
    stages {
        stage('Checkout') { steps { checkout scm } }
        stage('Build Image') {
            steps { sh 'docker build -t the-commit-crew .' }
        }
        stage('Smoke Test') {
            steps { sh 'docker run --rm the-commit-crew' }
        }
    }
}