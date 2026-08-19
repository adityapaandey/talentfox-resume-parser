# Resume Parser Portal - Quick Start Guide

## Overview

This is a **NO-LLM** PDF Resume Parser that extracts structured information from resumes using pattern matching and exports to Excel.

## Features

✅ **PDF Parsing** - Extract text from PDF resumes using PDFPlumber  
✅ **Smart Extraction** - Pattern matching for:
- Name, Email, Phone, LinkedIn, GitHub
- Skills (200+ tech keywords)
- Education (degrees, universities)
- Experience (years)
- Professional Summary

✅ **Excel Export** - Beautiful formatted Excel files with all parsed data  
✅ **Modern Web Portal** - Drag & drop interface with real-time results  
✅ **No API Keys Required** - 100% local processing, no external APIs

## Technology Stack

### Backend
- **FastAPI** - Modern Python web framework
- **PDFPlumber** - PDF text extraction
- **Pandas** - Data manipulation
- **OpenPyXL** - Excel file generation

### Frontend
- **HTML5** - Structure
- **CSS3** - Modern gradient design with animations
- **Vanilla JavaScript** - No framework dependencies

## Installation

### Prerequisites
- Python 3.8+
- pip package manager

### Backend Setup

1. Navigate to backend directory:
```bash
cd backend
```

2. Install dependencies:
```bash
pip install -r requirements.txt
```

3. Start the server:
```bash
uvicorn app.main:app --reload --host 0.0.0.0 --port 8000
```

The API will be available at: `http://localhost:8000`

### Frontend Setup

1. Open the frontend folder
2. Open `index.html` in your browser
   - Or use a simple HTTP server:
   ```bash
   # Python
   cd frontend
   python -m http.server 3000
   ```
   Then visit: `http://localhost:3000`

## Usage

### 1. Upload Resumes
- **Drag & Drop**: Drop PDF files onto the upload area
- **Browse**: Click the upload area to select files
- **Multiple Files**: Upload multiple resumes at once

### 2. Parse Resumes
- Click "Parse Resumes" button
- Watch the progress bar
- View results in cards

### 3. Review Results
- See all extracted information in organized cards
- View statistics (total parsed, unique skills, avg experience)
- Review individual candidate details

### 4. Export to Excel
- Click "Export to Excel" button
- Download formatted Excel file
- Open in Microsoft Excel, Google Sheets, etc.

## API Endpoints

### Parse Single Resume
```
POST /api/resume-parser/parse
Content-Type: multipart/form-data
Body: file (PDF)
```

### Parse Multiple Resumes
```
POST /api/resume-parser/parse-multiple
Content-Type: multipart/form-data
Body: files[] (PDFs)
```

### Export to Excel
```
GET /api/resume-parser/export-excel
Returns: Excel file download
```

### Get Parsed Resumes
```
GET /api/resume-parser/parsed-resumes
Returns: JSON array of parsed resumes
```

### Clear All
```
DELETE /api/resume-parser/clear
Clears all parsed resumes from session
```

### Health Check
```
GET /api/resume-parser/health
Returns: Service status
```

## Extracted Fields

The parser extracts the following information:

| Field | Description | Method |
|-------|-------------|--------|
| **Name** | Candidate's full name | First line pattern matching |
| **Email** | Email address | Regex pattern |
| **Phone** | Phone number | Regex with international support |
| **LinkedIn** | LinkedIn profile URL | URL pattern matching |
| **GitHub** | GitHub profile URL | URL pattern matching |
| **Skills** | Technical skills | 200+ keyword dictionary |
| **Education** | Degrees and universities | Education keyword matching |
| **Experience** | Years of experience | Date range calculation |
| **Summary** | Professional summary | Section header detection |

## Skills Detection

The parser recognizes 200+ technical skills including:

- **Languages**: Python, Java, JavaScript, TypeScript, C++, C#, Go, Rust, etc.
- **Frameworks**: React, Angular, Vue, Django, Flask, FastAPI, Spring, etc.
- **Databases**: SQL, MySQL, PostgreSQL, MongoDB, Redis, etc.
- **Cloud**: AWS, Azure, GCP
- **DevOps**: Docker, Kubernetes, Jenkins, CI/CD, Terraform
- **Data Science**: Machine Learning, TensorFlow, PyTorch, Pandas, NumPy
- **Mobile**: Android, iOS, React Native, Flutter

## Excel Export Format

The exported Excel file includes:

- ✅ Formatted header with colors
- ✅ Auto-adjusted column widths
- ✅ Frozen header row
- ✅ Text wrapping for long content
- ✅ Professional styling

**Columns:**
1. Name
2. Email
3. Phone
4. Experience
5. Skills
6. Education
7. LinkedIn
8. GitHub
9. Summary
10. Filename
11. Parsed Date

## Troubleshooting

### Backend Not Starting
- Check if port 8000 is available
- Verify Python version (3.8+)
- Install dependencies: `pip install -r requirements.txt`

### Cannot Connect to API
- Ensure backend is running
- Check console for CORS errors
- Verify API URL in `app.js` (default: `http://localhost:8000`)

### PDF Parsing Errors
- Ensure PDFs are not password-protected
- Some scanned PDFs may not extract text well
- Complex layouts may affect extraction accuracy

### Excel Export Issues
- Check if you have write permissions
- Ensure openpyxl is installed
- Try parsing at least one resume before exporting

## Limitations

⚠️ **No OCR**: Scanned PDFs without text layer won't parse well  
⚠️ **Pattern-Based**: Extraction accuracy depends on resume format  
⚠️ **English Only**: Optimized for English resumes  
⚠️ **Session Storage**: Data cleared when server restarts

## Future Enhancements

Potential improvements:
- OCR support for scanned PDFs
- Support for Word documents (.docx)
- Database storage for parsed resumes
- Advanced search and filtering
- Bulk resume comparison
- Custom skill dictionaries
- Multi-language support

## Project Structure

```
talentfor-hr/
├── backend/
│   ├── app/
│   │   ├── api/
│   │   │   └── resume_parser_routes.py
│   │   ├── services/
│   │   │   ├── resume_parser.py
│   │   │   └── excel_exporter.py
│   │   └── main.py
│   ├── uploads/
│   │   ├── resumes/
│   │   └── exports/
│   └── requirements.txt
├── frontend/
│   ├── index.html
│   ├── styles.css
│   └── app.js
└── RESUME_PARSER_GUIDE.md
```

## Support

For issues or questions:
1. Check the troubleshooting section
2. Review API documentation
3. Check backend logs for errors
4. Verify all dependencies are installed

---

**Built without LLM APIs - 100% Pattern Matching & Local Processing**
