@echo off
color 0A
echo.
echo ========================================
echo     RESUME PARSER PORTAL
echo     No LLM API - Pattern Matching Only
echo ========================================
echo.
echo This will start both backend and frontend.
echo.
echo Prerequisites:
echo   - Python 3.8+ installed
echo   - pip available
echo.
echo Press any key to continue or Ctrl+C to exit...
pause > nul

echo.
echo [1/3] Installing backend dependencies...
cd backend
pip install -r requirements.txt > nul 2>&1
if errorlevel 1 (
    echo ERROR: Failed to install dependencies
    echo Please run: pip install -r backend/requirements.txt
    pause
    exit /b 1
)
echo SUCCESS: Dependencies installed

cd ..

echo.
echo [2/3] Starting backend server...
echo Backend will run on: http://localhost:8000
echo API Docs available at: http://localhost:8000/docs
echo.
start "Resume Parser Backend" cmd /k "cd backend && uvicorn app.main:app --reload --host 0.0.0.0 --port 8000"

echo Waiting for backend to start...
timeout /t 5 > nul

echo.
echo [3/3] Starting frontend...
echo Frontend will open at: http://localhost:3000
echo.
start "Resume Parser Frontend" cmd /k "cd frontend && python -m http.server 3000"

echo.
echo Waiting for frontend to start...
timeout /t 3 > nul

echo.
echo ========================================
echo     APPLICATION STARTED!
echo ========================================
echo.
echo Backend:  http://localhost:8000
echo Frontend: http://localhost:3000
echo API Docs: http://localhost:8000/docs
echo.
echo Opening browser...
start http://localhost:3000

echo.
echo Keep this window open!
echo Close it to stop both servers.
echo.
echo Press any key to exit and stop servers...
pause > nul

echo.
echo Stopping servers...
taskkill /FI "WINDOWTITLE eq Resume Parser Backend" /T /F > nul 2>&1
taskkill /FI "WINDOWTITLE eq Resume Parser Frontend" /T /F > nul 2>&1

echo.
echo Servers stopped. Goodbye!
timeout /t 2 > nul
