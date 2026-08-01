@echo off
REM ============================================================
REM  Asset Tracker — Build & Run Script for Windows
REM  Requires: Java 17+, Maven 3.8+
REM ============================================================

echo ============================================================
echo   Asset Tracker — Build Script
echo ============================================================
echo.

REM Check Java version
java -version 2>&1
if %ERRORLEVEL% NEQ 0 (
    echo ERROR: Java not found. Install Java 17+ from https://adoptium.net
    pause
    exit /b 1
)

REM Check Maven
mvn -version 2>&1
if %ERRORLEVEL% NEQ 0 (
    echo ERROR: Maven not found. Install Maven 3.8+ from https://maven.apache.org
    pause
    exit /b 1
)

echo.
echo [1/2] Compiling and packaging...
mvn clean package -q

if %ERRORLEVEL% NEQ 0 (
    echo ERROR: Build failed. Run "mvn clean package" for details.
    pause
    exit /b 1
)

echo [2/2] Launching Asset Tracker...
echo.
mvn javafx:run

pause
