#!/bin/bash

# H2 데이터베이스 서버 시작 스크립트
# 사용법: ./start-h2-server.sh

echo "H2 데이터베이스 서버를 시작합니다..."

# data 디렉터리가 없으면 생성
if [ ! -d "data" ]; then
    mkdir -p data
    echo "data 디렉터리를 생성했습니다."
fi

# 이미 실행 중인 H2 서버가 있는지 확인
if [ -f "h2-server.pid" ]; then
    OLD_PID=$(cat h2-server.pid)
    if ps -p $OLD_PID > /dev/null 2>&1; then
        echo "H2 서버가 이미 실행 중입니다 (PID: $OLD_PID)"
        exit 0
    else
        echo "기존 PID 파일을 정리합니다."
        rm h2-server.pid
    fi
fi

# H2 JAR 파일 경로 찾기
H2_JAR=$(find ~/.gradle/caches -name "h2-*.jar" 2>/dev/null | head -1)

if [ -z "$H2_JAR" ]; then
    echo "H2 JAR 파일을 찾을 수 없습니다. Gradle 빌드를 먼저 실행해주세요."
    echo "./gradlew build"
    exit 1
fi

echo "H2 JAR: $H2_JAR"

# H2 서버 시작 (백그라운드 실행)
java -cp "$H2_JAR" org.h2.tools.Server \
    -tcp -tcpAllowOthers -tcpPort 9092 \
    -baseDir ./data \
    -ifNotExists &

# PID 저장
H2_PID=$!
echo $H2_PID > h2-server.pid

echo "H2 서버가 시작되었습니다."
echo "서버 포트: 9092"
echo "데이터 경로: ./data"
echo "PID: $H2_PID"
echo ""
echo "H2 Console 접속: http://localhost:8080/h2-console"
echo "JDBC URL: jdbc:h2:tcp://localhost:9092/./data/kmdb"
echo "Username: sa"
echo "Password: admin123"
echo ""
echo "서버 중지: ./stop-h2-server.sh"

# 서버가 정상적으로 시작되었는지 확인
sleep 2
if ps -p $H2_PID > /dev/null 2>&1; then
    echo "✅ H2 서버가 성공적으로 시작되었습니다."
else
    echo "❌ H2 서버 시작에 실패했습니다."
    rm -f h2-server.pid
    exit 1
fi