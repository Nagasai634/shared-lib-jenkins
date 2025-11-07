// vars/deployApp.groovy
def call(Map params = [:]) {
    String imageName     = params.get('imageName', 'java-app:latest')
    String containerName = params.get('containerName', 'java-app-container')
    int    hostPort      = (params.get('hostPort') ?: 8080) as int
    int    containerPort = (params.get('containerPort') ?: 8080) as int

    node {
        try {
            stage('Maven Build') {
                echo "Starting Maven Build"
                sh '''
                    mvn clean package -DskipTests
                '''
                echo "Completion of Maven Build"
            }

            stage('Docker Build') {
                echo "Creating Dockerfile dynamically..."
                writeFile file: 'Dockerfile', text: '''
FROM eclipse-temurin:21-jdk-jammy
WORKDIR /app
COPY target/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
'''
                echo "Dockerfile created (contents):"
                sh 'cat Dockerfile'

                echo "Building Docker image: ${imageName}"
                sh """
                    docker build -t ${imageName} .
                """
                echo "Docker Image ${imageName} Built Successfully!"
            }

            stage('Docker Run') {
                echo "Running container '${containerName}' from image '${imageName}' ..."

                // NOTE: escape any literal shell $ as \$ so Groovy GString doesn't try to parse it
                sh """
                    # if an existing container with this name exists, remove it
                    if [ \$(docker ps -a -q -f name=${containerName} | wc -l) -gt 0 ]; then
                      echo "Removing existing container ${containerName}..."
                      docker rm -f ${containerName} || true
                    fi

                    echo "Starting new container ${containerName} on ${hostPort}:${containerPort}..."
                    docker run -d --name ${containerName} -p ${hostPort}:${containerPort} ${imageName}
                """

                echo "Container '${containerName}' is running on port ${hostPort}"
            }
        } catch (err) {
            echo "Build failed: ${err}"
            error "Pipeline failed: ${err}"
        }
    }
}




// def call(Map params = [:]) {
//     // Set default parameters
//     def config = [
//         imageName: params.imageName ?: "java-app:latest",
//         containerName: params.containerName ?: "java-app-container",
//         hostPort: params.hostPort ?: 8080,
//         containerPort: params.containerPort ?: 8080,
//         skipTests: params.skipTests != false
//     ]
    
//     // Execute all stages
//     mavenBuildStage(config.skipTests)
//     dockerBuildStage(config.imageName)
//     dockerRunStage(config.imageName, config.containerName, config.hostPort, config.containerPort)
// }

// def mavenBuildStage(Boolean skipTests = true) {
//     stage('Maven Build') {
//         echo "Starting Maven Build"
//         def testFlag = skipTests ? '-DskipTests' : ''
//         sh """
//             mvn clean package ${testFlag}
//         """
//         echo "Completion of Maven Build"
//     }
// }

// def dockerBuildStage(String imageName) {
//     stage('Docker Build') {
//         echo "Creating Dockerfile dynamically..."

//         writeFile file: 'Dockerfile', text: '''
//         FROM eclipse-temurin:21-jdk-jammy
//         WORKDIR /app
//         COPY target/*.jar app.jar
//         EXPOSE 8080
//         ENTRYPOINT ["java", "-jar", "app.jar"]
//         '''

//         echo "Dockerfile created:"
//         sh 'cat Dockerfile'

//         echo "Building Docker image: ${imageName}"
//         sh """
//             docker build -t ${imageName} .
//         """
//         echo "Docker Image ${imageName} Built Successfully!"
//     }
// }

// def dockerRunStage(String imageName, String containerName, int hostPort, int containerPort) {
//     stage('Docker Run') {
//         echo "Running container '${containerName}' from image '${imageName}' ..."
//         sh """
//             # Stop and remove existing container if it exists
//             docker stop ${containerName} || true
//             docker rm ${containerName} || true
            
//             # Run new container
//             echo "Starting new container..."
//             docker run -d --name ${containerName} -p ${hostPort}:${containerPort} ${imageName}
//         """
//         echo "Container '${containerName}' is running on port ${hostPort}"
//     }
// }

// // Individual call methods for backward compatibility
// def call() {
//     mavenBuildStage()
// }

// def call(String imageName) {
//     mavenBuildStage()
//     dockerBuildStage(imageName)
// }

// def call(String imageName, String containerName, int hostPort, int containerPort) {
//     mavenBuildStage()
//     dockerBuildStage(imageName)
//     dockerRunStage(imageName, containerName, hostPort, containerPort)
// }
