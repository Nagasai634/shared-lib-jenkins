def call(String imageName, String containerName, int hostPort, int containerPort) {
    stage('Docker Run') {
        echo "Running container '${containerName}' from image '${imageName}' ..."
        sh """
           # Run new container
            echo "Starting new container..."
            docker run -d --name ${containerName} -p ${hostPort}:${containerPort} ${imageName}
        """
        echo "Container '${containerName}' is running on port ${hostPort}"
    }
}
