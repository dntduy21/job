package com.dinhngoctranduy.controller;

import com.dinhngoctranduy.config.Translator;
import com.dinhngoctranduy.model.Resume;
import com.dinhngoctranduy.model.response.ResultPaginationDTO;
import com.dinhngoctranduy.model.response.resume.ResCreateResumeDTO;
import com.dinhngoctranduy.model.response.resume.ResFetchResumeDTO;
import com.dinhngoctranduy.model.response.resume.ResUpdateResumeDTO;
import com.dinhngoctranduy.service.CvAnalysisService;
import com.dinhngoctranduy.service.ResumeService;
import com.dinhngoctranduy.util.annotation.Message;
import com.dinhngoctranduy.util.error.IdInvalidException;
import com.turkraft.springfilter.boot.Filter;
import jakarta.validation.Valid;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;
import java.util.concurrent.CompletableFuture;

@RestController
@RequestMapping("/api/v1")
public class ResumeController {

    private final ResumeService resumeService;
    private final CvAnalysisService cvAnalysisService;

    public ResumeController(ResumeService resumeService, CvAnalysisService cvAnalysisService) {
        this.resumeService = resumeService;
        this.cvAnalysisService = cvAnalysisService;
    }

    @PostMapping("/resumes")
    @Message("Create a resume")
    public ResponseEntity<ResCreateResumeDTO> create(@Valid @RequestBody Resume resume) throws IdInvalidException {
        boolean isIdExist = this.resumeService.checkResumeExistByUserAndJob(resume);
        if (!isIdExist) {
            throw new IdInvalidException(Translator.toLocale("user.or.job.not.found"));
        }

        // 1. Lưu resume
        ResCreateResumeDTO res = this.resumeService.create(resume);

        // 2. Tự động phân tích nếu hợp lệ
        CompletableFuture.runAsync(() -> {
            try {
                Optional<Resume> resumeOptional = resumeService.fetchById(res.getId());
                resumeOptional.ifPresent(r -> cvAnalysisService.analyzeIfEligible(r));
            } catch (Exception e) {
                System.err.println("Auto-analysis failed after creating resume.");
                e.printStackTrace();
            }
        });

        return ResponseEntity.status(HttpStatus.CREATED).body(res);
    }

    @PutMapping("/resumes")
    @Message("Update a resume")
    public ResponseEntity<ResUpdateResumeDTO> update(@RequestBody Resume resume) throws IdInvalidException {
        // check id exist
        Optional<Resume> reqResumeOptional = this.resumeService.fetchById(resume.getId());
        if (reqResumeOptional.isEmpty()) {
            throw new IdInvalidException(Translator.toLocale("resume.not.found.id", resume.getId()));
        }

        Resume reqResume = reqResumeOptional.get();
        reqResume.setStatus(resume.getStatus());

        return ResponseEntity.ok().body(this.resumeService.update(reqResume));
    }

    @DeleteMapping("/resumes/{id}")
    @Message("Delete a resume by id")
    public ResponseEntity<Void> delete(@PathVariable("id") long id) throws IdInvalidException {
        Optional<Resume> reqResumeOptional = this.resumeService.fetchById(id);
        if (reqResumeOptional.isEmpty()) {
            throw new IdInvalidException(Translator.toLocale("resume.not.found.id", id));
        }

        this.resumeService.delete(id);
        return ResponseEntity.ok().body(null);
    }

    @GetMapping("/resumes/{id}")
    @Message("Fetch a resume by id")
    public ResponseEntity<ResFetchResumeDTO> fetchById(@PathVariable("id") long id) throws IdInvalidException {
        Optional<Resume> reqResumeOptional = this.resumeService.fetchById(id);
        if (reqResumeOptional.isEmpty()) {
            throw new IdInvalidException(Translator.toLocale("resume.not.found.id", id));
        }

        return ResponseEntity.ok().body(this.resumeService.getResume(reqResumeOptional.get()));
    }

    @GetMapping("/resumes")
    @Message("Fetch all resume with paginate")
    public ResponseEntity<ResultPaginationDTO> fetchAll(@Filter Specification<Resume> spec,
                                                        Pageable pageable) {

        return ResponseEntity.ok().body(this.resumeService.fetchAllResume(spec, pageable));
    }

    @PostMapping("/resumes/by-user")
    @Message("Get list resumes by user")
    public ResponseEntity<ResultPaginationDTO> fetchResumeByUser(Pageable pageable) {
        return ResponseEntity.ok().body(this.resumeService.fetchResumeByUser(pageable));
    }
}
