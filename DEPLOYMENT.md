# MyRoom - Docker Hub Private Repository를 이용한 EC2 배포 가이드

## 📋 목차
1. [아키텍처 개요](#아키텍처-개요)
2. [사전 준비사항](#사전-준비사항)
3. [Docker Hub 설정](#docker-hub-설정)
4. [EC2 서버 초기 설정](#ec2-서버-초기-설정)
5. [로컬 PC 설정](#로컬-pc-설정)
6. [배포 프로세스](#배포-프로세스)
7. [스크립트 사용법](#스크립트-사용법)
8. [트러블슈팅](#트러블슈팅)

---

## 🏗️ 아키텍처 개요

```
┌─────────────────┐                    ┌──────────────────┐
│   로컬 PC       │      Push          │   Docker Hub     │
│                 │ ──────────────────▶│  (Private Repo)  │
│  - 소스 코드    │                    │                  │
│  - Docker       │                    └────────┬─────────┘
│  - Gradle       │                             │
└─────────────────┘                             │ Pull
                                                ▼
                                  ┌─────────────────────────────────┐
                                  │          EC2 서버               │
                                  │  ┌─────────────────────────┐    │
                                  │  │   Docker Compose        │    │
                                  │  │  ┌─────┐ ┌─────┐        │    │
                                  │  │  │Nginx│ │ App │        │    │
                                  │  │  └─────┘ └─────┘        │    │
                                  │  │  ┌─────┐ ┌────────┐     │    │
                                  │  │  │MySQL│ │RabbitMQ│     │    │
                                  │  │  └─────┘ └────────┘     │    │
                                  │  └─────────────────────────┘    │
                                  └─────────────────────────────────┘
```

---

## ✅ 사전 준비사항

### 로컬 PC (Windows)
- [x] Docker Desktop 설치
- [x] Git 설치
- [x] JDK 17 설치
- [x] Docker Hub 계정

### EC2 서버 (Ubuntu 22.04 권장)
- [x] t2.micro 인스턴스 (1 vCPU, 1GB RAM) - 프리티어 가능
- [x] 20GB EBS 볼륨 (gp3 권장)
- [x] **Swap 메모리 2GB 설정 필수** (아래 참고)
- [x] 보안그룹 설정:
  | 포트 | 용도 | 소스 |
  |------|------|------|
  | 22 | SSH | 내 IP |
  | 80 | HTTP | 0.0.0.0/0 |
  | 443 | HTTPS | 0.0.0.0/0 |
  | 15672 | RabbitMQ 관리 | 내 IP (선택사항) |

---

## 🐳 Docker Hub 설정

### 1. Docker Hub 계정 생성
1. [https://hub.docker.com](https://hub.docker.com) 접속
2. 회원가입 (무료 계정으로 Private Repository 1개 제공)

### 2. Private Repository 생성
1. Docker Hub 로그인
2. **Repositories** > **Create Repository** 클릭
3. 설정:
   - **Name**: `myroom-app`
   - **Visibility**: `Private` 선택
4. **Create** 클릭

### 3. Access Token 생성 (권장)
비밀번호 대신 Access Token 사용을 권장합니다:

1. Docker Hub > **Account Settings** > **Security**
2. **New Access Token** 클릭
3. 설명 입력 (예: "EC2 Deploy")
4. **Generate** 클릭
5. ⚠️ 토큰을 안전한 곳에 저장 (다시 볼 수 없음!)

---

## 🖥️ EC2 서버 초기 설정

### 1. EC2 인스턴스 접속
```bash
ssh -i your-key.pem ubuntu@your-ec2-ip
```

### 2. 시스템 업데이트
```bash
sudo apt-get update && sudo apt-get upgrade -y
```

### 3. Swap 메모리 설정 (t2.micro 필수)
t2.micro는 RAM이 1GB뿐이라 Swap 없이는 메모리 부족으로 앱이 죽을 수 있습니다.

```bash
# 2GB Swap 파일 생성
sudo fallocate -l 2G /swapfile
sudo chmod 600 /swapfile
sudo mkswap /swapfile
sudo swapon /swapfile

# 재부팅 후에도 유지되도록 설정
echo '/swapfile none swap sw 0 0' | sudo tee -a /etc/fstab

# Swap 확인
free -h
```

### 4. Docker 설치
```bash
# Docker 설치
curl -fsSL https://get.docker.com -o get-docker.sh
sudo sh get-docker.sh

# 현재 사용자를 docker 그룹에 추가
sudo usermod -aG docker $USER

# 변경사항 적용을 위해 재로그인
exit
```

### 5. 재접속 후 Docker Hub 로그인
```bash
ssh -i your-key.pem ubuntu@your-ec2-ip

# Docker 확인
docker --version
docker compose version

# Docker Hub 로그인
docker login
# Username: your-dockerhub-username
# Password: your-access-token (또는 비밀번호)
```

> 💡 **팁**: 로그인 정보는 `~/.docker/config.json`에 저장되어 재로그인 불필요

### 6. 배포 파일 업로드 (최초 1회)

EC2에 설정 파일들을 업로드합니다. **로컬 PC**에서 실행:

**Windows CMD:**
```cmd
scripts\upload-to-ec2.bat C:\path\to\your-key.pem ubuntu@your-ec2-ip
```

**또는 수동으로:**
```bash
scp -i your-key.pem deploy/docker-compose.prod.yml ubuntu@your-ec2-ip:/home/ubuntu/myroom/
scp -i your-key.pem deploy/.env ubuntu@your-ec2-ip:/home/ubuntu/myroom/
scp -i your-key.pem scripts/deploy.sh ubuntu@your-ec2-ip:/home/ubuntu/myroom/
scp -i your-key.pem scripts/rollback.sh ubuntu@your-ec2-ip:/home/ubuntu/myroom/
scp -i your-key.pem nginx.conf ubuntu@your-ec2-ip:/home/ubuntu/myroom/

# EC2에서 실행 권한 부여
ssh -i your-key.pem ubuntu@your-ec2-ip "chmod +x /home/ubuntu/myroom/*.sh"
```

---

## 💻 로컬 PC 설정

### 1. Docker Hub 로그인

**Windows CMD:**
```cmd
docker login
```
Username과 Password(또는 Access Token) 입력

### 2. 환경 변수 설정

```cmd
# deploy/.env 파일 생성
copy deploy\.env.example deploy\.env

# .env 파일 편집
notepad deploy\.env
```

**.env 파일 내용:**
```env
# Docker Hub 설정 - 본인 Docker Hub 사용자명으로 변경
DOCKERHUB_USERNAME=your-dockerhub-username
IMAGE_NAME=myroom-app
IMAGE_TAG=latest

# 데이터베이스 설정 - 보안상 강력한 비밀번호로 변경
DB_PASSWORD=your_secure_password
MYSQL_ROOT_PASSWORD=your_root_password

# RabbitMQ 설정
RABBITMQ_USER=guest
RABBITMQ_PASSWORD=your_rabbitmq_password
```

---

## 🚀 배포 프로세스

### 전체 배포 흐름

```
1. 로컬에서 코드 수정
       ↓
2. 로컬에서 빌드 & Docker Hub 푸시
   (build-and-push.bat)
       ↓
3. EC2에서 이미지 Pull & 컨테이너 재시작
   (deploy.sh)
       ↓
4. 서비스 확인
```

### Step 1: 로컬에서 빌드 및 푸시

**Windows CMD:**
```cmd
cd C:\Users\asa\Desktop\code\graduation\myroom
scripts\build-and-push.bat

# 특정 버전 태그로 빌드
scripts\build-and-push.bat v1.0.0
```

### Step 2: EC2에서 배포

```bash
# EC2 접속
ssh -i your-key.pem ubuntu@your-ec2-ip

# 배포 디렉토리로 이동
cd /home/ubuntu/myroom

# 배포 실행
./deploy.sh

# 또는 특정 버전 배포
./deploy.sh v1.0.0
```

### Step 3: 배포 확인

```bash
# 컨테이너 상태 확인
docker compose -f docker-compose.prod.yml ps

# 애플리케이션 로그 확인
docker compose -f docker-compose.prod.yml logs -f app

# 헬스 체크
curl http://localhost/actuator/health
```

---

## 📜 스크립트 사용법

### 디렉토리 구조
```
myroom/
├── deploy/
│   ├── docker-compose.prod.yml    # 프로덕션용 Compose 파일
│   ├── .env.example               # 환경변수 예제
│   └── .env                       # 실제 환경변수 (Git 무시)
├── scripts/
│   ├── build-and-push.bat         # Windows 빌드 스크립트
│   ├── deploy.sh                  # EC2 배포 스크립트
│   ├── rollback.sh                # 롤백 스크립트
│   └── upload-to-ec2.bat          # EC2 파일 업로드 스크립트
```

### 스크립트 설명

| 스크립트 | 실행 위치 | 설명 |
|---------|----------|------|
| `build-and-push.bat` | 로컬 (Windows) | Gradle 빌드 → Docker 이미지 빌드 → Docker Hub 푸시 |
| `build-and-push.sh` | 로컬 (Linux/Mac) | 위와 동일 |
| `deploy.sh` | EC2 서버 | Docker Hub에서 이미지 Pull → 컨테이너 재시작 |
| `rollback.sh` | EC2 서버 | 이전 버전으로 롤백 |
| `upload-to-ec2.bat` | 로컬 (Windows) | EC2에 설정 파일 업로드 |

### EC2 서버에 파일 복사 (최초 1회)

**Windows CMD (간편):**
```cmd
scripts\upload-to-ec2.bat C:\path\to\your-key.pem ubuntu@your-ec2-ip
```

**수동으로 복사:**
```bash
# 로컬에서 실행
scp -i your-key.pem -r deploy/* ubuntu@your-ec2-ip:/home/ubuntu/myroom/
scp -i your-key.pem scripts/deploy.sh ubuntu@your-ec2-ip:/home/ubuntu/myroom/
scp -i your-key.pem scripts/rollback.sh ubuntu@your-ec2-ip:/home/ubuntu/myroom/
scp -i your-key.pem nginx.conf ubuntu@your-ec2-ip:/home/ubuntu/myroom/

# EC2에서 실행 권한 부여
ssh -i your-key.pem ubuntu@your-ec2-ip "chmod +x /home/ubuntu/myroom/*.sh"
```

### 롤백 사용법

```bash
# 사용 가능한 버전 확인 및 롤백
./rollback.sh

# 특정 버전으로 바로 롤백
./rollback.sh v1.0.0
```

---

## 🔧 트러블슈팅

### 1. Docker Push 실패: "denied: requested access to the resource is denied"

**원인:** Docker Hub 로그인이 안 되었거나 Repository 권한 문제

**해결:**
```bash
# Docker Hub 재로그인
docker logout
docker login

# 이미지 이름이 올바른지 확인 (username/repo-name 형식)
docker images
```

### 2. EC2에서 이미지 Pull 실패: "unauthorized"

**원인:** EC2에서 Docker Hub 로그인이 안 됨

**해결:**
```bash
# EC2에서 Docker Hub 로그인
docker loginㄹ
# Username과 Password(Access Token) 입력
```

### 3. 컨테이너 시작 후 바로 종료됨

**원인:** 애플리케이션 시작 오류

**해결:**
```bash
# 로그 확인
docker compose -f docker-compose.prod.yml logs app

# 일반적인 원인:
# - DB 연결 실패 → .env의 DB_PASSWORD 확인
# - 포트 충돌 → docker ps로 사용 중인 포트 확인
```

### 4. Nginx Bad Gateway (502)

**원인:** Spring 앱이 아직 시작되지 않음

**해결:**
```bash
# 앱 컨테이너 상태 확인
docker compose -f docker-compose.prod.yml ps

# 앱 로그 확인
docker compose -f docker-compose.prod.yml logs -f app

# 앱이 시작 중이면 잠시 대기 (Spring Boot 시작에 30초~1분 소요)
```

### 5. 디스크 공간 부족

**원인:** Docker 이미지/컨테이너가 쌓임

**해결:**
```bash
# 사용하지 않는 Docker 리소스 정리
docker system prune -a

# 오래된 이미지만 삭제
docker image prune -a --filter "until=24h"
```

---

## 📝 유용한 명령어

### 로그 확인
```bash
# 실시간 로그
docker compose -f docker-compose.prod.yml logs -f

# 특정 서비스 로그
docker compose -f docker-compose.prod.yml logs -f app
docker compose -f docker-compose.prod.yml logs -f mysql

# 최근 100줄만
docker compose -f docker-compose.prod.yml logs --tail=100 app
```

### 컨테이너 관리
```bash
# 상태 확인
docker compose -f docker-compose.prod.yml ps

# 재시작
docker compose -f docker-compose.prod.yml restart app

# 중지
docker compose -f docker-compose.prod.yml down

# 볼륨 포함 완전 삭제 (⚠️ 데이터 손실)
docker compose -f docker-compose.prod.yml down -v
```

### Docker Hub 이미지 확인
```bash
# 로컬 이미지 목록
docker images | grep myroom

# Docker Hub에서 태그 확인 (웹에서 확인 권장)
# https://hub.docker.com/r/your-username/myroom-app/tags
```

---

## 🔒 보안 권장사항

1. **Access Token 사용**
   - Docker Hub 비밀번호 대신 Access Token 사용
   - 토큰은 필요한 권한만 부여

2. **환경 변수 관리**
   - `.env` 파일은 절대 Git에 커밋하지 않음
   - AWS Secrets Manager 또는 Parameter Store 사용 고려

3. **프로덕션 HTTPS 설정**
   - Let's Encrypt로 무료 SSL 인증서 발급
   - nginx.conf에서 HTTPS 설정 활성화

4. **정기 업데이트**
   ```bash
   # 베이스 이미지 업데이트
   docker pull eclipse-temurin:17-jdk-alpine
   docker pull nginx:alpine
   docker pull mysql:8.0
   ```

---

## 📞 문의

배포 중 문제가 발생하면:
1. 먼저 [트러블슈팅](#트러블슈팅) 섹션 확인
2. `docker compose logs`로 로그 수집
3. 이슈 등록 시 로그와 함께 상세 상황 기술
