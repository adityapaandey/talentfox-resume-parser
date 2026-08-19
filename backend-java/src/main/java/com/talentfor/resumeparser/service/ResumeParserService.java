package com.talentfor.resumeparser.service;

import com.talentfor.resumeparser.model.ParsedResume;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.Year;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * Service for parsing PDF resumes using pattern matching
 * No LLM API required - Pure regex and keyword matching
 */
@Service
public class ResumeParserService {

    // Regex patterns
    private static final Pattern EMAIL_PATTERN = 
        Pattern.compile("[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}");
    
    private static final Pattern PHONE_PATTERN = 
        Pattern.compile("(?:\\+?\\d{1,3}[-\\.\\s]?)?(?:\\(?\\d{1,4}\\)?[-\\.\\s]?)?\\d{1,4}[-\\.\\s]?\\d{1,4}[-\\.\\s]?\\d{1,9}");
    
    private static final Pattern LINKEDIN_PATTERN = 
        Pattern.compile("(?:https?://)?(?:www\\.)?linkedin\\.com/in/[a-zA-Z0-9-]+", Pattern.CASE_INSENSITIVE);
    
    private static final Pattern GITHUB_PATTERN = 
        Pattern.compile("(?:https?://)?(?:www\\.)?github\\.com/[a-zA-Z0-9-]+", Pattern.CASE_INSENSITIVE);
    
    private static final Pattern EXPERIENCE_PATTERN = 
        Pattern.compile("(\\d+)\\+?\\s+years?\\s+(?:of\\s+)?experience", Pattern.CASE_INSENSITIVE);
    
    private static final Pattern YEAR_PATTERN = 
        Pattern.compile("\\b(19|20)\\d{2}\\b");

    // Technical skills dictionary
    private static final List<String> TECH_SKILLS = Arrays.asList(
        "python", "java", "javascript", "typescript", "react", "angular", "vue",
        "node", "nodejs", "express", "django", "flask", "fastapi", "spring",
        "sql", "mysql", "postgresql", "mongodb", "redis", "docker", "kubernetes",
        "aws", "azure", "gcp", "git", "ci/cd", "jenkins", "terraform",
        "html", "css", "sass", "bootstrap", "tailwind", "rest", "api", "graphql",
        "machine learning", "ml", "ai", "deep learning", "tensorflow", "pytorch",
        "data science", "pandas", "numpy", "scikit-learn", "nlp", "opencv",
        "c++", "c#", ".net", "ruby", "rails", "php", "laravel", "go", "rust",
        "kotlin", "swift", "flutter", "react native", "android", "ios",
        "agile", "scrum", "jira", "confluence", "microservices", "devops"
    );

    // Education keywords
    private static final List<String> EDUCATION_KEYWORDS = Arrays.asList(
        "bachelor", "master", "phd", "ph.d.", "mba", "b.tech", "b.e.", "b.sc",
        "m.tech", "m.e.", "m.sc", "diploma", "degree"
    );

    /**
     * Parse a PDF file and extract resume information
     */
    public ParsedResume parsePdf(File pdfFile) throws IOException {
        String text = extractTextFromPdf(pdfFile);
        
        return ParsedResume.builder()
                .name(extractName(text))
                .email(extractEmail(text))
                .phone(extractPhone(text))
                .linkedin(extractLinkedIn(text))
                .github(extractGitHub(text))
                .skills(extractSkills(text))
                .education(extractEducation(text))
                .experienceYears(extractExperience(text))
                .summary(extractSummary(text))
                .filename(pdfFile.getName())
                .parsedDate(LocalDateTime.now())
                .build();
    }

    /**
     * Extract text from PDF using Apache PDFBox
     */
    private String extractTextFromPdf(File pdfFile) throws IOException {
        try (PDDocument document = PDDocument.load(pdfFile)) {
            PDFTextStripper stripper = new PDFTextStripper();
            return stripper.getText(document);
        }
    }

    /**
     * Extract name (usually first line)
     */
    private String extractName(String text) {
        String[] lines = text.split("\\n");
        for (String line : lines) {
            line = line.trim();
            if (line.isEmpty()) continue;
            
            // Skip common headers
            String lowerLine = line.toLowerCase();
            if (lowerLine.contains("resume") || lowerLine.contains("curriculum vitae") ||
                lowerLine.contains("cv") || lowerLine.contains("profile")) {
                continue;
            }
            
            // Name is usually 2-4 words
            String[] words = line.split("\\s+");
            if (words.length >= 2 && words.length <= 4) {
                return line.length() > 100 ? line.substring(0, 100) : line;
            }
        }
        return lines.length > 0 ? lines[0].substring(0, Math.min(100, lines[0].length())) : null;
    }

    /**
     * Extract email address
     */
    private String extractEmail(String text) {
        Matcher matcher = EMAIL_PATTERN.matcher(text);
        return matcher.find() ? matcher.group() : null;
    }

    /**
     * Extract phone number
     */
    private String extractPhone(String text) {
        Matcher matcher = PHONE_PATTERN.matcher(text);
        while (matcher.find()) {
            String phone = matcher.group();
            // Filter numbers with at least 10 digits
            long digitCount = phone.chars().filter(Character::isDigit).count();
            if (digitCount >= 10) {
                return phone;
            }
        }
        return null;
    }

    /**
     * Extract LinkedIn URL
     */
    private String extractLinkedIn(String text) {
        Matcher matcher = LINKEDIN_PATTERN.matcher(text);
        return matcher.find() ? matcher.group() : null;
    }

    /**
     * Extract GitHub URL
     */
    private String extractGitHub(String text) {
        Matcher matcher = GITHUB_PATTERN.matcher(text);
        return matcher.find() ? matcher.group() : null;
    }

    /**
     * Extract skills using keyword matching
     */
    private List<String> extractSkills(String text) {
        String lowerText = text.toLowerCase();
        Set<String> foundSkills = new LinkedHashSet<>();
        
        for (String skill : TECH_SKILLS) {
            Pattern pattern = Pattern.compile("\\b" + Pattern.quote(skill) + "\\b", 
                                            Pattern.CASE_INSENSITIVE);
            Matcher matcher = pattern.matcher(lowerText);
            if (matcher.find()) {
                // Capitalize first letter of each word
                String[] words = skill.split("\\s+");
                String capitalizedSkill = Arrays.stream(words)
                    .map(w -> w.substring(0, 1).toUpperCase() + w.substring(1))
                    .collect(Collectors.joining(" "));
                foundSkills.add(capitalizedSkill);
            }
        }
        
        return new ArrayList<>(foundSkills);
    }

    /**
     * Extract education information
     */
    private String extractEducation(String text) {
        String[] lines = text.split("\\n");
        List<String> educationInfo = new ArrayList<>();
        boolean inEducationSection = false;
        
        for (int i = 0; i < lines.length; i++) {
            String line = lines[i].trim();
            String lowerLine = line.toLowerCase();
            
            // Check for education section header
            if (lowerLine.matches("^education\\s*$") || 
                lowerLine.matches("^academic\\s*$") ||
                lowerLine.matches("^qualifications?\\s*$")) {
                inEducationSection = true;
                continue;
            }
            
            // Stop at next section
            if (inEducationSection && (lowerLine.matches("^experience\\s*$") ||
                lowerLine.matches("^work\\s+experience\\s*$") ||
                lowerLine.matches("^projects?\\s*$") ||
                lowerLine.matches("^skills?\\s*$"))) {
                break;
            }
            
            // Extract education entries
            if (inEducationSection && !line.isEmpty()) {
                // Add complete lines with degree info
                if (educationInfo.size() < 3 && line.length() > 5 && line.length() < 200) {
                    educationInfo.add(line);
                }
            } else {
                // Look for education keywords anywhere
                for (String keyword : EDUCATION_KEYWORDS) {
                    Pattern pattern = Pattern.compile("\\b" + keyword + "\\b", Pattern.CASE_INSENSITIVE);
                    if (pattern.matcher(line).find() && line.length() > 10 && line.length() < 200) {
                        if (!educationInfo.contains(line)) {
                            educationInfo.add(line);
                        }
                        break;
                    }
                }
            }
            
            if (educationInfo.size() >= 3) break;
        }
        
        if (educationInfo.isEmpty()) {
            return null;
        }
        
        // Clean and format education entries
        List<String> cleanedEducation = educationInfo.stream()
            .map(String::trim)
            .filter(s -> !s.isEmpty())
            .distinct()
            .limit(3)
            .collect(Collectors.toList());
        
        return String.join(" | ", cleanedEducation);
    }

    /**
     * Extract years of experience
     */
    private String extractExperience(String text) {
        // Try explicit mentions first
        Matcher matcher = EXPERIENCE_PATTERN.matcher(text);
        if (matcher.find()) {
            return matcher.group(1) + " years";
        }
        
        // Try to estimate from dates
        matcher = YEAR_PATTERN.matcher(text);
        List<Integer> years = new ArrayList<>();
        while (matcher.find()) {
            years.add(Integer.parseInt(matcher.group()));
        }
        
        if (years.size() >= 2) {
            int currentYear = Year.now().getValue();
            List<Integer> validYears = years.stream()
                .filter(y -> y >= 1990 && y <= currentYear)
                .toList();
            
            if (!validYears.isEmpty()) {
                int earliest = Collections.min(validYears);
                int estimated = currentYear - earliest;
                if (estimated > 0 && estimated <= 50) {
                    return "~" + estimated + " years";
                }
            }
        }
        
        return null;
    }

    /**
     * Extract professional summary
     */
    private String extractSummary(String text) {
        String[] lines = text.split("\\n");
        List<String> summaryKeywords = Arrays.asList(
            "summary", "professional summary", "profile", "objective",
            "career objective", "about me", "overview"
        );
        
        for (int i = 0; i < lines.length; i++) {
            String line = lines[i].toLowerCase().trim();
            
            // Check if line contains summary keyword
            for (String keyword : summaryKeywords) {
                if (line.contains(keyword) && line.length() < 50) {
                    // Found header, get next few lines
                    List<String> summaryLines = new ArrayList<>();
                    for (int j = i + 1; j < Math.min(i + 6, lines.length); j++) {
                        String nextLine = lines[j].trim();
                        if (!nextLine.isEmpty()) {
                            // Stop at next section header
                            String lowerNext = nextLine.toLowerCase();
                            if (lowerNext.contains("experience") || 
                                lowerNext.contains("education") ||
                                lowerNext.contains("skills") || 
                                lowerNext.contains("projects")) {
                                break;
                            }
                            summaryLines.add(nextLine);
                        }
                    }
                    
                    if (!summaryLines.isEmpty()) {
                        String summary = String.join(" ", summaryLines);
                        return summary.length() > 500 ? summary.substring(0, 500) : summary;
                    }
                }
            }
        }
        
        // If no explicit summary, take first meaningful paragraph
        for (int i = 0; i < Math.min(10, lines.length); i++) {
            String line = lines[i].trim();
            if (line.length() > 100 && !EMAIL_PATTERN.matcher(line).find()) {
                return line.length() > 500 ? line.substring(0, 500) : line;
            }
        }
        
        return null;
    }
}
