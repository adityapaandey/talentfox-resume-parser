@echo off
echo ========================================
echo  Resume Parser Frontend - Starting...
echo ========================================
echo.

cd frontend

echo Starting HTTP server...
echo.
echo Frontend URL: http://localhost:3000
echo.
echo Open your browser and go to: http://localhost:3000
echo.

python -m http.server 3000

pause
