def call() {
    stage('Maven Build') {
        echo "starting Maven Build"
        sh '''
            mvn clean package -DskipTests 
        '''
        echo "Completion of Maven Build"
    }
}



// def call(String projectDir = ".") {
//     stage('Maven Build') {
//         echo "Starting Maven Build in directory: ${projectDir}"
        
//         dir(projectDir) {
//             sh '''
//                 mvn clean package -DskipTests 
//             '''
//         }
//         echo "Completion of Maven Build"
//     }
// }