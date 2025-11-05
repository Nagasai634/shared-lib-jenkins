// vars/dockerBuildPush.groovy
// Usage: dockerBuildPush(image: 'myorg/myapp:1.0.0', registry: 'docker.io', credId: 'docker-hub-cred', dockerfile: 'Dockerfile')
def call(Map params = [:]) {
    String image = params.get('image')
    if (!image) {
        error "dockerBuildPush: 'image' parameter is required (e.g. myorg/myapp:1.0.0)"
    }
    String registry = params.get('registry', '')
    String credId = params.get('credId', '')
    String dockerfile = params.get('dockerfile', 'Dockerfile')
    String buildArgs = params.get('buildArgs', '')

    echo "Building docker image: ${image} (dockerfile=${dockerfile})"

    // If using Docker agent and docker CLI available
    withEnv(["DOCKER_BUILDKIT=1"]) {
        sh "docker build -f ${dockerfile} -t ${image} ${buildArgs} ."

        if (registry && credId) {
            // login using credentials binding
            withCredentials([usernamePassword(credentialsId: credId, usernameVariable: 'DOCKER_USER', passwordVariable: 'DOCKER_PASS')]) {
                sh "echo \"${DOCKER_PASS}\" | docker login ${registry} --username ${DOCKER_USER} --password-stdin"
                sh "docker push ${image}"
                sh "docker logout ${registry}"
            }
        } else {
            // if no credentials provided, just try push (likely to fail on protected registries)
            sh "docker push ${image} || true"
        }
    }
}
