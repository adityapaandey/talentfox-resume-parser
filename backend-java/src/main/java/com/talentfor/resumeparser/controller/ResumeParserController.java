package com.talentfor.resumeparser.controller;

import com.talentfor.resumeparser.model.ParsedResume;
import com.talentfor.resumeparser.service.ExcelExporterService;
import com.talentfor.resumeparser.service.ResumeParserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

/**
 * REST Controller for Resume Parser API
 */
@RestController
@RequestMapping("/api/resume-parser")
@RequiredArgsConstructor
@Slf4j
public class ResumeParserController {

    private final ResumeParserService resumeParserService;
    private final ExcelExporterService excelExporterService;
    
    // In-memory storage for current session
    private final List<ParsedResume> parsedResumes = Collections.synchronizedList(new ArrayList<>());
    
    private static final String UPLOAD_DIR = "uploads/resumes";
    private static final String EXPORT_DIR = "uploads/exports";

    /**
     * Parse a single PDF resume
     */
    @PostMapping("/parse")
    public ResponseEntity<Map<String, Object>> parseResume(@RequestParam("file") MultipartFile file) {
        if (!file.getOriginalFilename().toLowerCase().endsWith(".pdf")) {
            return ResponseEntity.badRequest()
                .body(Map.of("success", false, "message", "Only PDF files are supported"));
        }
        
        File tempFile = null;
        try {
            // Save uploaded file temporarily
            tempFile = saveUploadedFile(file);
            
            // Parse the resume
            ParsedResume parsedResume = resumeParserService.parsePdf(tempFile);
            
            // Store in session
            parsedResumes.add(parsedResume);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("data", parsedResume);
            response.put("message", "Successfully parsed " + file.getOriginalFilename());
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            log.error("Error parsing resume", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("success", false, "message", "Error parsing resume: " + e.getMessage()));
        } finally {
            // Clean up temp file
            if (tempFile != null && tempFile.exists()) {
                tempFile.delete();
            }
        }
    }

    /**
     * Parse multiple PDF resumes
     */
    @PostMapping("/parse-multiple")
    public ResponseEntity<Map<String, Object>> parseMultipleResumes(
            @RequestParam("files") MultipartFile[] files) {
        
        List<Map<String, Object>> results = new ArrayList<>();
        List<Map<String, Object>> errors = new ArrayList<>();
        
        for (MultipartFile file : files) {
            if (!file.getOriginalFilename().toLowerCase().endsWith(".pdf")) {
                errors.add(Map.of(
                    "filename", file.getOriginalFilename(),
                    "error", "Only PDF files are supported"
                ));
                continue;
            }
            
            File tempFile = null;
            try {
                // Save uploaded file
                tempFile = saveUploadedFile(file);
                
                // Parse the resume
                ParsedResume parsedResume = resumeParserService.parsePdf(tempFile);
                
                // Store in session
                parsedResumes.add(parsedResume);
                
                results.add(Map.of(
                    "filename", file.getOriginalFilename(),
                    "data", parsedResume,
                    "success", true
                ));
                
            } catch (Exception e) {
                log.error("Error parsing resume: " + file.getOriginalFilename(), e);
                errors.add(Map.of(
                    "filename", file.getOriginalFilename(),
                    "error", e.getMessage()
                ));
            } finally {
                // Clean up temp file
                if (tempFile != null && tempFile.exists()) {
                    tempFile.delete();
                }
            }
        }
        
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("parsed_count", results.size());
        response.put("error_count", errors.size());
        response.put("results", results);
        response.put("errors", errors);
        
        return ResponseEntity.ok(response);
    }

    /**
     * Export all parsed resumes to Excel
     */
    @GetMapping("/export-excel")
    public ResponseEntity<Resource> exportToExcel() {
        if (parsedResumes.isEmpty()) {
            return ResponseEntity.badRequest().build();
        }
        
        try {
            // Create export directory if not exists
            Path exportPath = Paths.get(EXPORT_DIR);
            Files.createDirectories(exportPath);
            
            // Generate filename with timestamp
            String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
            String filename = "parsed_resumes_" + timestamp + ".xlsx";
            String filePath = exportPath.resolve(filename).toString();
            
            // Export to Excel
            File excelFile = excelExporterService.exportToExcel(parsedResumes, filePath);
            
            // Return file for download
            Resource resource = new FileSystemResource(excelFile);
            
            return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename + "\"")
                .contentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
                .body(resource);
                
        } catch (Exception e) {
            log.error("Error exporting to Excel", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Get all parsed resumes in current session
     */
    @GetMapping("/parsed-resumes")
    public ResponseEntity<Map<String, Object>> getParsedResumes() {
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("count", parsedResumes.size());
        response.put("resumes", parsedResumes);
        
        return ResponseEntity.ok(response);
    }

    /**
     * Clear all parsed resumes from current session
     */
    @DeleteMapping("/clear")
    public ResponseEntity<Map<String, Object>> clearParsedResumes() {
        parsedResumes.clear();
        
        return ResponseEntity.ok(Map.of(
            "success", true,
            "message", "All parsed resumes cleared"
        ));
    }

    /**
     * Health check endpoint
     */
    @GetMapping("/health")
    public ResponseEntity<Map<String, Object>> healthCheck() {
        Map<String, Object> response = new HashMap<>();
        response.put("status", "healthy");
        response.put("service", "Resume Parser");
        response.put("parsed_count", parsedResumes.size());
        response.put("timestamp", LocalDateTime.now().toString());
        
        return ResponseEntity.ok(response);
    }

    /**
     * Save uploaded file temporarily
     */
    private File saveUploadedFile(MultipartFile file) throws IOException {
        // Create upload directory if not exists
        Path uploadPath = Paths.get(UPLOAD_DIR);
        Files.createDirectories(uploadPath);
        
        // Save file
        Path filePath = uploadPath.resolve(file.getOriginalFilename());
        Files.write(filePath, file.getBytes());
        
        return filePath.toFile();
    }
}
