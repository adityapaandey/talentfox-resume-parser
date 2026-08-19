# TalentFor HR - Services & API Implementation (Part 2)

## BACKEND SERVICES

### 6. app/services/resume_parser.py
```python
import fitz  # PyMuPDF
import re
import spacy
from typing import Dict, Any, List, Optional
import openai
from app.core.config import settings
import json

openai.api_key = settings.OPENAI_API_KEY

# Load spaCy model
try:
    nlp = spacy.load("en_core_web_sm")
except:
    print("Download spaCy model: python -m spacy download en_core_web_sm")
    nlp = None

class ResumeParser:
    
    @staticmethod
    def extract_text_from_pdf(pdf_path: str) -> str:
        """Extract text from PDF using PyMuPDF"""
        text = ""
        try:
            doc = fitz.open(pdf_path)
            for page in doc:
                text += page.get_text()
            doc.close()
        except Exception as e:
            print(f"Error extracting text: {e}")
        return text
    
    @staticmethod
    def extract_email(text: str) -> Optional[str]:
        """Extract email using regex"""
        email_pattern = r"\b[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\.[A-Z|a-z]{2,}\b"
        matches = re.findall(email_pattern, text)
        return matches[0] if matches else None
    
    @staticmethod
    def extract_phone(text: str) -> Optional[str]:
        """Extract phone number"""
        phone_pattern = r"\+?\d[\d -]{8,12}\d"
        matches = re.findall(phone_pattern, text)
        return matches[0] if matches else None
    
    @staticmethod
    def extract_linkedin(text: str) -> Optional[str]:
        """Extract LinkedIn URL"""
        linkedin_pattern = r"linkedin\.com/in/[\w-]+"
        matches = re.findall(linkedin_pattern, text, re.IGNORECASE)
        return f"https://{matches[0]}" if matches else None
    
    @staticmethod
    def extract_github(text: str) -> Optional[str]:
        """Extract GitHub URL"""
        github_pattern = r"github\.com/[\w-]+"
        matches = re.findall(github_pattern, text, re.IGNORECASE)
        return f"https://{matches[0]}" if matches else None
    
    @staticmethod
    def extract_skills(text: str) -> List[str]:
        """Extract technical skills"""
        common_skills = [
            "Python", "Java", "JavaScript", "TypeScript", "C++", "C#", "Go", "Rust",
            "React", "Angular", "Vue", "Node.js", "Express", "Django", "Flask",
            "Spring Boot", "FastAPI", "ASP.NET",
            "AWS", "Azure", "GCP", "Docker", "Kubernetes", "Jenkins",
            "PostgreSQL", "MySQL", "MongoDB", "Redis", "Elasticsearch",
            "Microservices", "REST API", "GraphQL", "gRPC",
            "Git", "CI/CD", "Agile", "Scrum", "DevOps",
            "Machine Learning", "AI", "TensorFlow", "PyTorch",
            "HTML", "CSS", "Sass", "Tailwind", "Bootstrap",
            "SQL", "NoSQL", "Linux", "Bash", "PowerShell"
        ]
        
        found_skills = []
        text_lower = text.lower()
        
        for skill in common_skills:
            if skill.lower() in text_lower:
                found_skills.append(skill)
        
        return list(set(found_skills))
    
    @staticmethod
    async def parse_with_openai(text: str) -> Dict[str, Any]:
        """Use OpenAI to parse resume with structured output"""
        try:
            prompt = f"""Parse the following resume and extract information in JSON format:
            
Resume Text:
{text[:4000]}

Extract:
- name (full name)
- email
- phone
- location
- linkedin
- github
- total_experience (in years as float)
- current_company
- current_designation
- education (array of {{degree, specialization, college, year}})
- skills (array of technical skills)
- certifications (array of {{name, organization}})
- projects (array of {{name, description}})
- profile_summary

Return ONLY valid JSON, no additional text."""
            
            response = openai.ChatCompletion.create(
                model="gpt-3.5-turbo",
                messages=[
                    {"role": "system", "content": "You are a resume parser. Return only valid JSON."},
                    {"role": "user", "content": prompt}
                ],
                temperature=0.3,
                max_tokens=1500
            )
            
            result = response.choices[0].message.content
            parsed_data = json.loads(result)
            return parsed_data
            
        except Exception as e:
            print(f"OpenAI parsing error: {e}")
            return {}
    
    @classmethod
    async def parse_resume(cls, pdf_path: str) -> Dict[str, Any]:
        """Main method to parse resume"""
        # Extract text
        text = cls.extract_text_from_pdf(pdf_path)
        
        if not text:
            return {"error": "Could not extract text from PDF"}
        
        # Try OpenAI first for best results
        try:
            parsed_data = await cls.parse_with_openai(text)
            
            # Fallback to regex if OpenAI fails
            if not parsed_data.get("email"):
                parsed_data["email"] = cls.extract_email(text)
            if not parsed_data.get("phone"):
                parsed_data["phone"] = cls.extract_phone(text)
            if not parsed_data.get("linkedin"):
                parsed_data["linkedin"] = cls.extract_linkedin(text)
            if not parsed_data.get("github"):
                parsed_data["github"] = cls.extract_github(text)
            if not parsed_data.get("skills"):
                parsed_data["skills"] = cls.extract_skills(text)
            
            return parsed_data
            
        except Exception as e:
            print(f"Parsing error: {e}")
            # Full fallback to regex
            return {
                "email": cls.extract_email(text),
                "phone": cls.extract_phone(text),
                "linkedin": cls.extract_linkedin(text),
                "github": cls.extract_github(text),
                "skills": cls.extract_skills(text),
                "profile_summary": text[:500]
            }
```

