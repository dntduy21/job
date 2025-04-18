package com.dinhngoctranduy.service;

import com.dinhngoctranduy.model.Resume;
import com.dinhngoctranduy.model.ResumeDetails;
import com.dinhngoctranduy.model.request.GeminiRequest;
import com.dinhngoctranduy.model.response.CvAnalysisResult;
import com.dinhngoctranduy.model.response.GeminiResponse;
import com.dinhngoctranduy.repository.ResumeRepository;
import com.dinhngoctranduy.util.PdfUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Instant;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CvAnalysisService {

    @Value("${upload-file.base-uri}")
    private String baseUri;

    @Value("${gemini.api.url}")
    private String geminiApiUrl;

    @Value("${gemini.api.key}")
    private String geminiApiKey;

    private final ResumeRepository resumeRepository;
    private final RestTemplate restTemplate = new RestTemplate();

    //Gọi phân tích nếu resume chưa được phân tích và file đủ chuẩn ATS
    public void analyzeIfEligible(Resume resume) {
        String fileName = resume.getUrl();

        if (resume.isParsed()) {
            System.out.println("Resume already parsed, skipping: " + fileName);
            return;
        }

        try {
            Path filePath = Paths.get(new URI(baseUri + fileName));
            if (!Files.exists(filePath)) {
                System.err.println("File not found: " + fileName);
                return;
            }

            String text = PdfUtil.extractTextFromPdf(filePath);

            if (!isAtsCompliant(text)) {
                System.out.println("Resume not ATS-compliant: " + fileName);
                return;
            }

            analyzeAndSave(fileName); // Gọi phân tích chính

        } catch (Exception e) {
            System.err.println("Error analyzing resume: " + fileName);
            e.printStackTrace();
        }
    }

    //Phân tích nội dung và lưu vào resumeDetails
    public CvAnalysisResult analyzeAndSave(String fileName) {
        try {
            Path filePath = Paths.get(new URI(baseUri + fileName));
            if (!Files.exists(filePath)) {
                throw new RuntimeException("File not found: " + filePath.toAbsolutePath());
            }

            String text = PdfUtil.extractTextFromPdf(filePath);

            if (!isAtsCompliant(text)) {
                throw new RuntimeException("CV is not ATS-friendly. Use a simple, text-based format.");
            }

            String aiResponse = callGeminiAPI(text);
            System.out.println("=== Gemini Response ===\n" + aiResponse);

            CvAnalysisResult extracted = parseAIResult(fileName, aiResponse);
            int score = scoreCv(extracted);
            System.out.println("CV Score: " + score);

            Resume resume = resumeRepository.findByUrl(fileName)
                    .orElseThrow(() -> new RuntimeException("Resume not found for file: " + fileName));

            ResumeDetails details = resume.getResumeDetails();
            if (details == null) {
                details = new ResumeDetails();
                details.setResume(resume);           // Gán chiều 1
                resume.setResumeDetails(details);    // Gán chiều 2
            }

            details.setSkills(extracted.getSkills());
            details.setEducation(extracted.getEducation());
            details.setAddress(extracted.getAddress());
            details.setYearsOfExperience(extracted.getYearsOfExperience());
            details.setCertificates(extracted.getCertificates());
            details.setScore(score);

            resume.setParsed(true);
            resume.setUpdatedAt(Instant.now());

            resumeRepository.save(resume); // Cascade lưu ResumeDetails

            return extracted;

        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Failed to analyze and save resume: " + fileName, e);
        }
    }

    // Chấm điểm CV trên thang 10
    private int scoreCv(CvAnalysisResult result) {
        int score = 0;

        // Years of experience (0-10)
        int years = Optional.ofNullable(result.getYearsOfExperience()).orElse(0);
        if (years >= 5) score += 10;
        else if (years >= 3) score += 7;
        else if (years >= 1) score += 4;
        else score += 2;

        // Skills (0-10)
        int skillCount = result.getSkills() != null ? result.getSkills().split(",").length : 0;
        if (skillCount >= 10) score += 10;
        else if (skillCount >= 6) score += 7;
        else if (skillCount >= 3) score += 4;
        else score += 2;

        // Education (0-10)
        String edu = Optional.ofNullable(result.getEducation()).orElse("").toLowerCase();
        if (edu.contains("master") || edu.contains("thạc sĩ")) score += 10;
        else if (edu.contains("bachelor") || edu.contains("đại học")) score += 8;
        else if (edu.contains("cao đẳng") || edu.contains("college")) score += 5;
        else score += 2;

        // Certificates (0-10)
        if (result.getCertificates() != null && !result.getCertificates().isEmpty()) {
            int certCount = result.getCertificates().split(",").length;
            score += Math.min(10, certCount * 2);
        }

        // ATS bonus (5 điểm nếu pass)
        score += 5;

        return Math.min(score / 5, 10); // Chuyển về thang điểm 10
    }

    private boolean isAtsCompliant(String text) {
        return !text.toLowerCase().contains("table") &&
                !text.toLowerCase().contains("image") &&
                !text.contains("│") && !text.contains("➤") &&
                text.length() >= 500;
    }

    private String callGeminiAPI(String content) {
        String prompt = "Based on the resume content below, extract the following information:\n" +
                "Skills:\nEducation:\nAddress:\nYears of Experience:\nCertificates:\n\n" + content;

        GeminiRequest.Part part = new GeminiRequest.Part(prompt);
        GeminiRequest.Content requestContent = new GeminiRequest.Content(List.of(part), "user");
        GeminiRequest request = new GeminiRequest(List.of(requestContent));

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("x-goog-api-key", geminiApiKey);

        HttpEntity<GeminiRequest> entity = new HttpEntity<>(request, headers);

        ResponseEntity<GeminiResponse> response = restTemplate.exchange(
                geminiApiUrl,
                HttpMethod.POST,
                entity,
                GeminiResponse.class
        );

        return response.getBody()
                .getCandidates().get(0)
                .getContent().getParts().get(0)
                .getText();
    }

    private CvAnalysisResult parseAIResult(String fileName, String aiText) {
        String skills = cleanOrNull(stripMarkdown(extractBetween(aiText, "Skills:", "Education:")));
        String education = cleanOrNull(stripMarkdown(extractBetween(aiText, "Education:", "Address:")));
        String address = cleanOrNull(stripMarkdown(extractBetween(aiText, "Address:", "Years of Experience:")));
        String yearsStr = stripMarkdown(extractBetween(aiText, "Years of Experience:", "Certificates:"));
        String certStr = cleanOrNull(stripMarkdown(extractBetween(aiText, "Certificates:", null)));

        Integer yearsOfExperience = extractNumberFromText(yearsStr) > 0
                ? extractNumberFromText(yearsStr)
                : null;

        String cleanedCertStr = (certStr != null)
                ? Arrays.stream(certStr.split(","))
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .collect(Collectors.joining(", "))
                : null;

        return new CvAnalysisResult(
                fileName,
                skills,
                education,
                address,
                yearsOfExperience,
                cleanedCertStr
        );
    }

    private String cleanOrNull(String input) {
        if (input == null) return null;

        String cleaned = input
                .replaceAll("[\\n\\r]+", " ")          // Xóa xuống dòng
                .replaceAll("\\*+$", "")               // Xóa dấu * ở cuối
                .replaceAll("\\s{2,}", " ")            // Gom nhiều space thành 1
                .trim();

        return cleaned.isEmpty() || List.of("n/a", "none", "-", "null").contains(cleaned.toLowerCase()) ? null : cleaned;
    }

    private String extractBetween(String text, String start, String end) {
        int startIndex = text.indexOf(start);
        if (startIndex < 0) return null;
        startIndex += start.length();

        int endIndex = (end != null && text.contains(end)) ? text.indexOf(end) : text.length();
        if (startIndex >= endIndex) return null;

        return text.substring(startIndex, endIndex).trim();
    }

    private int extractNumberFromText(String text) {
        if (text == null) return 0;
        Matcher matcher = Pattern.compile("(\\d+)").matcher(text);
        return matcher.find() ? Integer.parseInt(matcher.group(1)) : 0;
    }

    private String stripMarkdown(String input) {
        if (input == null) return null;
        return input
                .replaceAll("\\*\\*(.*?)\\*\\*", "$1") // bold
                .replaceAll("\\*(.*?)\\*", "$1");      // italic
    }
}
