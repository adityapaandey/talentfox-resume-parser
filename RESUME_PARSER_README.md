# 📄 Resume Parser Portal

**A Simple PDF Resume Parser - No LLM API Required!**

## ⚡ Quick Start

### Windows Users

1. **Start Backend** (in one terminal):
   ```bash
   double-click start_backend.bat
   ```
   Wait until you see: "Application startup complete"

2. **Start Frontend** (in another terminal):
   ```bash
   double-click start_frontend.bat
   ```
   
3. **Open Browser**:
   ```
   http://localhost:3000
   ```

### Manual Start

**Backend:**
```bash
cd backend
pip install -r requirements.txt
uvicorn app.main:app --reload --host 0.0.0.0 --port 8000
```

**Frontend:**
```bash
cd frontend
python -m http.server 3000
```

## 🎯 Features

✅ **No API Keys** - 100% local processing  
✅ **Drag & Drop** - Easy file upload  
✅ **Multiple Files** - Batch processing  
✅ **Smart Extraction** - Name, email, phone, skills, education, experience  
✅ **Excel Export** - Beautiful formatted spreadsheets  
✅ **Modern UI** - Gradient design with animations  

## 📊 What Gets Extracted

- 👤 **Name** - From resume header
- 📧 **Email** - Email addresses
- 📞 **Phone** - Phone numbers (international format supported)
- 🔗 **LinkedIn** - LinkedIn profile URLs
- 💻 **GitHub** - GitHub profile URLs
- 🛠️ **Skills** - 200+ tech skills (Python, JavaScript, React, AWS, etc.)
- 🏛️ **Education** - Degrees and universities
- 📅 **Experience** - Years of experience
- 📝 **Summary** - Professional summary

## 📁 Project Structure

```
talentfor-hr/
├── backend/               # FastAPI backend
│   ├── app/
│   │   ├── api/          # API routes
│   │   ├── services/     # Business logic
│   │   └── core/         # Configuration
│   └── requirements.txt
├── frontend/              # Web interface
│   ├── index.html
│   ├── styles.css
│   └── app.js
├── start_backend.bat      # Windows startup
├── start_frontend.bat     # Windows startup
└── RESUME_PARSER_GUIDE.md # Full documentation
```

## 🛠️ Technology

**Backend:**
- FastAPI - Modern Python web framework
- PDFPlumber - PDF text extraction
- Pandas - Data processing
- OpenPyXL - Excel generation

**Frontend:**
- HTML5, CSS3, JavaScript
- No frameworks - Pure vanilla JS
- Modern gradient UI design

## 📝 How It Works

1. **Upload** - Drag & drop or browse PDF resumes
2. **Parse** - Extract text using PDFPlumber
3. **Extract** - Use regex patterns to find information
4. **Display** - Show results in beautiful cards
5. **Export** - Download as formatted Excel file

## 💾 API Endpoints

- `POST /api/resume-parser/parse` - Parse single resume
- `POST /api/resume-parser/parse-multiple` - Parse multiple resumes
- `GET /api/resume-parser/export-excel` - Download Excel file
- `GET /api/resume-parser/parsed-resumes` - Get all parsed data
- `DELETE /api/resume-parser/clear` - Clear session data
- `GET /api/resume-parser/health` - Health check

## 🎮 Demo Workflow

1. Open http://localhost:3000
2. Drag PDF resumes to the upload area
3. Click "Parse Resumes"
4. View extracted information in cards
5. Click "Export to Excel"
6. Open the downloaded Excel file

## ⚠️ Important Notes

- **PDF Format**: Only text-based PDFs work (not scanned images)
- **Accuracy**: Depends on resume format and structure
- **Session Storage**: Data is cleared when server restarts
- **No Database**: All data stored in memory temporarily

## 🔧 Troubleshooting

**Backend won't start?**
- Check if Python is installed: `python --version`
- Install dependencies: `pip install -r requirements.txt`
- Check if port 8000 is available

**Frontend can't connect?**
- Ensure backend is running first
- Check console for errors (F12 in browser)
- Verify URL is http://localhost:8000

**Parsing errors?**
- Ensure PDF is not password-protected
- Check if PDF has selectable text (not scanned image)
- Try a different resume format

## 📚 Full Documentation

See [RESUME_PARSER_GUIDE.md](RESUME_PARSER_GUIDE.md) for complete documentation.

## ✨ No LLM, No API Keys, Just Code!

This parser uses **pure pattern matching** and **regex** - no AI services required!

---

**Ready to parse some resumes? Let's go! 🚀**
