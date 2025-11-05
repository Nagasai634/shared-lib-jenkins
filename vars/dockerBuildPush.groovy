// vars/dockerBuildPush.groovy
// Usage: dockerBuildPush(image: 'myorg/myapp:1.0.0', registry: 'docker.io', credId: 'docker-hub-cred', dockerfile: 'Dockerfile', buildDir: 'service')
def call(Map params = [:]) {
    String image = params.get('image')
    if (!image) {
        error "dockerBuildPush: 'image' parameter is required (e.g. myorg/myapp:1.0.0)"
    }
    String registry = params.get('registry', '')
    String credId = params.get('credId', '')
    String dockerfile = params.get('dockerfile', 'Dockerfile')
    String buildArgs = params.get('buildArgs', '')
    String buildDir = params.get('buildDir', null)

    echo "dockerBuildPush: image=${image}, dockerfile=${dockerfile}, buildDir='${buildDir ?: '.'}'"

    def runDocker = {
        // enable BuildKit if available
        withEnv(["DOCKER_BUILDKIT=1"]) {
            // build context is the current dir ('.') so run inside the desired dir
            sh "docker build -f ${dockerfile} -t ${image} ${buildArgs} ."

            if (registry && credId) {
                withCredentials([usernamePassword(credentialsId: credId, usernameVariable: 'DOCKER_USER', passwordVariable: 'DOCKER_PASS')]) {
                    sh "echo \"${DOCKER_PASS}\" | docker login ${registry} --username ${DOCKER_USER} --password-stdin"
                    sh "docker push ${image}"
                    sh "docker logout ${registry}"
                }
            } else {
                // try pushing without credentials (may fail on protected registries)
                sh "docker push ${image} || true"
            }
        }
    }

    if (buildDir) {
        dir(buildDir) {
            runDocker()
        }
    } else {
        runDocker()
    }
}
