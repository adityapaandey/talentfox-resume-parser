# TalentFor HR - React Components (Part 4 - FINAL)

## React Component: ResumeUpload.tsx
```typescript
import React, { useCallback, useState } from "react";
import { useDropzone } from "react-dropzone";
import { Box, Button, Typography, LinearProgress, Alert } from "@mui/material";
import CloudUploadIcon from "@mui/icons-material/CloudUpload";
import { candidatesAPI } from "../services/api";

interface ResumeUploadProps {
  onUploadComplete: () => void;
}

const ResumeUpload: React.FC<ResumeUploadProps> = ({ onUploadComplete }) => {
  const [uploading, setUploading] = useState(false);
  const [progress, setProgress] = useState(0);
  const [message, setMessage] = useState("");

  const onDrop = useCallback(async (acceptedFiles: File[]) => {
    if (acceptedFiles.length === 0) return;

    setUploading(true);
    setMessage("");

    try {
      await candidatesAPI.uploadResumes(acceptedFiles);
      setMessage(`Successfully uploaded ${acceptedFiles.length} resume(s)!`);
      onUploadComplete();
    } catch (error) {
      setMessage("Error uploading resumes. Please try again.");
    } finally {
      setUploading(false);
    }
  }, [onUploadComplete]);

  const { getRootProps, getInputProps, isDragActive } = useDropzone({
    onDrop,
    accept: { "application/pdf": [".pdf"] },
    maxSize: 10485760, // 10MB
    multiple: true,
  });

  return (
    <Box sx={{ mb: 4 }}>
      <Box
        {...getRootProps()}
        sx={{
          border: "2px dashed",
          borderColor: isDragActive ? "primary.main" : "grey.400",
          borderRadius: 2,
          p: 4,
          textAlign: "center",
          cursor: "pointer",
          bgcolor: isDragActive ? "action.hover" : "background.paper",
          transition: "all 0.3s",
          "&:hover": {
            borderColor: "primary.main",
            bgcolor: "action.hover",
          },
        }}
      >
        <input {...getInputProps()} />
        <CloudUploadIcon sx={{ fontSize: 64, color: "primary.main", mb: 2 }} />
        <Typography variant="h6" gutterBottom>
          {isDragActive ? "Drop the files here" : "Drag & drop PDF resumes here"}
        </Typography>
        <Typography variant="body2" color="text.secondary" gutterBottom>
          or
        </Typography>
        <Button variant="contained" component="span" sx={{ mt: 2 }}>
          Browse Files
        </Button>
        <Typography variant="caption" display="block" sx={{ mt: 2 }} color="text.secondary">
          Supported: PDF files (Max 10MB per file)
        </Typography>
      </Box>

      {uploading && (
        <Box sx={{ mt: 2 }}>
          <LinearProgress />
          <Typography variant="body2" sx={{ mt: 1 }} align="center">
            Uploading and parsing resumes...
          </Typography>
        </Box>
      )}

      {message && (
        <Alert severity={message.includes("Error") ? "error" : "success"} sx={{ mt: 2 }}>
          {message}
        </Alert>
      )}
    </Box>
  );
};

export default ResumeUpload;
```

## React Component: CandidateGrid.tsx
```typescript
import React, { useState, useEffect, useMemo } from "react";
import { AgGridReact } from "ag-grid-react";
import "ag-grid-community/styles/ag-grid.css";
import "ag-grid-community/styles/ag-theme-material.css";
import { Box, TextField, Button, Chip } from "@mui/material";
import { candidatesAPI } from "../services/api";

interface CandidateGridProps {
  refreshTrigger: number;
}

const CandidateGrid: React.FC<CandidateGridProps> = ({ refreshTrigger }) => {
  const [rowData, setRowData] = useState([]);
  const [searchText, setSearchText] = useState("");

  const columnDefs = useMemo(
    () => [
      { field: "id", headerName: "ID", width: 80, filter: true },
      { field: "name", headerName: "Name", width: 200, filter: true },
      { field: "email", headerName: "Email", width: 220, filter: true },
      { field: "phone", headerName: "Phone", width: 140 },
      { field: "total_experience", headerName: "Experience (Years)", width: 150 },
      { field: "current_company", headerName: "Company", width: 180, filter: true },
      { field: "current_designation", headerName: "Designation", width: 180 },
      {
        field: "skills",
        headerName: "Skills",
        width: 250,
        cellRenderer: (params: any) => {
          const skills = params.value || [];
          return (
            <Box sx={{ display: "flex", gap: 0.5, flexWrap: "wrap", py: 0.5 }}>
              {skills.slice(0, 3).map((skill: string, idx: number) => (
                <Chip key={idx} label={skill} size="small" />
              ))}
              {skills.length > 3 && <Chip label={`+${skills.length - 3}`} size="small" />}
            </Box>
          );
        },
      },
      { field: "match_score", headerName: "Match %", width: 100 },
      {
        field: "upload_date",
        headerName: "Upload Date",
        width: 150,
        valueFormatter: (params: any) =>
          params.value ? new Date(params.value).toLocaleDateString() : "",
      },
    ],
    []
  );

  const fetchCandidates = async () => {
    try {
      const data = await candidatesAPI.getCandidates(0, 100, searchText);
      setRowData(data);
    } catch (error) {
      console.error("Error fetching candidates:", error);
    }
  };

  useEffect(() => {
    fetchCandidates();
  }, [refreshTrigger, searchText]);

  return (
    <Box>
      <Box sx={{ mb: 2, display: "flex", gap: 2 }}>
        <TextField
          label="Search candidates..."
          variant="outlined"
          size="small"
          value={searchText}
          onChange={(e) => setSearchText(e.target.value)}
          sx={{ flexGrow: 1 }}
        />
        <Button variant="outlined" onClick={() => candidatesAPI.exportToExcel()}>
          Export to Excel
        </Button>
        <Button variant="outlined" onClick={() => candidatesAPI.exportToCSV()}>
          Export to CSV
        </Button>
      </Box>

      <Box className="ag-theme-material" sx={{ height: 600, width: "100%" }}>
        <AgGridReact
          rowData={rowData}
          columnDefs={columnDefs}
          pagination={true}
          paginationPageSize={50}
          defaultColDef={{ sortable: true, resizable: true }}
          rowHeight={60}
        />
      </Box>
    </Box>
  );
};

export default CandidateGrid;
```

## Main Dashboard Component: Dashboard.tsx
```typescript
import React, { useState } from "react";
import {
  Container,
  Typography,
  Box,
  Paper,
  AppBar,
  Toolbar,
  IconButton,
} from "@mui/material";
import Brightness4Icon from "@mui/icons-material/Brightness4";
import Brightness7Icon from "@mui/icons-material/Brightness7";
import ResumeUpload from "../components/ResumeUpload";
import CandidateGrid from "../components/CandidateGrid";

const Dashboard: React.FC = () => {
  const [refreshTrigger, setRefreshTrigger] = useState(0);
  const [darkMode, setDarkMode] = useState(false);

  const handleUploadComplete = () => {
    setRefreshTrigger((prev) => prev + 1);
  };

  return (
    <Box sx={{ flexGrow: 1 }}>
      <AppBar position="static">
        <Toolbar>
          <Typography variant="h6" component="div" sx={{ flexGrow: 1 }}>
            TalentFor HR - AI-Powered Recruitment Platform
          </Typography>
          <IconButton onClick={() => setDarkMode(!darkMode)} color="inherit">
            {darkMode ? <Brightness7Icon /> : <Brightness4Icon />}
          </IconButton>
        </Toolbar>
      </AppBar>

      <Container maxWidth="xl" sx={{ mt: 4, mb: 4 }}>
        <Paper elevation={3} sx={{ p: 3, mb: 3 }}>
          <Typography variant="h5" gutterBottom>
            Upload Resumes
          </Typography>
          <ResumeUpload onUploadComplete={handleUploadComplete} />
        </Paper>

        <Paper elevation={3} sx={{ p: 3 }}>
          <Typography variant="h5" gutterBottom sx={{ mb: 2 }}>
            Candidate Database
          </Typography>
          <CandidateGrid refreshTrigger={refreshTrigger} />
        </Paper>
      </Container>
    </Box>
  );
};

export default Dashboard;
```

## App.tsx
```typescript
import React from "react";
import { BrowserRouter as Router } from "react-router-dom";
import { ThemeProvider, createTheme, CssBaseline } from "@mui/material";
import Dashboard from "./pages/Dashboard";

const theme = createTheme({
  palette: {
    primary: { main: "#1976d2" },
    secondary: { main: "#dc004e" },
  },
});

function App() {
  return (
    <ThemeProvider theme={theme}>
      <CssBaseline />
      <Router>
        <Dashboard />
      </Router>
    </ThemeProvider>
  );
}

export default App;
```

## Database Initialization Script (backend/app/db/init_db.py)
```python
from app.db.database import engine, Base
from app.models.candidate import Candidate
from app.models.user import User

def init_database():
    """Initialize database tables"""
    print("Creating database tables...")
    Base.metadata.create_all(bind=engine)
    print("Database tables created successfully!")

if __name__ == "__main__":
    init_database()
```

## QUICK START COMMANDS

### 1. Backend Setup
```bash
cd backend
python -m venv venv
venv\Scripts\activate
pip install -r requirements.txt
python -m spacy download en_core_web_sm
cp .env.example .env
# Edit .env and add your OPENAI_API_KEY
python -m app.db.init_db
uvicorn app.main:app --reload
```

### 2. Frontend Setup
```bash
cd frontend
npx create-react-app . --template typescript
# Copy package.json content from above
npm install
echo REACT_APP_API_URL=http://localhost:8000 > .env
npm start
```

### 3. Database Setup
```bash
# Install PostgreSQL, then:
createdb talentfor_hr
```

## PROJECT COMPLETE! ✅

**Location:** C:\Users\adipande1\Downloads\talentfor-hr

**All Implementation Files Created:**
- ✅ README.md
- ✅ SETUP_GUIDE.md
- ✅ IMPLEMENTATION_CODE.md (Parts 1-4)
- ✅ docker-compose.yml
- ✅ Backend structure (app/, uploads/)
- ✅ Frontend structure ready
- ✅ .gitignore
- ✅ requirements.txt
- ✅ .env.example

**Next Steps:**
1. Open project in VS Code: `code "C:\Users\adipande1\Downloads\talentfor-hr"`
2. Follow SETUP_GUIDE.md
3. Use implementation code from IMPLEMENTATION_CODE*.md files
4. Set up PostgreSQL database
5. Configure .env with OpenAI API key
6. Run backend and frontend

Enjoy your TalentFor HR platform! 🚀
