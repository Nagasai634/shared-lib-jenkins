def call(String imageName = "java-app:latest") {
    stage('Docker Build') {
        echo "Creating Dockerfile dynamically..."

        writeFile file: 'Dockerfile', text: '''
        FROM eclipse-temurin:21-jdk-jammy
        WORKDIR /app
        COPY target/*.jar app.jar
        EXPOSE 8080
        ENTRYPOINT ["java", "-jar", "app.jar"]
        '''

        echo "Dockerfile created:"
        sh 'cat Dockerfile'

        echo "Building Docker image: ${imageName}"
        sh """
            docker build -t ${imageName} .
        """
        echo "Docker Image ${imageName} Built Successfully!"
    }
}