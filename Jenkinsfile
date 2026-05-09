pipeline {
    // This tells Jenkins it can run on any available server/node
    agent any

    environment {
        IMAGE_NAME = 'greenops-sentinel-app'
        CONTAINER_NAME = 'sentinel-backend'
    }

    stages {
        stage('Checkout Code') {
            steps {
                echo 'Fetching latest code from GitHub...'
                checkout scm
            }
        }

        stage('Compile & Build') {
            steps {
                echo 'Compiling the Spring Boot application...'
                // Make the wrapper executable just in case
                sh 'chmod +x ./mvnw'
                // Package the .jar file
                sh './mvnw clean package -DskipTests'
            }
        }

        stage('Build Docker Image') {
            steps {
                echo 'Building the new Docker image...'
                sh "docker build -t ${IMAGE_NAME} ."
            }
        }

        stage('Deploy to Production') {
            steps {
                echo 'Deploying the new container...'

                // 1. Stop and remove the old container (if it exists)
                // The '|| true' means "don't crash if the container isn't running"
                sh "docker rm -f ${CONTAINER_NAME} || true"

                // 2. Run the new container securely
                // CRITICAL: We use Jenkins Credentials here so keys aren't in GitHub!
                withCredentials([
                    string(credentialsId: 'gemini-api-key', variable: 'GEMINI_KEY'),
                    string(credentialsId: 'aws-access-key', variable: 'AWS_ACCESS'),
                    string(credentialsId: 'aws-secret-key', variable: 'AWS_SECRET')
                ]) {
                    sh """
                    docker run -d --name ${CONTAINER_NAME} -p 8080:8080 \
                      -e SPRING_DATASOURCE_URL=jdbc:postgresql://host.docker.internal:5433/postgres \
                      -e SPRING_AI_GOOGLE_GENAI_API_KEY=${GEMINI_KEY} \
                      -e AWS_ACCESS_KEY_ID=${AWS_ACCESS} \
                      -e AWS_SECRET_ACCESS_KEY=${AWS_SECRET} \
                      -e AWS_REGION=ap-south-1 \
                      ${IMAGE_NAME}
                    """
                }
            }
        }
    }

    post {
        success {
            echo '✅ Pipeline succeeded! GreenOps Sentinel is live.'
        }
        failure {
            echo '❌ Pipeline failed! Check the logs.'
        }
    }
}