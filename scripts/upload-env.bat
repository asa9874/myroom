@echo off
REM ===============================================================================
REM EC2 서버에 .env 파일만 업로드하는 스크립트 (Windows)
REM 사용법: upload-env.bat [pem파일경로] [ec2-ip]
REM 예시: upload-env.bat C:\keys\mykey.pem 52.123.456.789
REM ===============================================================================

setlocal enabledelayedexpansion

set "PEM_FILE=%1"
set "EC2_IP=%2"
set "EC2_USER=ubuntu"
set "REMOTE_DIR=/home/ubuntu/myroom"

if "%PEM_FILE%"=="" (
    echo 사용법: upload-env.bat [pem파일경로] [ec2-ip]
    echo 예시: upload-env.bat C:\keys\mykey.pem 52.123.456.789
    exit /b 1
)

if "%EC2_IP%"=="" (
    echo 사용법: upload-env.bat [pem파일경로] [ec2-ip]
    echo 예시: upload-env.bat C:\keys\mykey.pem 52.123.456.789
    exit /b 1
)

REM ubuntu@ 가 이미 포함되어 있으면 그대로 사용, 아니면 추가
echo %EC2_IP% | findstr /C:"@" >nul
if %errorlevel%==0 (
    set "EC2_HOST=%EC2_IP%"
) else (
    set "EC2_HOST=%EC2_USER%@%EC2_IP%"
)

cd /d "%~dp0\.."

if not exist deploy\.env (
    echo 오류: deploy\.env 파일이 존재하지 않습니다.
    echo 먼저 deploy\.env 파일을 생성하세요.
    exit /b 1
)

echo ========================================
echo   .env 파일 업로드
echo ========================================
echo.

echo .env 파일을 EC2로 업로드 중...
scp -i "%PEM_FILE%" deploy\.env %EC2_HOST%:%REMOTE_DIR%/

REM Windows 줄바꿈(CRLF)을 Linux 줄바꿈(LF)으로 변환
ssh -i "%PEM_FILE%" %EC2_HOST% "sed -i 's/\r$//' %REMOTE_DIR%/.env"

if %errorlevel%==0 (
    echo.
    echo ========================================
    echo .env 파일 업로드 완료!
    echo ========================================
    echo.
    echo EC2에서 배포 실행 가능: ./deploy.sh
) else (
    echo.
    echo ========================================
    echo .env 파일 업로드 실패!
    echo ========================================
    exit /b 1
)

endlocal