from fastapi import FastAPI
from fastapi.middleware.cors import CORSMiddleware
from app.core.config import settings
from app.api import candidates, auth, export, resume_parser_routes

app = FastAPI(
    title="TalentFor HR API",
    description="AI-Powered Resume Parsing and Recruitment Tool",
    version="1.0.0"
)

app.add_middleware(
    CORSMiddleware,
    allow_origins=settings.CORS_ORIGINS,
    allow_credentials=True,
    allow_methods=["*"],
    allow_headers=["*"]
)

app.include_router(auth.router, prefix="/api/auth", tags=["Authentication"])
app.include_router(candidates.router, prefix="/api/candidates", tags=["Candidates"])
app.include_router(export.router, prefix="/api/export", tags=["Export"])
app.include_router(resume_parser_routes.router, prefix="/api/resume-parser", tags=["Resume Parser"])

@app.get("/")
def root():
    return {"message": "TalentFor HR API is running"}
