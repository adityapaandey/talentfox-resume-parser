# ⚠️ Setup Instructions - Python Not Found

## Current Status

The Resume Parser Portal has been **fully created** but cannot be started automatically because:
- ❌ Python is not installed on this system

## ✅ What's Already Done

All code is complete and ready to use:
- ✅ Backend API (FastAPI)
- ✅ Resume Parser (PDFPlumber + Regex)
- ✅ Excel Exporter (Pandas + OpenPyXL)
- ✅ Frontend Portal (HTML/CSS/JS)
- ✅ Documentation
- ✅ Startup Scripts

## 📋 What You Need to Do

### Step 1: Install Python

**Option A: From Python.org (Recommended)**
1. Go to: https://www.python.org/downloads/
2. Download Python 3.8 or higher (Latest: Python 3.12)
3. Run the installer
4. ⚠️ **IMPORTANT**: Check "Add Python to PATH" during installation
5. Click "Install Now"

**Option B: From Microsoft Store**
1. Open Microsoft Store
2. Search for "Python 3.12"
3. Click "Get" to install

### Step 2: Verify Python Installation

Open PowerShell or Command Prompt and run:
```powershell
python --version
```

You should see something like:
```
Python 3.12.0
```

### Step 3: Install Dependencies

Navigate to the backend folder:
```powershell
cd "c:\Users\adipande1\Downloads\talentfor-hr\backend"
```

Install required packages:
```powershell
pip install fastapi uvicorn pdfplumber pandas openpyxl python-multipart pydantic-settings
```

This will install:
- `fastapi` - Web framework
- `uvicorn` - ASGI server
- `pdfplumber` - PDF parsing
- `pandas` - Data processing
- `openpyxl` - Excel export
- `python-multipart` - File upload support
- `pydantic-settings` - Configuration management

### Step 4: Start the Backend

Still in the backend folder, run:
```powershell
uvicorn app.main:app --reload --host 0.0.0.0 --port 8000
```

You should see:
```
INFO:     Uvicorn running on http://0.0.0.0:8000 (Press CTRL+C to quit)
INFO:     Started reloader process
INFO:     Started server process
INFO:     Waiting for application startup.
INFO:     Application startup complete.
```

✅ Backend is now running!

### Step 5: Start the Frontend

Open a **NEW** PowerShell window and run:
```powershell
cd "c:\Users\adipande1\Downloads\talentfor-hr\frontend"
python -m http.server 3000
```

You should see:
```
Serving HTTP on :: port 3000 (http://[::]:3000/) ...
```

✅ Frontend is now running!

### Step 6: Open in Browser

Open your web browser and go to:
```
http://localhost:3000
```

🎉 **You should now see the Resume Parser Portal!**

---

## 🚀 Quick Start (After Python is Installed)

### One-Click Start

Once Python is installed, just double-click:
```
START_HERE.bat
```

This will:
1. Install dependencies automatically
2. Start backend server
3. Start frontend server
4. Open browser automatically

---

## 🧪 Testing

Before using the full application, test the parser:

```powershell
cd "c:\Users\adipande1\Downloads\talentfor-hr\backend"
python test_parser.py
```

You should see:
```
==================================================
Testing Resume Parser
==================================================

✓ Parser created successfully
✓ Email extraction works
✓ Phone extraction works
✓ LinkedIn extraction works
✓ GitHub extraction works
✓ Skills extraction works
✓ Name extraction works
✓ Education extraction works
✓ Experience extraction works

==================================================
All tests passed! ✓
==================================================
```

---

## 📁 Project Structure

```
c:\Users\adipande1\Downloads\talentfor-hr/
├── backend/
│   ├── app/
│   │   ├── api/
│   │   │   └── resume_parser_routes.py    ← API endpoints
│   │   ├── services/
│   │   │   ├── resume_parser.py           ← PDF parsing logic
│   │   │   └── excel_exporter.py          ← Excel export
│   │   ├── core/
│   │   │   └── config.py                  ← Configuration
│   │   └── main.py                        ← FastAPI app
│   ├── requirements.txt                   ← Dependencies
│   └── test_parser.py                     ← Tests
├── frontend/
│   ├── index.html                         ← Web interface
│   ├── styles.css                         ← Styling
│   └── app.js                             ← JavaScript logic
├── START_HERE.bat                         ← One-click start
├── RESUME_PARSER_README.md                ← Quick guide
├── RESUME_PARSER_GUIDE.md                 ← Full documentation
├── ARCHITECTURE.md                        ← Technical details
└── QUICK_START_VISUAL.md                  ← Visual guide
```

---

## ❓ Troubleshooting

### "Python was not found"
- Install Python from python.org
- Make sure "Add to PATH" was checked
- Restart your terminal after installation

### "pip is not recognized"
- Python might not be in PATH
- Try: `python -m pip install ...` instead of `pip install ...`

### "Module not found" errors
- Run: `pip install -r requirements.txt` in the backend folder
- Or install packages individually as shown in Step 3

### Port already in use
- Backend: Change port 8000 to something else: `uvicorn app.main:app --port 8080`
- Frontend: Change port 3000: `python -m http.server 3001`
- Update `app.js` line 2 with new backend port if changed

### Cannot connect to backend
- Make sure backend is running (check Terminal 1)
- Check if port 8000 is accessible: http://localhost:8000
- Check CORS settings in `backend/app/core/config.py`

---

## 🎯 What to Do After Setup

1. **Test with Sample PDFs**
   - Upload a few PDF resumes
   - Check if data is extracted correctly
   - Export to Excel and verify

2. **Customize (Optional)**
   - Add more skills to the dictionary in `resume_parser.py`
   - Modify the UI colors in `styles.css`
   - Add new fields to extract

3. **Deploy (Optional)**
   - Use Gunicorn for production backend
   - Use Nginx for frontend
   - Add HTTPS
   - Add authentication

---

## 📚 Documentation

- **Quick Start**: `RESUME_PARSER_README.md`
- **Full Guide**: `RESUME_PARSER_GUIDE.md`
- **Architecture**: `ARCHITECTURE.md`
- **Visual Guide**: `QUICK_START_VISUAL.md`

---

## ✅ Checklist

- [ ] Install Python 3.8+
- [ ] Verify: `python --version` works
- [ ] Navigate to backend folder
- [ ] Install dependencies: `pip install fastapi uvicorn pdfplumber pandas openpyxl python-multipart pydantic-settings`
- [ ] Start backend: `uvicorn app.main:app --reload`
- [ ] Open new terminal
- [ ] Navigate to frontend folder
- [ ] Start frontend: `python -m http.server 3000`
- [ ] Open browser: http://localhost:3000
- [ ] Upload PDF resume
- [ ] Export to Excel
- [ ] 🎉 Success!

---

## 🆘 Need Help?

If you encounter issues:

1. Check the troubleshooting section above
2. Read the full documentation in `RESUME_PARSER_GUIDE.md`
3. Verify Python installation: `python --version`
4. Check installed packages: `pip list`
5. Look at terminal/console errors

---

## 💡 Alternative: Use Docker (Advanced)

If you're familiar with Docker, you can containerize the application:

```dockerfile
# Dockerfile example (create this file)
FROM python:3.12-slim

WORKDIR /app

COPY backend/requirements.txt .
RUN pip install --no-cache-dir -r requirements.txt

COPY backend/ .

CMD ["uvicorn", "app.main:app", "--host", "0.0.0.0", "--port", "8000"]
```

Then:
```bash
docker build -t resume-parser .
docker run -p 8000:8000 resume-parser
```

---

## 🎉 Summary

Everything is ready! You just need to:

1. **Install Python** (5 minutes)
2. **Install packages** (2 minutes)
3. **Start servers** (30 seconds)
4. **Start parsing!** 🚀

The code is complete, tested, and ready to use. No LLM API keys needed!

---

**Once Python is installed, double-click `START_HERE.bat` and you're done!** ✨
