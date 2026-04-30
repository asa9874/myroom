# MyRoom
3D 방 꾸미기와 커뮤니티 경험을 제공하는 Spring Boot 백엔드.

[AI 서버 레포지토리](https://github.com/asa9874/myroom-ai)

## Features
- 회원/인증: Spring Security + JWT 기반 인증/인가
- 커뮤니티: 게시글/댓글 및 이미지 업로드
- 3D 도메인: 룸/모델/도면 관리 및 관련 관리자 기능
- 추천/AI 연동: 추천 파이프라인 연동을 위한 도메인 및 메시지 처리
- 실시간: WebSocket 및 RabbitMQ 기반 메시징

## Tech Stack
| 영역 | 기술 |
| --- | --- |
| Backend | Java 17, Spring Boot 4, Spring MVC |
| Security | Spring Security, JWT (jjwt) |
| Data | Spring Data JPA, MySQL 8 |
| Messaging | WebSocket, RabbitMQ |
| Storage | AWS S3 (spring-cloud-aws) |
| API Docs | springdoc-openapi (Swagger UI) |
| Build/Deploy | Gradle, Docker, Nginx |
| Etc | Lombok, ImageIO WebP |

## Architecture & Flows
### 전체 아키텍처
![전체 아키텍처](image/시스템%20아키텍쳐.png)

### 기능별 플로우
#### 가구 사이즈 입력 기능
![가구 사이즈 입력 기능](image/가구%20사이즈입력.png)

#### AI 가구 추천 기능
![AI 가구 추천 기능](image/AI%20가구%20추천.png)

#### 3D 모델 생성 기능
![3D 모델 생성 기능](image/3d%20가구%20모델%20생성.png)

#### 도면 3D 방 생성 기능
![도면 3D 방 생성 기능](image/도면%203D%20방%20생성.png)

## Installation & Usage
### 사전 요구사항
- JDK 17
- Docker Desktop (MySQL, RabbitMQ) 또는 로컬 설치
- (선택) AWS S3 자격 증명

### 로컬 실행
1) 인프라 기동 (MySQL, RabbitMQ)
```bash
docker-compose up -d
```

2) 애플리케이션 실행
```bash
# Windows
./gradlew.bat bootRun

# macOS/Linux
./gradlew bootRun
```

3) 확인
- Swagger UI: http://localhost:8080/swagger-ui/index.html
- RabbitMQ 관리 UI: http://localhost:15672

### 프로파일 및 설정
- 기본 설정: [src/main/resources/application.yml](src/main/resources/application.yml)
- Docker 환경: [src/main/resources/application-docker.yml](src/main/resources/application-docker.yml)
- S3 설정: [src/main/resources/application-s3.yml](src/main/resources/application-s3.yml)

#### 로컬 기본값
- MySQL: `localhost:3307` / DB: `myroom`
- RabbitMQ: `localhost:5672`
- 업로드 경로: `C:/uploads/`

#### Docker 기본값
- MySQL: `mysql:3306`
- RabbitMQ: `rabbitmq:5672`
- 업로드 경로: `/tmp/uploads`

> S3 사용 시 `spring.cloud.aws.credentials`를 환경 변수로 주입하는 방식을 권장합니다.

## Project Structure
```
image/                  # 아키텍처 및 기능 플로우 다이어그램
src/
  main/
    java/com/example/myroom/
      admin/            # 관리자 기능
      domain/           # auth, member, post, comment, image, recommand, room3D, socket 등
      global/           # config, exception, jwt, service, util
      MyroomApplication.java
    resources/
      application.yml
      application-docker.yml
      application-s3.yml
      logback.xml
  test/
    java/com/example/myroom/
```

## Contributing
1) 이슈 등록 또는 기능 제안
2) 브랜치 생성: `feat/your-feature`
3) 커밋 및 PR 생성
4) 테스트 실행 후 공유: `./gradlew.bat test`
