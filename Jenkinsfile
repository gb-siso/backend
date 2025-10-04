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
                    cp /home/jidamine87593/app/files/application-private.properties src/main/resources/
                    cp /home/jidamine87593/app/files/sisso-api.p12 src/main/resources/ssl/
                    echo ">>> [Build] Running as user: $(whoami)"
                    chmod +x ./gradlew
                    ./gradlew clean build -x test
                '''
            }
        }

        stage('Copy Built Jar') {
            steps {
                sh '''
                    echo ">>> [Copy] Copying built JAR to /home/jidamine87593/siso/backend/build/libs"
                    cp build/libs/siso-0.0.1-SNAPSHOT.jar /home/jidamine87593/app/jar/
                '''
            }
        }

        stage('Deploy') {
            steps {
                sh '''
                    echo ">>> [Deploy] Running as user: $(whoami)"
                    cd /home/jidamine87593/app/shell
                    sh stop_jenkins.sh
                    sh start_jenkins.sh
                '''
            }
        }
    }
}
