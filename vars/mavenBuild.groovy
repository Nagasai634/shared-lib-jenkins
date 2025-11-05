// vars/mavenBuild.groovy
// Usage: mavenBuild(pom: 'pom.xml', goals: 'clean package -DskipTests=true')
def call(Map params = [:]) {
    String pom = params.get('pom', 'pom.xml')
    String goals = params.get('goals', 'clean package -DskipTests=true')
    String mavenToolName = params.get('maven', 'Maven 3.8.7') // adjust to your configured tool name

    echo "Running Maven build: pom=${pom}, goals='${goals}'"

    // If you have configured Maven in Jenkins Global Tools as 'Maven 3.6.3' change above
    if (isUnix()) {
        withEnv(["MAVEN_OPTS=-Xms256m -Xmx1024m"]) {
            withMaven(maven: mavenToolName) {
                sh "mvn -f ${pom} ${goals}"
            }
        }
    } else {
        error "Non-unix agents are not supported in this simple example."
    }
}
