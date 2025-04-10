package com.dinhngoctranduy.model.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CvAnalysisResult {
    private String fileName;
    private String skills;
    private String education;
    private String address;
    private int yearsOfExperience;
    private String certificates;
}
