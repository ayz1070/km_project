#!/bin/bash

# H2 데이터베이스 서버 중지 스크립트
# 사용법: ./stop-h2-server.sh

echo "H2 데이터베이스 서버를 중지합니다..."

# PID 파일이 있는지 확인
if [ -f "h2-server.pid" ]; then
    H2_PID=$(cat h2-server.pid)
    
    # 프로세스가 실행 중인지 확인
    if ps -p $H2_PID > /dev/null 2>&1; then
        kill $H2_PID
        echo "H2 서버 (PID: $H2_PID)를 중지했습니다."
    else
        echo "H2 서버 프로세스를 찾을 수 없습니다."
    fi
    
    # PID 파일 삭제
    rm h2-server.pid
else
    echo "PID 파일을 찾을 수 없습니다."
    echo "수동으로 H2 서버 프로세스를 찾아 중지해주세요:"
    echo "ps aux | grep h2"
    echo "kill [PID]"
fi