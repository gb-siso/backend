pipeline {
    agent any

    stages {
        stage('Checkout') {
            steps {
                checkout scm
            }
        }

        stage('Build') {
            steps {
        sh '''
            chmod +x ./gradlew
            ./gradlew clean build -x test
        '''
            }
        }

        stage('Deploy') {
            steps{
                sh '''
                cd /home/jidamine87593/shell
                sh stop.sh
                sh start.sh
                '''
            }
        }
    }
}