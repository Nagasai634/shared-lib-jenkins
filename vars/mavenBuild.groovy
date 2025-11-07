def call() {
    stage('Maven Build') {
        echo "starting Maven Build"
        sh '''
            mvn clean package -DskipTests 
        '''
        echo "Completion of Maven Build"
    }
}