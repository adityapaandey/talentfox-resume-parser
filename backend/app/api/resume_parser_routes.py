from fastapi import APIRouter, UploadFile, File, HTTPException
from fastapi.responses import FileResponse
from typing import List
import os
import shutil
from datetime import datetime
import tempfile

from app.services.resume_parser import ResumeParser
from app.services.excel_exporter import ExcelExporter

router = APIRouter()

# Initialize services
resume_parser = ResumeParser()
excel_exporter = ExcelExporter()

# Storage for parsed resumes in current session
parsed_resumes_storage = []

UPLOAD_DIR = "uploads/resumes"
EXPORT_DIR = "uploads/exports"

# Create directories if they don't exist
os.makedirs(UPLOAD_DIR, exist_ok=True)
os.makedirs(EXPORT_DIR, exist_ok=True)


@router.post("/parse")
async def parse_resume(file: UploadFile = File(...)):
    """
    Parse a single PDF resume and return extracted data.
    """
    if not file.filename.endswith('.pdf'):
        raise HTTPException(status_code=400, detail="Only PDF files are supported")
    
    # Save uploaded file temporarily
    file_path = os.path.join(UPLOAD_DIR, file.filename)
    
    try:
        with open(file_path, "wb") as buffer:
            shutil.copyfileobj(file.file, buffer)
        
        # Parse the resume
        parsed_data = resume_parser.parse_pdf(file_path)
        
        # Store in session
        parsed_resumes_storage.append(parsed_data)
        
        return {
            "success": True,
            "data": parsed_data,
            "message": f"Successfully parsed {file.filename}"
        }
        
    except Exception as e:
        raise HTTPException(status_code=500, detail=f"Error parsing resume: {str(e)}")
    
    finally:
        # Clean up uploaded file
        if os.path.exists(file_path):
            try:
                os.remove(file_path)
            except:
                pass


@router.post("/parse-multiple")
async def parse_multiple_resumes(files: List[UploadFile] = File(...)):
    """
    Parse multiple PDF resumes and return extracted data.
    """
    results = []
    errors = []
    
    for file in files:
        if not file.filename.endswith('.pdf'):
            errors.append({"filename": file.filename, "error": "Only PDF files are supported"})
            continue
        
        file_path = os.path.join(UPLOAD_DIR, file.filename)
        
        try:
            # Save uploaded file
            with open(file_path, "wb") as buffer:
                shutil.copyfileobj(file.file, buffer)
            
            # Parse the resume
            parsed_data = resume_parser.parse_pdf(file_path)
            
            # Store in session
            parsed_resumes_storage.append(parsed_data)
            
            results.append({
                "filename": file.filename,
                "data": parsed_data,
                "success": True
            })
            
        except Exception as e:
            errors.append({
                "filename": file.filename,
                "error": str(e)
            })
        
        finally:
            # Clean up uploaded file
            if os.path.exists(file_path):
                try:
                    os.remove(file_path)
                except:
                    pass
    
    return {
        "success": True,
        "parsed_count": len(results),
        "error_count": len(errors),
        "results": results,
        "errors": errors
    }


@router.get("/export-excel")
async def export_to_excel():
    """
    Export all parsed resumes to Excel file.
    """
    if not parsed_resumes_storage:
        raise HTTPException(status_code=400, detail="No parsed resumes to export")
    
    try:
        # Generate filename with timestamp
        timestamp = datetime.now().strftime("%Y%m%d_%H%M%S")
        filename = f"parsed_resumes_{timestamp}.xlsx"
        output_path = os.path.join(EXPORT_DIR, filename)
        
        # Export to Excel
        excel_exporter.export_to_excel(parsed_resumes_storage, output_path)
        
        # Return file for download
        return FileResponse(
            path=output_path,
            filename=filename,
            media_type="application/vnd.openxmlformats-officedocument.spreadsheetml.sheet",
            headers={
                "Content-Disposition": f"attachment; filename={filename}"
            }
        )
        
    except Exception as e:
        raise HTTPException(status_code=500, detail=f"Error exporting to Excel: {str(e)}")


@router.get("/parsed-resumes")
async def get_parsed_resumes():
    """
    Get all parsed resumes in current session.
    """
    return {
        "success": True,
        "count": len(parsed_resumes_storage),
        "resumes": parsed_resumes_storage
    }


@router.delete("/clear")
async def clear_parsed_resumes():
    """
    Clear all parsed resumes from current session.
    """
    parsed_resumes_storage.clear()
    return {
        "success": True,
        "message": "All parsed resumes cleared"
    }


@router.get("/health")
async def health_check():
    """
    Health check endpoint.
    """
    return {
        "status": "healthy",
        "service": "Resume Parser",
        "parsed_count": len(parsed_resumes_storage)
    }
