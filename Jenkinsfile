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
                    // Determine environment based on branch
                    def environment = 'dev'
                    if (env.GIT_BRANCH == 'main' || env.GIT_BRANCH == 'origin/main') {
                        environment = 'prod'
                    }
                    echo "Starting ${environment} database environment..."
            
                    // Use Jenkins Credentials to access .env files
                    def credentialsId = (environment == 'prod') ? 'env-prod-file' : 'env-dev-file'
            
                    withCredentials([file(credentialsId: credentialsId, variable: 'ENV_FILE_PATH')]) {
                        sh """
                            set -e
                    
                            # Copy the secret .env file from Jenkins credentials
                            cp "\${ENV_FILE_PATH}" ".env.${environment}"
                            echo "Loaded environment: .env.${environment} from Jenkins credentials"
                    
                            # Start database container with environment-specific configuration
                            docker-compose --env-file ".env.${environment}" up -d db
                    
                            # Wait for PostgreSQL to be ready (max 60 seconds)
                            echo "Waiting for PostgreSQL to be ready..."
                            for i in {1..12}; do
                                if docker-compose --env-file ".env.${environment}" exec -T db pg_isready -U postgres > /dev/null 2>&1; then
                                    echo "PostgreSQL is ready!"
                                    break
                                fi
                                echo "Attempt \$i/12: Waiting for database..."
                                sleep 5
                            done
                    
                            # Verify the connection works
                            docker-compose --env-file ".env.${environment}" exec -T db psql -U postgres -c "SELECT version();" | head -1
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
                // Only run if database-related files changed
                changeset glob: "db/**"
            }
            steps {
                script {
                    // Determine environment based on branch
                    def environment = 'dev'
                    if (env.GIT_BRANCH == 'main' || env.GIT_BRANCH == 'origin/main') {
                        environment = 'prod'
                    }
                    echo "Updating database data for ${environment} environment..."
                    
                    sh """
                        set -e
                        
                        ENV_FILE=".env.${environment}"
                        
                        # Check if update-data.sql exists
                        if [ ! -f "db/update-data.sql" ]; then
                            echo "WARN: db/update-data.sql not found, skipping data update"
                            exit 0
                        fi
                        
                        echo "Running database update script..."
                        
                        # Read POSTGRES_DB and POSTGRES_PASSWORD from .env file
                        POSTGRES_DB=\$(grep "^POSTGRES_DB=" "\${ENV_FILE}" | cut -d'=' -f2)
                        POSTGRES_PASSWORD=\$(grep "^POSTGRES_PASSWORD=" "\${ENV_FILE}" | cut -d'=' -f2)
                        
                        # Run the update script
                        docker-compose exec -T \
                            -e PGPASSWORD="\${POSTGRES_PASSWORD}" \
                            db psql -U postgres -d "\${POSTGRES_DB}" -f /docker-entrypoint-initdb.d/db/update-data.sql
                        
                        echo "Database update completed successfully"
                    """
                }
            }
            post {
                failure {
                    sh 'docker-compose logs db || true'
                }
            }
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