@echo off
REM HytaleAE2 Deployment Script (Windows)
REM Builds and deploys the plugin to Hytale

setlocal enabledelayedexpansion

REM Configuration
set PROJECT_NAME=HytaleAE2
set JAR_NAME=HytaleAE2-0.2.0.jar
set BUILD_OUTPUT=build\libs\%JAR_NAME%
set HYTALE_MODS_PATH=%APPDATA%\Hytale\UserData\Mods

REM Colors (using ANSI codes)
for /F %%A in ('echo prompt $H ^| cmd') do set "BS=%%A"

cls
echo.
echo ╔════════════════════════════════════════════════════════════╗
echo ║          HytaleAE2 Deployment Script (Windows)             ║
echo ╚════════════════════════════════════════════════════════════╝
echo.

REM Step 1: Build
echo [*] Building project...
call .\gradlew.bat clean shadowJar --info
if %errorlevel% neq 0 (
    echo [X] Build failed!
    exit /b 1
)
echo [OK] Build completed
echo.

REM Step 2: Verify JAR
if not exist "%BUILD_OUTPUT%" (
    echo [X] JAR not found at %BUILD_OUTPUT%
    exit /b 1
)
echo [OK] JAR created: %BUILD_OUTPUT%
echo.

REM Step 3: Check Mods directory
if not exist "%HYTALE_MODS_PATH%" (
    echo [*] Creating Hytale mods directory...
    mkdir "%HYTALE_MODS_PATH%"
)
echo.

REM Step 4: Deploy
echo [*] Deploying to Hytale...
copy "%BUILD_OUTPUT%" "%HYTALE_MODS_PATH%\%JAR_NAME%"
if %errorlevel% neq 0 (
    echo [X] Deployment failed!
    exit /b 1
)
echo [OK] Deployed to: %HYTALE_MODS_PATH%\%JAR_NAME%
echo.

REM Step 5: Show file info
for %%A in ("%HYTALE_MODS_PATH%\%JAR_NAME%") do (
    set JAR_SIZE=%%~zA
    set JAR_DATE=%%~TA
)
echo [*] File size: %JAR_SIZE% bytes
echo [*] Timestamp: %JAR_DATE%
echo.

echo ╔════════════════════════════════════════════════════════════╗
echo ║          Deployment Complete - Ready to Use!               ║
echo ╚════════════════════════════════════════════════════════════╝
echo.
echo Start Hytale and check the logs for: HytaleAE2
echo.

endlocal
