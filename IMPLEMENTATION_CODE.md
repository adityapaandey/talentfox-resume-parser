# TalentFor HR - Complete Source Code Implementation

## BACKEND IMPLEMENTATION

### 1. app/core/config.py
```python
from pydantic_settings import BaseSettings
from typing import List
import os

class Settings(BaseSettings):
    # Database
    DATABASE_URL: str = "postgresql://postgres:password@localhost:5432/talentfor_hr"
    
    # Security
    SECRET_KEY: str = "your-secret-key-change-in-production-09876543210"
    ALGORITHM: str = "HS256"
    ACCESS_TOKEN_EXPIRE_MINUTES: int = 30
    
    # OpenAI
    OPENAI_API_KEY: str = "sk-your-openai-api-key"
    
    # Upload Settings
    UPLOAD_DIR: str = "./uploads"
    MAX_FILE_SIZE: int = 10485760  # 10MB
    ALLOWED_EXTENSIONS: List[str] = [".pdf"]
    
    # CORS
    CORS_ORIGINS: List[str] = ["http://localhost:3000", "http://127.0.0.1:3000"]
    
    class Config:
        env_file = ".env"
        case_sensitive = True

settings = Settings()

# Ensure upload directory exists
os.makedirs(settings.UPLOAD_DIR, exist_ok=True)
```

### 2. app/db/database.py
```python
from sqlalchemy import create_engine
from sqlalchemy.ext.declarative import declarative_base
from sqlalchemy.orm import sessionmaker
from app.core.config import settings

engine = create_engine(settings.DATABASE_URL)
SessionLocal = sessionmaker(autocommit=False, autoflush=False, bind=engine)
Base = declarative_base()

def get_db():
    db = SessionLocal()
    try:
        yield db
    finally:
        db.close()

def init_db():
    Base.metadata.create_all(bind=engine)
```

### 3. app/models/candidate.py
```python
from sqlalchemy import Column, Integer, String, Float, DateTime, JSON, Text
from datetime import datetime
from app.db.database import Base

class Candidate(Base):
    __tablename__ = "candidates"
    
    id = Column(Integer, primary_key=True, index=True)
    name = Column(String(255), nullable=False)
    email = Column(String(255), index=True)
    phone = Column(String(50))
    location = Column(String(255))
    linkedin = Column(String(500))
    github = Column(String(500))
    total_experience = Column(Float)
    current_company = Column(String(255))
    current_designation = Column(String(255))
    education = Column(JSON)
    skills = Column(JSON)
    certifications = Column(JSON)
    projects = Column(JSON)
    profile_summary = Column(Text)
    resume_path = Column(String(500))
    resume_file_name = Column(String(255))
    match_score = Column(Float, default=0.0)
    status = Column(String(50), default="new")
    upload_date = Column(DateTime, default=datetime.utcnow)
    created_at = Column(DateTime, default=datetime.utcnow)
    updated_at = Column(DateTime, default=datetime.utcnow, onupdate=datetime.utcnow)
```

### 4. app/models/user.py
```python
from sqlalchemy import Column, Integer, String, DateTime, Boolean
from datetime import datetime
from app.db.database import Base

class User(Base):
    __tablename__ = "users"
    
    id = Column(Integer, primary_key=True, index=True)
    email = Column(String(255), unique=True, index=True, nullable=False)
    full_name = Column(String(255))
    hashed_password = Column(String(255), nullable=False)
    role = Column(String(50), default="recruiter")  # admin, recruiter
    is_active = Column(Boolean, default=True)
    created_at = Column(DateTime, default=datetime.utcnow)
```

### 5. app/schemas/candidate.py
```python
from pydantic import BaseModel, EmailStr
from typing import Optional, List, Dict, Any
from datetime import datetime

class CandidateBase(BaseModel):
    name: str
    email: Optional[EmailStr] = None
    phone: Optional[str] = None
    location: Optional[str] = None
    linkedin: Optional[str] = None
    github: Optional[str] = None

class CandidateCreate(CandidateBase):
    pass

class CandidateResponse(CandidateBase):
    id: int
    total_experience: Optional[float] = None
    current_company: Optional[str] = None
    current_designation: Optional[str] = None
    education: Optional[Dict[str, Any]] = None
    skills: Optional[List[str]] = None
    certifications: Optional[List[Dict[str, str]]] = None
    projects: Optional[List[Dict[str, str]]] = None
    profile_summary: Optional[str] = None
    resume_file_name: Optional[str] = None
    match_score: Optional[float] = 0.0
    status: Optional[str] = "new"
    upload_date: Optional[datetime] = None
    
    class Config:
        from_attributes = True

class JobMatchRequest(BaseModel):
    job_description: str
    required_skills: Optional[List[str]] = []
    min_experience: Optional[float] = 0
```

