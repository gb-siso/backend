pipeline {
    agent any

    environment {
        DEPLOY_USER = 'jidamine87593'
        DEPLOY_HOST = '34.64.182.4'
        DEPLOY_DIR = '/home/jidamine87593/app'
        JAR_NAME = 'siso-0.0.1-SNAPSHOT.jar'
    }

    stages {
        stage('Checkout') {
            steps {
                checkout scm
            }
        }

        stage('Build') {
            steps {
                sh './gradlew clean build -x test'
            }
        }

        stage('Deploy (only on main)') {
            when {
                branch 'main' // ✅ main 브랜치일 때만 실행
            }
            steps {
                sshagent(['gcp-ssh-key-id']) {
                    sh """
                        scp build/libs/${JAR_NAME} ${DEPLOY_USER}@${DEPLOY_HOST}:${DEPLOY_DIR}/
                        ssh ${DEPLOY_USER}@${DEPLOY_HOST} '
                            pkill -f ${JAR_NAME} || true
                            nohup java -jar ${DEPLOY_DIR}/${JAR_NAME} > ${DEPLOY_DIR}/app.log 2>&1 &
                        '
                    """
                }
            }
        }
    }
}