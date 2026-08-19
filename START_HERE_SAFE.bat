@echo off
color 0A
echo.
echo ========================================
echo     RESUME PARSER PORTAL
echo     Setup and Start Script
echo ========================================
echo.

REM Check if Python is installed
echo [1/5] Checking Python installation...
python --version >nul 2>&1
if errorlevel 1 (
    echo.
    echo ========================================
    echo ERROR: Python is not installed!
    echo ========================================
    echo.
    echo Please install Python first:
    echo 1. Go to https://www.python.org/downloads/
    echo 2. Download Python 3.8 or higher
    echo 3. Run installer and CHECK "Add Python to PATH"
    echo 4. Restart this script
    echo.
    echo Or read SETUP_INSTRUCTIONS.md for details
    echo.
    pause
    exit /b 1
)

python --version
echo SUCCESS: Python is installed!
echo.

REM Check if pip is available
echo [2/5] Checking pip...
pip --version >nul 2>&1
if errorlevel 1 (
    echo ERROR: pip is not available
    echo Try: python -m ensurepip
    pause
    exit /b 1
)
echo SUCCESS: pip is available!
echo.

REM Install minimal requirements
echo [3/5] Installing dependencies...
echo This may take a few minutes...
cd backend
pip install fastapi uvicorn pdfplumber pandas openpyxl python-multipart pydantic-settings --quiet
if errorlevel 1 (
    echo.
    echo WARNING: Some packages failed to install
    echo Trying with requirements_minimal.txt...
    pip install -r requirements_minimal.txt
    if errorlevel 1 (
        echo ERROR: Failed to install dependencies
        echo Please manually run: pip install fastapi uvicorn pdfplumber pandas openpyxl python-multipart pydantic-settings
        pause
        exit /b 1
    )
)
cd ..
echo SUCCESS: Dependencies installed!
echo.

REM Start backend
echo [4/5] Starting backend server...
echo Backend will run on: http://localhost:8000
echo.
start "Resume Parser Backend" cmd /k "cd /d "%~dp0backend" && echo Starting backend... && uvicorn app.main:app --reload --host 0.0.0.0 --port 8000"

echo Waiting for backend to initialize...
timeout /t 8 /nobreak >nul

REM Start frontend
echo [5/5] Starting frontend server...
echo Frontend will open at: http://localhost:3000
echo.
start "Resume Parser Frontend" cmd /k "cd /d "%~dp0frontend" && echo Starting frontend... && python -m http.server 3000"

echo.
echo Waiting for frontend to initialize...
timeout /t 5 /nobreak >nul

echo.
echo ========================================
echo     APPLICATION STARTED!
echo ========================================
echo.
echo Backend:  http://localhost:8000
echo Frontend: http://localhost:3000
echo API Docs: http://localhost:8000/docs
echo.
echo Opening browser in 3 seconds...
timeout /t 3 /nobreak >nul
start http://localhost:3000

echo.
echo ========================================
echo     SERVERS ARE RUNNING
echo ========================================
echo.
echo Two terminal windows are now open:
echo  1. Backend (port 8000)
echo  2. Frontend (port 3000)
echo.
echo Keep both windows open to use the application!
echo Close them when you're done.
echo.
echo Press any key to exit this window (servers will keep running)
pause >nul
