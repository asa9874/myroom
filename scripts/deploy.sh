#!/bin/bash
#===============================================================================
# EC2 서버 배포 스크립트
# 사용법: ./deploy.sh [tag]
#===============================================================================

set -e

# 색상 정의
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m'

# 설정
SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
DEPLOY_DIR="${SCRIPT_DIR}"
TAG="${1:-latest}"

echo -e "${BLUE}========================================${NC}"
echo -e "${BLUE}  MyRoom EC2 Deployment Script${NC}"
echo -e "${BLUE}========================================${NC}"
echo ""

cd "$DEPLOY_DIR" 

# 환경 변수 파일 확인
if [ ! -f ".env" ]; then
    echo -e "${RED}오류: .env 파일이 없습니다.${NC}"
    echo -e "${YELLOW}.env.example을 복사하여 .env 파일을 생성하세요:${NC}"
    echo -e "  cp .env.example .env"
    echo -e "  nano .env"
    exit 1
fi

# 환경 변수 로드
set -a
source .env
set +a
export IMAGE_TAG="$TAG"

echo -e "${YELLOW}[1/5] 환경 변수 확인...${NC}"
echo -e "  Docker Hub: ${DOCKERHUB_USERNAME}/${IMAGE_NAME}"
echo -e "  Tag: ${IMAGE_TAG}"
echo ""

# 2. Docker Hub에서 최신 이미지 풀
echo -e "${YELLOW}[2/5] 최신 이미지 다운로드 중...${NC}"
docker pull "${DOCKERHUB_USERNAME}/${IMAGE_NAME}:${IMAGE_TAG}"
echo -e "${GREEN}✓ 이미지 다운로드 완료${NC}"
echo ""

# 3. 기존 컨테이너 중지
echo -e "${YELLOW}[3/5] 기존 컨테이너 중지 중...${NC}"
docker compose -f docker-compose.prod.yml down --remove-orphans || true
echo -e "${GREEN}✓ 기존 컨테이너 중지 완료${NC}"
echo ""

# 4. 새 컨테이너 시작
echo -e "${YELLOW}[4/5] 새 컨테이너 시작 중...${NC}"
docker compose -f docker-compose.prod.yml up -d
echo -e "${GREEN}✓ 새 컨테이너 시작 완료${NC}"
echo ""

# 5. 상태 확인
echo -e "${YELLOW}[5/5] 컨테이너 상태 확인...${NC}"
sleep 5
docker compose -f docker-compose.prod.yml ps
echo ""

# 헬스 체크
echo -e "${YELLOW}애플리케이션 헬스 체크 중...${NC}"
MAX_RETRIES=30
RETRY_COUNT=0

while [ $RETRY_COUNT -lt $MAX_RETRIES ]; do
    HTTP_CODE=$(curl -s -o /dev/null -w "%{http_code}" http://localhost/actuator/health 2>/dev/null || echo "000")
    if [ "$HTTP_CODE" = "200" ]; then
        echo -e "${GREEN}✓ 애플리케이션이 정상적으로 시작되었습니다!${NC}"
        break
    fi
    RETRY_COUNT=$((RETRY_COUNT + 1))
    echo -e "  대기 중... ($RETRY_COUNT/$MAX_RETRIES) - HTTP: $HTTP_CODE"
    sleep 2
done

if [ $RETRY_COUNT -eq $MAX_RETRIES ]; then
    echo -e "${YELLOW}⚠ 헬스 체크 타임아웃 - 로그를 확인하세요:${NC}"
    echo -e "  docker compose -f docker-compose.prod.yml logs app"
fi

echo ""
echo -e "${GREEN}========================================${NC}"
echo -e "${GREEN}배포가 완료되었습니다!${NC}"
echo -e "${GREEN}========================================${NC}"
echo ""
echo -e "로그 확인: ${BLUE}docker compose -f docker-compose.prod.yml logs -f app${NC}"
echo -e "컨테이너 상태: ${BLUE}docker compose -f docker-compose.prod.yml ps${NC}"
