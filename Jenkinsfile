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
                sh '''
                    chmod +x ./gradlew
                    ./gradlew clean build -x test
                '''
            }
        }

        stage('Debug Branch') {
            steps {
                echo "env.BRANCH_NAME = ${env.BRANCH_NAME}"
                echo "env.GIT_BRANCH  = ${env.GIT_BRANCH}"
            }
        }

        // 조건 분기를 넣고 싶다면 아래와 같이 when 블록 사용
        // stage('Deploy') {
        //     when {
        //         branch 'main'
        //     }

        stage('Deploy') {
            steps {
                sshagent(['gcp-ssh-key-id']) {
                    sh """
                        echo '[1] 최신 JAR 찾기'
                        JAR_FILE=\$(find build/libs -name "*.jar" | sort | tail -n 1)
                        if [ -z "\$JAR_FILE" ]; then
                            echo "❌ JAR 파일이 존재하지 않습니다."
                            exit 1
                        fi

                        echo "[2] JAR 파일 전송: \$JAR_FILE"
                        scp -o StrictHostKeyChecking=no "\$JAR_FILE" ${DEPLOY_USER}@${DEPLOY_HOST}:${DEPLOY_DIR}/app.jar.new

                        echo "[3] 원격 서버에서 배포 진행"
                        ssh -o StrictHostKeyChecking=no ${DEPLOY_USER}@${DEPLOY_HOST} '
                            echo "→ 기존 프로세스 종료"
                            PID=\$(pgrep -u \$USER -f "app.jar" || true)
                            if [ ! -z "\$PID" ]; then
                                kill -9 \$PID
                                echo "✔️ 프로세스 종료: \$PID"
                            else
                                echo "ℹ️ 종료할 기존 프로세스 없음"
                            fi

                            echo "→ 로그 디렉토리 생성"
                            mkdir -p ${DEPLOY_DIR}/logs

                            echo "→ 새 JAR 적용"
                            mv ${DEPLOY_DIR}/app.jar.new ${DEPLOY_DIR}/app.jar

                            echo "→ 애플리케이션 기동"
                            nohup java -Duser.timezone=Asia/Seoul -Dspring.profiles.active=prod \\
                                -jar ${DEPLOY_DIR}/app.jar > ${DEPLOY_DIR}/logs/console.log 2>&1 &

                            echo "→ 로그 대기"
                            timeout=10
                            while [ ! -f ${DEPLOY_DIR}/logs/console.log ] && [ \$timeout -gt 0 ]; do
                                sleep 1
                                timeout=\$((timeout - 1))
                            done

                            echo "→ 로그 출력"
                            if [ -f ${DEPLOY_DIR}/logs/console.log ]; then
                                tail -n 100 ${DEPLOY_DIR}/logs/console.log
                            else
                                echo "⚠️ 로그 파일이 일정 시간 내에 생성되지 않았습니다."
                            fi
                        '
                    """
                }
            }
        }
    }
}
