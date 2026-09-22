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
                            
                            echo "Cleaning up previous database state..."
                            docker-compose --env-file "\${ENV_FILE_PATH}" down -v || true
                            sleep 2

                            # Check if DB container already exists and is running
                            RUNNING=\$(docker-compose --env-file "\${ENV_FILE_PATH}" ps db 2>/dev/null | grep -q "Up" && echo "true" || echo "false")
                            
                            POSTGRES_DB=\$(grep "^POSTGRES_DB=" "\${ENV_FILE_PATH}" | cut -d'=' -f2 | tr -d '\r' | xargs)
                            POSTGRES_PASSWORD=\$(grep "^POSTGRES_PASSWORD=" "\${ENV_FILE_PATH}" | cut -d'=' -f2 | tr -d '\r' | xargs)
                            POSTGRES_PORT=\$(grep "^POSTGRES_PORT=" "\${ENV_FILE_PATH}" | cut -d'=' -f2 | tr -d '\r' | xargs || echo "5432")
                            
                            if [ "\$RUNNING" = "false" ]; then
                                echo "Database container not running, initializing from scratch..."
                                docker-compose --env-file "\${ENV_FILE_PATH}" down db --remove-orphans || true
                                
                                # Remove all postgres/db containers across all projects
                                docker ps -a --filter "ancestor=postgres" --format "{{.ID}}" | xargs -r docker rm -f 2>/dev/null || true

                                # Remove containers with "db" in the name
                                docker ps -a --filter "name=db" --format "{{.ID}}" | xargs -r docker rm -f 2>/dev/null || true

                                # Wait for OS to release the port
                                sleep 3
                                
                                docker-compose --env-file "\${ENV_FILE_PATH}" build --no-cache db
                                docker-compose --env-file "\${ENV_FILE_PATH}" up -d db
                                
                                sleep 2
                                docker-compose --env-file "\${ENV_FILE_PATH}" ps db
                                
                                echo "Waiting for PostgreSQL to be ready..."
                                for i in {1..30}; do
                                    if docker-compose --env-file "\${ENV_FILE_PATH}" exec -T db pg_isready -U postgres > /dev/null 2>&1; then
                                        echo "PostgreSQL is ready!"
                                        break
                                    fi
                                    echo "Attempt \$i/30: Waiting for database..."
                                    sleep 2
                                done
                                
                                echo "Initializing database schema and data..."
                                docker-compose --env-file "\${ENV_FILE_PATH}" exec -T \
                                    -e PGPASSWORD="\${POSTGRES_PASSWORD}" \
                                    db sh -c "cd /docker-entrypoint-initdb.d && psql -v ON_ERROR_STOP=1 -U postgres -d \"\${POSTGRES_DB}\" -f init-db.sql && psql -v ON_ERROR_STOP=1 -U postgres -d \"\${POSTGRES_DB}\" -f update-data.sql"
                                
                                echo "Database initialization completed"
                            else
                                echo "Database already running, checking if data exists..."
                                docker-compose --env-file "\${ENV_FILE_PATH}" ps db
                                
                                # Check if instruments table has data (as a proxy for overall population)
                                DATA_COUNT=\$(docker-compose --env-file "\${ENV_FILE_PATH}" exec -T \
                                    -e PGPASSWORD="\${POSTGRES_PASSWORD}" \
                                    db psql -U postgres -d "\${POSTGRES_DB}" -t -c "SELECT COUNT(*) FROM instruments;" 2>/dev/null || echo "0")
                                
                                if [ "\$DATA_COUNT" -eq 0 ]; then
                                    echo "Database exists but is unpopulated, loading seed data..."
                                    docker-compose --env-file "\${ENV_FILE_PATH}" exec -T \
                                        -e PGPASSWORD="\${POSTGRES_PASSWORD}" \
                                        db sh -c "cd /docker-entrypoint-initdb.d && psql -v ON_ERROR_STOP=1 -U postgres -d \"\${POSTGRES_DB}\" -f update-data.sql"
                                    echo "Seed data loaded successfully"
                                else
                                    echo "Database already populated with data, skipping initialization"
                                fi
                            fi
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
                                db sh -c "cd /docker-entrypoint-initdb.d && psql -v ON_ERROR_STOP=1 -U postgres -d \"\${POSTGRES_DB}\" -f update-data.sql"
                            
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
            steps {
                sh '''
                    CONTAINER_ID=$(docker run -d the-commit-crew:${BUILD_NUMBER})
                    sleep 5
                    if docker exec $CONTAINER_ID curl -f http://localhost:8081/actuator/health; then
                        echo "Health check passed"
                    else
                        echo "Health check failed"
                        docker logs $CONTAINER_ID
                        docker rm -f $CONTAINER_ID
                        exit 1
                    fi
                    docker rm -f $CONTAINER_ID
                '''
            }
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