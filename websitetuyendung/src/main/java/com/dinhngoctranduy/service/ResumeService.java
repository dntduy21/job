package com.dinhngoctranduy.service;

import com.dinhngoctranduy.model.Job;
import com.dinhngoctranduy.model.Resume;
import com.dinhngoctranduy.model.User;
import com.dinhngoctranduy.model.response.ResultPaginationDTO;
import com.dinhngoctranduy.model.response.resume.ResCreateResumeDTO;
import com.dinhngoctranduy.model.response.resume.ResFetchResumeDTO;
import com.dinhngoctranduy.model.response.resume.ResUpdateResumeDTO;
import com.dinhngoctranduy.repository.JobRepository;
import com.dinhngoctranduy.repository.ResumeRepository;
import com.dinhngoctranduy.repository.UserRepository;
import com.dinhngoctranduy.util.SecurityUtil;
import com.dinhngoctranduy.util.specification.ResumeSpecification;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ResumeService {
    private final ResumeRepository resumeRepository;
    private final UserRepository userRepository;
    private final JobRepository jobRepository;

    public ResumeService(
            ResumeRepository resumeRepository,
            UserRepository userRepository,
            JobRepository jobRepository) {
        this.resumeRepository = resumeRepository;
        this.userRepository = userRepository;
        this.jobRepository = jobRepository;
    }

    public Optional<Resume> fetchById(long id) {
        return this.resumeRepository.findById(id);
    }

    public boolean checkResumeExistByUserAndJob(Resume resume) {
        // check user by id
        if (resume.getUser() == null)
            return false;
        Optional<User> userOptional = this.userRepository.findById(resume.getUser().getId());
        if (userOptional.isEmpty())
            return false;

        // check job by id
        if (resume.getJob() == null)
            return false;
        Optional<Job> jobOptional = this.jobRepository.findById(resume.getJob().getId());
        if (jobOptional.isEmpty())
            return false;

        return true;
    }

    public ResCreateResumeDTO create(Resume resume) {
        resume = this.resumeRepository.save(resume);

        ResCreateResumeDTO res = new ResCreateResumeDTO();
        res.setId(resume.getId());
        res.setCreatedBy(resume.getCreatedBy());
        res.setCreatedAt(resume.getCreatedAt());

        return res;
    }

    public ResUpdateResumeDTO update(Resume resume) {
        resume = this.resumeRepository.save(resume);
        ResUpdateResumeDTO res = new ResUpdateResumeDTO();
        res.setUpdatedAt(resume.getUpdatedAt());
        res.setUpdatedBy(resume.getUpdatedBy());
        return res;
    }

    @Transactional
    public void delete(long id) {
        Resume resume = resumeRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Resume not found with id " + id));

        // Xoá liên kết để Hibernate tự xoá ResumeDetails
        resume.setResumeDetails(null);
        resumeRepository.delete(resume);
    }


    public ResFetchResumeDTO getResume(Resume resume) {
        ResFetchResumeDTO res = new ResFetchResumeDTO();
        res.setId(resume.getId());
        res.setEmail(resume.getEmail());
        res.setUrl(resume.getUrl());
        res.setStatus(resume.getStatus());
        res.setCreatedAt(resume.getCreatedAt());
        res.setCreatedBy(resume.getCreatedBy());
        res.setUpdatedAt(resume.getUpdatedAt());
        res.setUpdatedBy(resume.getUpdatedBy());

        res.setUser(new ResFetchResumeDTO.UserResume(resume.getUser().getId(), resume.getUser().getName()));
        res.setJob(new ResFetchResumeDTO.JobResume(resume.getJob().getId(), resume.getJob().getName()));

        return res;
    }

    public ResultPaginationDTO fetchAllResume(Specification<Resume> spec, Pageable pageable) {
        Page<Resume> pageUser = this.resumeRepository.findAll(spec, pageable);
        ResultPaginationDTO rs = new ResultPaginationDTO();
        ResultPaginationDTO.Meta mt = new ResultPaginationDTO.Meta();

        mt.setPage(pageable.getPageNumber() + 1);
        mt.setPageSize(pageable.getPageSize());

        mt.setPages(pageUser.getTotalPages());
        mt.setTotal(pageUser.getTotalElements());

        rs.setMeta(mt);

        // remove sensitive data
        List<ResFetchResumeDTO> listResume = pageUser.getContent()
                .stream().map(item -> this.getResume(item))
                .collect(Collectors.toList());

        rs.setResult(listResume);

        return rs;
    }

    public ResultPaginationDTO fetchResumeByUser(Pageable pageable) {
        // Lấy email người dùng hiện tại
        String email = SecurityUtil.getCurrentUserLogin().orElse("");

        // Gọi spec filter theo email
        Specification<Resume> spec = ResumeSpecification.withAllFilters(
                email,     // email
                null,      // url
                null,      // status
                null,      // isParsed
                null,      // userId
                null,      // jobId
                null,      // skills
                null,      // education
                null,      // address
                null,      // minYearsOfExperience
                null,      // maxYearsOfExperience
                null,      // certificate
                null,
                null
        );

        Page<Resume> pageResume = this.resumeRepository.findAll(spec, pageable);

        // Tạo meta
        ResultPaginationDTO rs = new ResultPaginationDTO();
        ResultPaginationDTO.Meta mt = new ResultPaginationDTO.Meta();
        mt.setPage(pageable.getPageNumber() + 1);
        mt.setPageSize(pageable.getPageSize());
        mt.setPages(pageResume.getTotalPages());
        mt.setTotal(pageResume.getTotalElements());
        rs.setMeta(mt);

        // Map về DTO (ẩn field nhạy cảm nếu cần)
        List<ResFetchResumeDTO> listResume = pageResume.getContent()
                .stream().map(this::getResume)
                .collect(Collectors.toList());

        rs.setResult(listResume);

        return rs;
    }
}
