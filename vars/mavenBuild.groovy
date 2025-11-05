// vars/mavenBuild.groovy
// Usage: mavenBuild(pom: 'pom.xml', goals: 'clean package -DskipTests=true', buildDir: 'service')
def call(Map params = [:]) {
    String pom = params.get('pom', 'pom.xml')
    String goals = params.get('goals', 'clean package -DskipTests=true')
<<<<<<< HEAD
    String mavenToolName = params.get('mavenTool', 'Maven 3.6.3') // adjust to your configured tool name
    String buildDir = params.get('buildDir', null) // optional
=======
    String mavenToolName = params.get('maven', 'Maven 3.8.7') // adjust to your configured tool name
>>>>>>> d0dd456ab9b68523838093a5ad653dd612df3796

    echo "mavenBuild: pom=${pom}, goals='${goals}', buildDir='${buildDir ?: '.'}'"

    def runBuild = {
        // If you prefer not to use withMaven, replace with a plain mvn call
        if (isUnix()) {
            withEnv(["MAVEN_OPTS=-Xms256m -Xmx1024m"]) {
                withMaven(maven: mavenToolName) {
                    sh "mvn -f ${pom} ${goals}"
                }
            }
        } else {
            error "Non-unix agents are not supported in this example."
        }
    }

    if (buildDir) {
        dir(buildDir) {
            runBuild()
        }
    } else {
        runBuild()
    }
}
