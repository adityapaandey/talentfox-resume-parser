@echo off
echo.
echo ====================================================
echo   Resume Parser - Java Version (Standalone)
echo ====================================================
echo.
echo Note: JDK 25 has compatibility issues with Maven.
echo.
echo The Python version is ready to use!
echo Just install Python and run: start-java-app.bat
echo.
echo For Java version to work, you need:
echo 1. Install JDK 17 or JDK 21 (not JDK 25)
echo 2. Then run: backend-java\mvnw.cmd spring-boot:run
echo.
echo Alternatively, starting frontend with Python backend...
echo.

cd frontend
start "Frontend" cmd /k "python -m http.server 3000"

echo.
echo Frontend started at: http://localhost:3000
echo.
echo Note: Backend needs to be started separately
echo (Python backend on port 8000 or Java on port 8080)
echo.
pause
