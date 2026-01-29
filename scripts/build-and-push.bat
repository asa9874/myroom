@echo off
REM ===============================================================================
REM 로컬 빌드 및 Docker Hub 푸시 스크립트 (Windows)
REM 사용법: build-and-push.bat [tag]
REM ===============================================================================

setlocal enabledelayedexpansion

set "IMAGE_NAME=myroom-app"
set "TAG=%1"
if "%TAG%"=="" set "TAG=latest"

echo ========================================
echo   MyRoom Docker Build ^& Push Script
echo ========================================
echo.

REM 프로젝트 루트로 이동
cd /d "%~dp0\.."

REM 환경 변수 로드
if exist "deploy\.env" (
    for /f "tokens=1,2 delims==" %%a in ('type "deploy\.env" ^| findstr /v "^#"') do (
        if not "%%a"=="" if not "%%b"=="" set "%%a=%%b"
    )
)

REM Docker Hub 사용자명 확인
if "%DOCKERHUB_USERNAME%"=="" (
    echo 오류: DOCKERHUB_USERNAME이 설정되지 않았습니다.
    echo deploy\.env 파일에 DOCKERHUB_USERNAME을 설정하세요.
    exit /b 1
)

echo [1/5] Docker Hub 로그인 확인 중...
docker info >nul 2>&1
if errorlevel 1 (
    echo 오류: Docker가 실행되고 있지 않습니다.
    exit /b 1
)
echo √ Docker 실행 중
echo.

echo [2/5] Gradle 빌드 중...
call gradlew.bat build -x test
if errorlevel 1 (
    echo 오류: Gradle 빌드 실패
    exit /b 1
)
echo ✓ Gradle 빌드 완료
echo.

echo [3/5] Docker 이미지 빌드 중...
set "FULL_IMAGE_NAME=%DOCKERHUB_USERNAME%/%IMAGE_NAME%:%TAG%"
docker build -t "%FULL_IMAGE_NAME%" .
if errorlevel 1 (
    echo 오류: Docker 이미지 빌드 실패
    exit /b 1
)
if not "%TAG%"=="latest" (
    docker tag "%FULL_IMAGE_NAME%" "%DOCKERHUB_USERNAME%/%IMAGE_NAME%:latest"
)
echo √ Docker 이미지 빌드 완료: %FULL_IMAGE_NAME%
echo.

echo [4/5] Docker Hub에 푸시 중...
docker push "%FULL_IMAGE_NAME%"
if errorlevel 1 (
    echo 오류: Docker push 실패
    echo Docker Hub에 로그인했는지 확인하세요: docker login
    exit /b 1
)
if not "%TAG%"=="latest" (
    docker push "%DOCKERHUB_USERNAME%/%IMAGE_NAME%:latest"
)
echo √ Docker Hub 푸시 완료
echo.

echo [5/5] 완료
echo ========================================
echo 빌드 및 푸시가 완료되었습니다!
echo.
echo 이미지: %FULL_IMAGE_NAME%
echo Docker Hub: https://hub.docker.com/r/%DOCKERHUB_USERNAME%/%IMAGE_NAME%
echo.
echo EC2에서 배포하려면:
echo   ssh -i your-key.pem ubuntu@your-ec2-ip
echo   cd /home/ubuntu/myroom
echo   ./deploy.sh
echo ========================================

endlocal
