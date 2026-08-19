@echo off
color 0A
echo.
echo ========================================
echo  RESUME PARSER - JAVA VERSION
echo  Spring Boot + JDK 25
echo ========================================
echo.

REM Check Java
echo Checking Java installation...
java --version >nul 2>&1
if errorlevel 1 (
    echo.
    echo ========================================
    echo ERROR: Java is not installed!
    echo ========================================
    echo.
    echo JDK 25 is required to run this application.
    echo.
    echo Download from:
    echo https://www.oracle.com/java/technologies/downloads/
    echo or
    echo https://adoptium.net/
    echo.
    pause
    exit /b 1
)

echo Java version:
java --version
echo.

echo Starting backend and frontend...
echo.

REM Start backend in new window
echo [1/2] Starting Java backend...
start "Resume Parser - Java Backend" cmd /k "cd /d "%~dp0" && call start-java-backend.bat"

echo Waiting for backend to build and start (30 seconds)...
timeout /t 30 /nobreak >nul

REM Start frontend in new window  
echo.
echo [2/2] Starting frontend...
start "Resume Parser - Frontend" cmd /k "cd /d "%~dp0frontend" && python -m http.server 3000"

echo.
echo Waiting for frontend to start...
timeout /t 5 /nobreak >nul

echo.
echo ========================================
echo     APPLICATION STARTED!
echo ========================================
echo.
echo Backend:  http://localhost:8080
echo Frontend: http://localhost:3000
echo Health:   http://localhost:8080/api/resume-parser/health
echo.
echo Opening browser...
timeout /t 3 /nobreak >nul
start http://localhost:3000

echo.
echo Both servers are running in separate windows.
echo Keep them open to use the application.
echo.
echo Press any key to exit this window (servers will keep running)...
pause >nul
