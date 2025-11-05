import com.nagasai.builds.Calculator

def call(Map pipelineParams){
   Calculator cal = new Calculator(this)



pipeline{
    agent any
    environment {
        APPLICATION_NAME = "${pipelineParams.appName}"
    }
    stages {

        stage ('calculate'){
            steps {
                script {
               echo "calling add method for reuasbility"
               println cal.add(14,18);
                }
            }
        }
       stage('Build'){
           steps {
            echo "*******building the application for ${env.APPLICATION_NAME}*********"
           }
       }

       stage ('Test'){
         steps {
            echo "***********application testing **************"
         }
       }

       stage ('devDeploy'){
        steps {
            echo "******deployong to dev env ******"
        }
       }

       stage ('testDeploy')
       {
        steps {
            echo "******deployong to test env ******"
        }
       }
    }
}
}



