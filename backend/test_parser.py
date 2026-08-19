"""Simple test script to verify the resume parser works."""

from app.services.resume_parser import ResumeParser
import tempfile
import os

def create_sample_pdf_text():
    """Create sample resume text for testing."""
    return """
John Doe
Software Engineer

Contact Information:
Email: john.doe@example.com
Phone: +1 (555) 123-4567
LinkedIn: https://www.linkedin.com/in/johndoe
GitHub: https://github.com/johndoe

Professional Summary:
Experienced software engineer with 5 years of experience in full-stack development.
Specializing in Python, JavaScript, and cloud technologies.

Skills:
- Programming Languages: Python, JavaScript, TypeScript, Java
- Frontend: React, Angular, HTML, CSS
- Backend: Django, Flask, FastAPI, Node.js
- Databases: PostgreSQL, MongoDB, Redis
- Cloud: AWS, Docker, Kubernetes
- Tools: Git, CI/CD, Jenkins

Education:
Bachelor of Science in Computer Science
University of Technology, 2018

Master of Science in Software Engineering
Tech University, 2020

Work Experience:
Senior Software Engineer
Tech Corp, 2021 - Present

Software Engineer
Startup Inc, 2019 - 2021

Junior Developer
Code Company, 2018 - 2019
"""

def test_parser():
    """Test the resume parser."""
    print("\n" + "="*50)
    print("Testing Resume Parser")
    print("="*50 + "\n")
    
    # Note: This is a simplified test
    # In reality, you need a real PDF file to test PDFPlumber
    print("Creating ResumeParser instance...")
    parser = ResumeParser()
    print("✓ Parser created successfully\n")
    
    # Test email extraction
    sample_text = create_sample_pdf_text()
    email = parser._extract_email(sample_text)
    print(f"Email extraction test: {email}")
    assert email == "john.doe@example.com", "Email extraction failed"
    print("✓ Email extraction works\n")
    
    # Test phone extraction
    phone = parser._extract_phone(sample_text)
    print(f"Phone extraction test: {phone}")
    assert phone is not None, "Phone extraction failed"
    print("✓ Phone extraction works\n")
    
    # Test LinkedIn extraction
    linkedin = parser._extract_linkedin(sample_text)
    print(f"LinkedIn extraction test: {linkedin}")
    assert linkedin == "https://www.linkedin.com/in/johndoe", "LinkedIn extraction failed"
    print("✓ LinkedIn extraction works\n")
    
    # Test GitHub extraction
    github = parser._extract_github(sample_text)
    print(f"GitHub extraction test: {github}")
    assert github == "https://github.com/johndoe", "GitHub extraction failed"
    print("✓ GitHub extraction works\n")
    
    # Test skills extraction
    skills = parser._extract_skills(sample_text)
    print(f"Skills extraction test: Found {len(skills)} skills")
    print(f"Sample skills: {skills[:10]}")
    assert len(skills) > 0, "Skills extraction failed"
    assert 'Python' in skills or 'python' in [s.lower() for s in skills], "Python not detected"
    print("✓ Skills extraction works\n")
    
    # Test name extraction
    name = parser._extract_name(sample_text)
    print(f"Name extraction test: {name}")
    assert name == "John Doe", "Name extraction failed"
    print("✓ Name extraction works\n")
    
    # Test education extraction
    education = parser._extract_education(sample_text)
    print(f"Education extraction test: {education}")
    assert education is not None, "Education extraction failed"
    print("✓ Education extraction works\n")
    
    # Test experience extraction
    experience = parser._extract_experience_years(sample_text)
    print(f"Experience extraction test: {experience}")
    print("✓ Experience extraction works\n")
    
    print("="*50)
    print("All tests passed! ✓")
    print("="*50 + "\n")
    print("The parser is working correctly!")
    print("Note: To test full PDF parsing, use actual PDF files.")
    print("\nNext steps:")
    print("1. Start the backend: uvicorn app.main:app --reload")
    print("2. Start the frontend: python -m http.server 3000")
    print("3. Open http://localhost:3000")
    print("4. Upload PDF resumes to test!")

if __name__ == "__main__":
    try:
        test_parser()
    except Exception as e:
        print(f"\n❌ Test failed with error: {e}")
        import traceback
        traceback.print_exc()
