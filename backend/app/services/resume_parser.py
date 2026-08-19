import re
import pdfplumber
from typing import Dict, List, Optional
from datetime import datetime
import os

class ResumeParser:
    """Parse resume PDFs and extract structured information using pattern matching."""
    
    def __init__(self):
        # Common patterns for resume parsing
        self.email_pattern = r'[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\.[a-zA-Z]{2,}'
        self.phone_pattern = r'(?:\+?\d{1,3}[-.\s]?)?(?:\(?\d{1,4}\)?[-.\s]?)?\d{1,4}[-.\s]?\d{1,4}[-.\s]?\d{1,9}'
        self.linkedin_pattern = r'(?:https?://)?(?:www\.)?linkedin\.com/in/[a-zA-Z0-9-]+'
        self.github_pattern = r'(?:https?://)?(?:www\.)?github\.com/[a-zA-Z0-9-]+'
        
        # Skills keywords (common tech skills)
        self.tech_skills = [
            'python', 'java', 'javascript', 'typescript', 'react', 'angular', 'vue',
            'node', 'nodejs', 'express', 'django', 'flask', 'fastapi', 'spring',
            'sql', 'mysql', 'postgresql', 'mongodb', 'redis', 'docker', 'kubernetes',
            'aws', 'azure', 'gcp', 'git', 'ci/cd', 'jenkins', 'terraform',
            'html', 'css', 'sass', 'bootstrap', 'tailwind', 'rest', 'api', 'graphql',
            'machine learning', 'ml', 'ai', 'deep learning', 'tensorflow', 'pytorch',
            'data science', 'pandas', 'numpy', 'scikit-learn', 'nlp', 'opencv',
            'c++', 'c#', '.net', 'ruby', 'rails', 'php', 'laravel', 'go', 'rust',
            'kotlin', 'swift', 'flutter', 'react native', 'android', 'ios',
            'agile', 'scrum', 'jira', 'confluence', 'microservices', 'devops'
        ]
        
    def parse_pdf(self, file_path: str) -> Dict:
        """Parse a PDF resume and extract information."""
        try:
            text = self._extract_text_from_pdf(file_path)
            
            parsed_data = {
                'name': self._extract_name(text),
                'email': self._extract_email(text),
                'phone': self._extract_phone(text),
                'linkedin': self._extract_linkedin(text),
                'github': self._extract_github(text),
                'skills': self._extract_skills(text),
                'education': self._extract_education(text),
                'experience_years': self._extract_experience_years(text),
                'summary': self._extract_summary(text),
                'filename': os.path.basename(file_path),
                'parsed_date': datetime.now().isoformat()
            }
            
            return parsed_data
            
        except Exception as e:
            raise Exception(f"Error parsing PDF: {str(e)}")
    
    def _extract_text_from_pdf(self, file_path: str) -> str:
        """Extract text from PDF file."""
        text = ""
        with pdfplumber.open(file_path) as pdf:
            for page in pdf.pages:
                text += page.extract_text() or ""
        return text
    
    def _extract_name(self, text: str) -> Optional[str]:
        """Extract name (usually first line or after certain keywords)."""
        lines = [line.strip() for line in text.split('\n') if line.strip()]
        if not lines:
            return None
            
        # First non-empty line is often the name
        first_line = lines[0]
        
        # Filter out common headers/keywords
        skip_keywords = ['resume', 'curriculum vitae', 'cv', 'profile', 'contact']
        if any(keyword in first_line.lower() for keyword in skip_keywords):
            return lines[1] if len(lines) > 1 else first_line
        
        # Name is usually 2-4 words, all capitalized or title case
        words = first_line.split()
        if 2 <= len(words) <= 4:
            return first_line
        
        return first_line[:100]  # Limit length
    
    def _extract_email(self, text: str) -> Optional[str]:
        """Extract email address."""
        match = re.search(self.email_pattern, text, re.IGNORECASE)
        return match.group(0) if match else None
    
    def _extract_phone(self, text: str) -> Optional[str]:
        """Extract phone number."""
        matches = re.findall(self.phone_pattern, text)
        if matches:
            # Filter out numbers that are too short (likely not phone numbers)
            valid_phones = [p for p in matches if len(re.sub(r'\D', '', p)) >= 10]
            return valid_phones[0] if valid_phones else None
        return None
    
    def _extract_linkedin(self, text: str) -> Optional[str]:
        """Extract LinkedIn URL."""
        match = re.search(self.linkedin_pattern, text, re.IGNORECASE)
        return match.group(0) if match else None
    
    def _extract_github(self, text: str) -> Optional[str]:
        """Extract GitHub URL."""
        match = re.search(self.github_pattern, text, re.IGNORECASE)
        return match.group(0) if match else None
    
    def _extract_skills(self, text: str) -> List[str]:
        """Extract skills from text."""
        text_lower = text.lower()
        found_skills = []
        
        for skill in self.tech_skills:
            # Use word boundary to avoid partial matches
            pattern = r'\b' + re.escape(skill) + r'\b'
            if re.search(pattern, text_lower):
                # Preserve original case from skill list
                found_skills.append(skill.title())
        
        # Remove duplicates while preserving order
        seen = set()
        unique_skills = []
        for skill in found_skills:
            if skill.lower() not in seen:
                seen.add(skill.lower())
                unique_skills.append(skill)
        
        return unique_skills
    
    def _extract_education(self, text: str) -> Optional[str]:
        """Extract education information."""
        education_keywords = [
            r'bachelor(?:\'s)?(?:\s+of)?(?:\s+science)?(?:\s+in)?',
            r'master(?:\'s)?(?:\s+of)?(?:\s+science)?(?:\s+in)?',
            r'phd|ph\.d\.',
            r'mba',
            r'b\.?tech|b\.?e\.|b\.?sc',
            r'm\.?tech|m\.?e\.|m\.?sc',
            r'diploma',
            r'degree'
        ]
        
        education_info = []
        lines = text.split('\n')
        
        for i, line in enumerate(lines):
            for keyword in education_keywords:
                if re.search(keyword, line, re.IGNORECASE):
                    # Get this line and potentially next line for complete info
                    edu_text = line.strip()
                    if i + 1 < len(lines):
                        next_line = lines[i + 1].strip()
                        if next_line and len(next_line) < 100:
                            edu_text += " " + next_line
                    education_info.append(edu_text)
                    break
        
        return "; ".join(education_info[:3]) if education_info else None
    
    def _extract_experience_years(self, text: str) -> Optional[str]:
        """Extract years of experience."""
        # Look for explicit mentions
        patterns = [
            r'(\d+)\+?\s+years?\s+(?:of\s+)?experience',
            r'experience[:\s]+(\d+)\+?\s+years?',
        ]
        
        for pattern in patterns:
            match = re.search(pattern, text, re.IGNORECASE)
            if match:
                return match.group(1) + " years"
        
        # Try to estimate from dates
        year_pattern = r'\b(19|20)\d{2}\b'
        years = re.findall(year_pattern, text)
        
        if len(years) >= 2:
            years_int = [int(y) for y in years]
            current_year = datetime.now().year
            # Find earliest reasonable year (not future, not too old)
            valid_years = [y for y in years_int if 1990 <= y <= current_year]
            if valid_years:
                earliest = min(valid_years)
                estimated = current_year - earliest
                if 0 < estimated <= 50:  # Reasonable range
                    return f"~{estimated} years"
        
        return None
    
    def _extract_summary(self, text: str) -> Optional[str]:
        """Extract professional summary or objective."""
        summary_keywords = [
            'summary', 'professional summary', 'profile', 'objective',
            'career objective', 'about me', 'overview'
        ]
        
        lines = text.split('\n')
        
        for i, line in enumerate(lines):
            line_lower = line.lower().strip()
            
            # Check if line contains summary keyword
            for keyword in summary_keywords:
                if keyword in line_lower and len(line_lower) < 50:
                    # Found header, get next few lines as summary
                    summary_lines = []
                    for j in range(i + 1, min(i + 6, len(lines))):
                        next_line = lines[j].strip()
                        if next_line:
                            # Stop at next section header
                            if any(h in next_line.lower() for h in ['experience', 'education', 'skills', 'projects']):
                                break
                            summary_lines.append(next_line)
                    
                    if summary_lines:
                        summary = " ".join(summary_lines)
                        return summary[:500]  # Limit length
        
        # If no explicit summary, take first meaningful paragraph
        for line in lines[:10]:
            line = line.strip()
            if len(line) > 100 and not re.search(self.email_pattern, line):
                return line[:500]
        
        return None
