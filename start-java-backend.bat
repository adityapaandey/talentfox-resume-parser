@echo off
color 0B
echo.
echo ========================================
echo  Resume Parser - Java Backend
echo  Spring Boot with JDK 25
echo ========================================
echo.

REM Check Java
echo [1/3] Checking Java installation...
java --version >nul 2>&1
if errorlevel 1 (
    echo ERROR: Java is not installed!
    echo Please install JDK 25 first.
    pause
    exit /b 1
)

java --version
echo SUCCESS: Java is installed!
echo.

REM Navigate to backend-java directory
cd backend-java

echo [2/3] Building application...
echo This may take a few minutes on first run...
echo.

REM Build with Maven wrapper (Windows)
if exist "mvnw.cmd" (
    call mvnw.cmd clean package -DskipTests
) else (
    echo ERROR: Maven wrapper not found!
    echo Please download Maven wrapper or use: mvn clean package -DskipTests
    pause
    exit /b 1
)

if errorlevel 1 (
    echo.
    echo ERROR: Build failed!
    echo Check the error messages above.
    pause
    exit /b 1
)

echo.
echo SUCCESS: Build complete!
echo.

echo [3/3] Starting Spring Boot application...
echo.
echo Backend URL: http://localhost:8080
echo Health Check: http://localhost:8080/api/resume-parser/health
echo.

REM Find and run the JAR file
for /r target %%i in (*.jar) do (
    if not "%%~nxi"=="*-sources.jar" (
        if not "%%~nxi"=="*-javadoc.jar" (
            java -jar "%%i"
            goto :end
        )
    )
)

:end
pause
