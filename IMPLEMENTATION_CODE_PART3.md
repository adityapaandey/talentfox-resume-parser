# TalentFor HR - API & Frontend Implementation (Part 3)

## API ENDPOINTS

### 7. app/api/candidates.py
```python
from fastapi import APIRouter, UploadFile, File, Depends, HTTPException, Query
from sqlalchemy.orm import Session
from typing import List
from app.db.database import get_db
from app.models.candidate import Candidate
from app.schemas.candidate import CandidateResponse, JobMatchRequest
from app.services.resume_parser import ResumeParser
from app.services.ai_matcher import AIMatcher
from app.core.config import settings
import os
import uuid
from datetime import datetime

router = APIRouter()

@router.post("/upload", response_model=List[CandidateResponse])
async def upload_resumes(
    files: List[UploadFile] = File(...),
    db: Session = Depends(get_db)
):
    """Upload and parse multiple resumes"""
    candidates = []
    
    for file in files:
        # Validate file
        if not file.filename.endswith(".pdf"):
            continue
        
        # Save file
        file_id = str(uuid.uuid4())
        file_path = os.path.join(settings.UPLOAD_DIR, f"{file_id}_{file.filename}")
        
        with open(file_path, "wb") as f:
            content = await file.read()
            f.write(content)
        
        # Parse resume
        parsed_data = await ResumeParser.parse_resume(file_path)
        
        # Create candidate
        candidate = Candidate(
            name=parsed_data.get("name", "Unknown"),
            email=parsed_data.get("email"),
            phone=parsed_data.get("phone"),
            location=parsed_data.get("location"),
            linkedin=parsed_data.get("linkedin"),
            github=parsed_data.get("github"),
            total_experience=parsed_data.get("total_experience"),
            current_company=parsed_data.get("current_company"),
            current_designation=parsed_data.get("current_designation"),
            education=parsed_data.get("education"),
            skills=parsed_data.get("skills"),
            certifications=parsed_data.get("certifications"),
            projects=parsed_data.get("projects"),
            profile_summary=parsed_data.get("profile_summary"),
            resume_path=file_path,
            resume_file_name=file.filename,
            upload_date=datetime.utcnow()
        )
        
        db.add(candidate)
        db.commit()
        db.refresh(candidate)
        candidates.append(candidate)
    
    return candidates

@router.get("/", response_model=List[CandidateResponse])
def get_candidates(
    skip: int = Query(0, ge=0),
    limit: int = Query(50, le=100),
    search: str = None,
    db: Session = Depends(get_db)
):
    """Get all candidates with pagination and search"""
    query = db.query(Candidate)
    
    if search:
        search_filter = f"%{search}%"
        query = query.filter(
            (Candidate.name.ilike(search_filter)) |
            (Candidate.email.ilike(search_filter)) |
            (Candidate.current_company.ilike(search_filter))
        )
    
    candidates = query.order_by(Candidate.upload_date.desc()).offset(skip).limit(limit).all()
    return candidates

@router.get("/{candidate_id}", response_model=CandidateResponse)
def get_candidate(candidate_id: int, db: Session = Depends(get_db)):
    """Get single candidate by ID"""
    candidate = db.query(Candidate).filter(Candidate.id == candidate_id).first()
    if not candidate:
        raise HTTPException(status_code=404, detail="Candidate not found")
    return candidate

@router.post("/match")
async def match_candidates(
    request: JobMatchRequest,
    db: Session = Depends(get_db)
):
    """Match candidates against job description"""
    candidates = db.query(Candidate).all()
    matched = await AIMatcher.match_candidates(candidates, request)
    return matched

@router.delete("/{candidate_id}")
def delete_candidate(candidate_id: int, db: Session = Depends(get_db)):
    """Delete candidate"""
    candidate = db.query(Candidate).filter(Candidate.id == candidate_id).first()
    if not candidate:
        raise HTTPException(status_code=404, detail="Candidate not found")
    
    # Delete resume file
    if candidate.resume_path and os.path.exists(candidate.resume_path):
        os.remove(candidate.resume_path)
    
    db.delete(candidate)
    db.commit()
    return {"message": "Candidate deleted successfully"}
```

### 8. app/api/export.py
```python
from fastapi import APIRouter, Depends, Response
from fastapi.responses import StreamingResponse
from sqlalchemy.orm import Session
from app.db.database import get_db
from app.models.candidate import Candidate
import pandas as pd
import io

router = APIRouter()

@router.get("/excel")
def export_to_excel(db: Session = Depends(get_db)):
    """Export all candidates to Excel"""
    candidates = db.query(Candidate).all()
    
    data = []
    for c in candidates:
        data.append({
            "Candidate ID": c.id,
            "Full Name": c.name,
            "Email": c.email,
            "Phone": c.phone,
            "Location": c.location,
            "LinkedIn": c.linkedin,
            "GitHub": c.github,
            "Total Experience": c.total_experience,
            "Current Company": c.current_company,
            "Current Designation": c.current_designation,
            "Skills": ", ".join(c.skills) if c.skills else "",
            "Match Score": c.match_score,
            "Status": c.status,
            "Resume File": c.resume_file_name,
            "Upload Date": c.upload_date.strftime("%Y-%m-%d %H:%M:%S") if c.upload_date else ""
        })
    
    df = pd.DataFrame(data)
    
    # Create Excel file in memory
    output = io.BytesIO()
    with pd.ExcelWriter(output, engine="openpyxl") as writer:
        df.to_excel(writer, index=False, sheet_name="Candidates")
    
    output.seek(0)
    
    return StreamingResponse(
        output,
        media_type="application/vnd.openxmlformats-officedocument.spreadsheetml.sheet",
        headers={"Content-Disposition": "attachment; filename=candidates_export.xlsx"}
    )

@router.get("/csv")
def export_to_csv(db: Session = Depends(get_db)):
    """Export all candidates to CSV"""
    candidates = db.query(Candidate).all()
    
    data = []
    for c in candidates:
        data.append({
            "Candidate ID": c.id,
            "Full Name": c.name,
            "Email": c.email,
            "Phone": c.phone,
            "Skills": ", ".join(c.skills) if c.skills else "",
            "Experience": c.total_experience,
            "Company": c.current_company
        })
    
    df = pd.DataFrame(data)
    
    output = io.StringIO()
    df.to_csv(output, index=False)
    
    return Response(
        content=output.getvalue(),
        media_type="text/csv",
        headers={"Content-Disposition": "attachment; filename=candidates.csv"}
    )
```

## FRONTEND (React + TypeScript + Material-UI)

### Package.json for Frontend
```json
{
  "name": "talentfor-hr-frontend",
  "version": "1.0.0",
  "private": true,
  "dependencies": {
    "@mui/material": "^5.15.0",
    "@mui/icons-material": "^5.15.0",
    "@emotion/react": "^11.11.1",
    "@emotion/styled": "^11.11.0",
    "ag-grid-react": "^31.0.0",
    "ag-grid-community": "^31.0.0",
    "axios": "^1.6.2",
    "react": "^18.2.0",
    "react-dom": "^18.2.0",
    "react-dropzone": "^14.2.3",
    "react-router-dom": "^6.20.0",
    "react-scripts": "5.0.1"
  },
  "scripts": {
    "start": "react-scripts start",
    "build": "react-scripts build",
    "test": "react-scripts test",
    "eject": "react-scripts eject"
  }
}
```

### Frontend src/services/api.ts
```typescript
import axios from "axios";

const API_URL = process.env.REACT_APP_API_URL || "http://localhost:8000";

const apiClient = axios.create({
  baseURL: API_URL,
  headers: {
    "Content-Type": "application/json",
  },
});

export const candidatesAPI = {
  uploadResumes: async (files: File[]) => {
    const formData = new FormData();
    files.forEach(file => formData.append("files", file));
    
    const response = await apiClient.post("/api/candidates/upload", formData, {
      headers: { "Content-Type": "multipart/form-data" },
    });
    return response.data;
  },
  
  getCandidates: async (skip = 0, limit = 50, search = "") => {
    const response = await apiClient.get("/api/candidates", {
      params: { skip, limit, search },
    });
    return response.data;
  },
  
  getCandidate: async (id: number) => {
    const response = await apiClient.get(`/api/candidates/${id}`);
    return response.data;
  },
  
  deleteCandidate: async (id: number) => {
    const response = await apiClient.delete(`/api/candidates/${id}`);
    return response.data;
  },
  
  exportToExcel: () => {
    window.open(`${API_URL}/api/export/excel`, "_blank");
  },
  
  exportToCSV: () => {
    window.open(`${API_URL}/api/export/csv`, "_blank");
  },
};

export default apiClient;
```

