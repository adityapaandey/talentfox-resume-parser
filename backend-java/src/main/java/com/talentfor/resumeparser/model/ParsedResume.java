package com.talentfor.resumeparser.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Data model for parsed resume information
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ParsedResume {
    private String name;
    private String email;
    private String phone;
    private String linkedin;
    private String github;
    private List<String> skills;
    private String education;
    private String experienceYears;
    private String summary;
    private String filename;
    private LocalDateTime parsedDate;
}
