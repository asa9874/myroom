@echo off
REM ===============================================================================
REM EC2 서버에 배포 파일 업로드 스크립트 (Windows)
REM 사용법: upload-to-ec2.bat [pem파일경로] [ec2-ip]
REM 예시: upload-to-ec2.bat C:\keys\mykey.pem 52.123.456.789
REM ===============================================================================

setlocal enabledelayedexpansion

set "PEM_FILE=%1"
set "EC2_IP=%2"
set "EC2_USER=ubuntu"
set "REMOTE_DIR=/home/ubuntu/myroom"

if "%PEM_FILE%"=="" (
    echo 사용법: upload-to-ec2.bat [pem파일경로] [ec2-ip]
    echo 예시: upload-to-ec2.bat C:\keys\mykey.pem 52.123.456.789
    exit /b 1
)

if "%EC2_IP%"=="" (
    echo 사용법: upload-to-ec2.bat [pem파일경로] [ec2-ip]
    echo 예시: upload-to-ec2.bat C:\keys\mykey.pem 52.123.456.789
    exit /b 1
)

REM ubuntu@ 가 이미 포함되어 있으면 그대로 사용, 아니면 추가
echo %EC2_IP% | findstr /C:"@" >nul
if %errorlevel%==0 (
    set "EC2_HOST=%EC2_IP%"
) else (
    set "EC2_HOST=%EC2_USER%@%EC2_IP%"
)

echo ========================================
echo   EC2 배포 파일 업로드
echo ========================================
echo.

cd /d "%~dp0\.."

echo [1/4] EC2에 디렉토리 생성 중...
ssh -i "%PEM_FILE%" %EC2_HOST% "mkdir -p %REMOTE_DIR%"
echo.

echo [2/4] 배포 설정 파일 업로드 중...
scp -i "%PEM_FILE%" deploy\docker-compose.prod.yml %EC2_HOST%:%REMOTE_DIR%/
scp -i "%PEM_FILE%" deploy\.env.example %EC2_HOST%:%REMOTE_DIR%/
if exist deploy\.env (
    scp -i "%PEM_FILE%" deploy\.env %EC2_HOST%:%REMOTE_DIR%/
    REM Windows 줄바꿈(CRLF)을 Linux 줄바꿈(LF)으로 변환
    ssh -i "%PEM_FILE%" %EC2_HOST% "sed -i 's/\r$//' %REMOTE_DIR%/.env"
    echo .env 파일 업로드 및 변환 완료
) else (
    echo .env 파일 없음 - EC2에서 .env.example 복사 필요
)
scp -i "%PEM_FILE%" nginx.conf %EC2_HOST%:%REMOTE_DIR%/
echo.

echo [3/4] 배포 스크립트 업로드 중...
scp -i "%PEM_FILE%" scripts\deploy.sh %EC2_HOST%:%REMOTE_DIR%/
scp -i "%PEM_FILE%" scripts\rollback.sh %EC2_HOST%:%REMOTE_DIR%/
echo.

echo [4/4] 스크립트 실행 권한 설정 중...
ssh -i "%PEM_FILE%" %EC2_HOST% "chmod +x %REMOTE_DIR%/*.sh"
echo.

echo ========================================
echo 업로드 완료!
echo ========================================
echo.
echo 다음 단계:
echo   1. EC2 접속: ssh -i %PEM_FILE% %EC2_HOST%
echo   2. 디렉토리 이동: cd %REMOTE_DIR%
echo   3. Docker Hub 로그인: docker login
echo   4. 환경변수 설정: cp .env.example .env ^&^& nano .env
echo   5. 배포 실행: ./deploy.sh
echo.

endlocal
