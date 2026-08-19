package com.talentfor.resumeparser.service;

import com.talentfor.resumeparser.model.ParsedResume;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * Service for exporting parsed resumes to Excel
 */
@Service
public class ExcelExporterService {

    private static final String[] COLUMN_HEADERS = {
        "Name", "Email", "Phone", "Experience",
        "Skills", "Education", "LinkedIn", "GitHub",
        "Summary", "Filename", "Parsed Date"
    };

    private static final int[] COLUMN_WIDTHS = {
        25, 30, 18, 15, 50, 60, 35, 35, 60, 30, 20
    };

    /**
     * Export list of parsed resumes to Excel file
     */
    public File exportToExcel(List<ParsedResume> resumes, String outputPath) throws IOException {
        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("Resumes");
            
            // Create header row
            createHeaderRow(workbook, sheet);
            
            // Create data rows
            int rowNum = 1;
            for (ParsedResume resume : resumes) {
                createDataRow(sheet, rowNum++, resume);
            }
            
            // Auto-size columns
            for (int i = 0; i < COLUMN_HEADERS.length; i++) {
                sheet.setColumnWidth(i, COLUMN_WIDTHS[i] * 256);
            }
            
            // Freeze header row
            sheet.createFreezePane(0, 1);
            
            // Write to file
            File outputFile = new File(outputPath);
            try (FileOutputStream fileOut = new FileOutputStream(outputFile)) {
                workbook.write(fileOut);
            }
            
            return outputFile;
        }
    }

    /**
     * Create and format header row
     */
    private void createHeaderRow(Workbook workbook, Sheet sheet) {
        Row headerRow = sheet.createRow(0);
        
        // Create header style
        CellStyle headerStyle = workbook.createCellStyle();
        headerStyle.setFillForegroundColor(IndexedColors.DARK_BLUE.getIndex());
        headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        
        Font headerFont = workbook.createFont();
        headerFont.setBold(true);
        headerFont.setColor(IndexedColors.WHITE.getIndex());
        headerStyle.setFont(headerFont);
        
        headerStyle.setAlignment(HorizontalAlignment.CENTER);
        headerStyle.setVerticalAlignment(VerticalAlignment.CENTER);
        
        // Create header cells
        for (int i = 0; i < COLUMN_HEADERS.length; i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(COLUMN_HEADERS[i]);
            cell.setCellStyle(headerStyle);
        }
    }

    /**
     * Create data row from parsed resume
     */
    private void createDataRow(Sheet sheet, int rowNum, ParsedResume resume) {
        Row row = sheet.createRow(rowNum);
        
        int colNum = 0;
        
        // Name
        createCell(row, colNum++, resume.getName());
        
        // Email
        createCell(row, colNum++, resume.getEmail());
        
        // Phone
        createCell(row, colNum++, resume.getPhone());
        
        // Experience
        createCell(row, colNum++, resume.getExperienceYears());
        
        // Skills
        String skills = resume.getSkills() != null ? 
            String.join(", ", resume.getSkills()) : "";
        createCell(row, colNum++, skills);
        
        // Education - handle multi-line properly
        String education = resume.getEducation() != null ? 
            resume.getEducation().replace(" | ", "\n") : "";
        Cell eduCell = createCell(row, colNum++, education);
        CellStyle eduStyle = sheet.getWorkbook().createCellStyle();
        eduStyle.setWrapText(true);
        eduStyle.setVerticalAlignment(VerticalAlignment.TOP);
        eduCell.setCellStyle(eduStyle);
        
        // LinkedIn
        createCell(row, colNum++, resume.getLinkedin());
        
        // GitHub
        createCell(row, colNum++, resume.getGithub());
        
        // Summary
        Cell summaryCell = createCell(row, colNum++, resume.getSummary());
        CellStyle wrapStyle = sheet.getWorkbook().createCellStyle();
        wrapStyle.setWrapText(true);
        wrapStyle.setVerticalAlignment(VerticalAlignment.TOP);
        summaryCell.setCellStyle(wrapStyle);
        
        // Filename
        createCell(row, colNum++, resume.getFilename());
        
        // Parsed Date
        String parsedDate = resume.getParsedDate() != null ?
            resume.getParsedDate().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME) : "";
        createCell(row, colNum++, parsedDate);
    }

    /**
     * Create cell with value
     */
    private Cell createCell(Row row, int column, String value) {
        Cell cell = row.createCell(column);
        cell.setCellValue(value != null ? value : "");
        return cell;
    }
}
