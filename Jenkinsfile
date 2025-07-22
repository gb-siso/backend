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
                    echo ">>> [Build] Running as user: $(whoami)"
                    cd /home/jidamine87593/siso/backend
                    chmod +x ./gradlew
                    ./gradlew clean build -x test
                '''
            }
        }

stage('Deploy') {
    steps {
        sh '''
            echo ">>> [Deploy] Running as user: $(whoami)"
            cd /home/jidamine87593/shell
            sh stop_jenkins.sh
            sh start_jenkins.sh
        '''
    }
}

    }
}
