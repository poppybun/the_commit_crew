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
                sh 'mvn -B clean package'
            }
        }
        stage('Build Image') {
            steps { sh 'docker build -t the-commit-crew:${BUILD_NUMBER} .' }
        }
        stage('Start Database') {
            steps {
                script {
                    def environment = 'dev'
                    if (env.GIT_BRANCH == 'main' || env.GIT_BRANCH == 'origin/main') {
                        environment = 'prod'
                    }
                    echo "Starting ${environment} database environment..."
            
                    def credentialsId = (environment == 'prod') ? 'env-prod-file' : 'env-dev-file'
            
                    withCredentials([file(credentialsId: credentialsId, variable: 'ENV_FILE_PATH')]) {
                        sh """
                            set -e
                            
                            docker-compose --env-file "\${ENV_FILE_PATH}" down db || true
                            docker-compose --env-file "\${ENV_FILE_PATH}" up -d db
                            echo "Docker compose up command completed..."
                            
                            sleep 2
                            docker-compose --env-file "\${ENV_FILE_PATH}" ps db
                            docker-compose --env-file "\${ENV_FILE_PATH}" logs db || true
                            
                            echo "Waiting for PostgreSQL to be ready..."
                            for i in {1..12}; do
                                if docker-compose --env-file "\${ENV_FILE_PATH}" exec -T db pg_isready -U postgres > /dev/null 2>&1; then
                                    echo "PostgreSQL is ready!"
                                    break
                                fi
                                echo "Attempt \$i/12: Waiting for database..."
                                sleep 5
                            done
                            
                            # Initialize database schema, indexes, and seed data
                            echo "Initializing database..."
                            POSTGRES_DB=\$(grep "^POSTGRES_DB=" "\${ENV_FILE_PATH}" | cut -d'=' -f2 | tr -d '\r' | xargs)
                            POSTGRES_PASSWORD=\$(grep "^POSTGRES_PASSWORD=" "\${ENV_FILE_PATH}" | cut -d'=' -f2 | tr -d '\r' | xargs)

                            docker-compose --env-file "\${ENV_FILE_PATH}" exec -T \
                                -e PGPASSWORD="\${POSTGRES_PASSWORD}" \
                                -w /docker-entrypoint-initdb.d \
                                db psql -v ON_ERROR_STOP=1 -U postgres -d "\${POSTGRES_DB}" \
                                -f init-db.sql
                            echo "Database initialization completed"
                        """
                    }
                }
            }
            post {
                failure {
                    sh 'docker-compose logs db 2>/dev/null || true'
                }
            }
        }
        stage('Update Database') {
            when {
                changeset pattern: "db/**"
            }
            steps {
                script {
                    def environment = 'dev'
                    if (env.GIT_BRANCH == 'main' || env.GIT_BRANCH == 'origin/main') {
                        environment = 'prod'
                    }
                    echo "Updating database data for ${environment} environment..."
                    
                    def credentialsId = (environment == 'prod') ? 'env-prod-file' : 'env-dev-file'
                    
                    withCredentials([file(credentialsId: credentialsId, variable: 'ENV_FILE_PATH')]) {
                        sh """
                            set -e
                            
                            if [ ! -f "db/update-data.sql" ]; then
                                echo "WARN: db/update-data.sql not found, skipping data update"
                                exit 0
                            fi
                            
                            echo "Running database update script..."
                            
                            POSTGRES_DB=\$(grep "^POSTGRES_DB=" "\${ENV_FILE_PATH}" | cut -d'=' -f2 | tr -d '\r' | xargs)
                            POSTGRES_PASSWORD=\$(grep "^POSTGRES_PASSWORD=" "\${ENV_FILE_PATH}" | cut -d'=' -f2 | tr -d '\r' | xargs)

                            # Set ON_ERROR_STOP to exit on first error
                            docker-compose --env-file "\${ENV_FILE_PATH}" exec -T \
                                -e PGPASSWORD="\${POSTGRES_PASSWORD}" \
                                -w /docker-entrypoint-initdb.d \
                                db psql -v ON_ERROR_STOP=1 -U postgres -d "\${POSTGRES_DB}" \
                                -f update-data.sql
                            
                            echo "Database update completed successfully"
                        """
                    }
                }
            }
            post {
                failure {
                    sh 'docker-compose logs db 2>/dev/null || true'
                }
            }
        }
        stage('Smoke Test') {
            steps { sh 'docker run --rm the-commit-crew:${BUILD_NUMBER}' }
        }
        stage('Archive') {
            steps {
                archiveArtifacts artifacts: 'target/*.jar', fingerprint: true
            }
        }
        stage('Test') {
            steps { sh 'mvn -B test' }
                post { always { junit 'target/surefire-reports/*.xml' } }
        }

    }
}