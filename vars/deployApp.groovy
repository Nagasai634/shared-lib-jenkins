// vars/deployApp.groovy
def call(Map params = [:]) {
  String imageName     = params.get('imageName', 'java-app:latest')
  String containerName = params.get('containerName', 'java-app-container')
  int    hostPort      = (params.get('hostPort') ?: 8080) as int
  int    containerPort = (params.get('containerPort') ?: 8080) as int

  try {
    stage('Maven Build') {
      echo "Starting Maven Build"
      // run inside the checked-out project workspace
      dir("${env.WORKSPACE}") {
        sh 'pwd; ls -la'
        sh 'mvn clean package -DskipTests'
      }
      echo "Completion of Maven Build"
    }

    stage('Docker Build') {
      echo "Creating Dockerfile dynamically..."
      writeFile file: 'Dockerfile', text: '''FROM eclipse-temurin:21-jdk-jammy
WORKDIR /app
COPY target/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
'''
      sh 'cat Dockerfile'
      echo "Building Docker image: ${imageName}"
      sh "docker build -t ${imageName} ."
      echo "Docker Image ${imageName} Built Successfully!"
    }

    stage('Docker Run') {
      echo "Running container '${containerName}' from image '${imageName}' ..."
      sh """
        if [ \$(docker ps -a -q -f name=${containerName} | wc -l) -gt 0 ]; then
          docker rm -f ${containerName} || true
        fi
        docker run -d --name ${containerName} -p ${hostPort}:${containerPort} ${imageName}
      """
      echo "Container '${containerName}' is running on port ${hostPort}"
    }

  } catch (err) {
    echo "Build failed: ${err}"
    error "Pipeline failed: ${err}"
  }
}



