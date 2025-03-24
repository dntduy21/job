package com.dinhngoctranduy.service;

import com.dinhngoctranduy.model.Job;
import com.dinhngoctranduy.model.Skill;
import com.dinhngoctranduy.model.response.ResultPaginationDTO;
import com.dinhngoctranduy.model.response.job.ResCreateJobDTO;
import com.dinhngoctranduy.model.response.job.ResUpdateJobDTO;
import com.dinhngoctranduy.repository.JobRepository;
import com.dinhngoctranduy.repository.SkillRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class JobService {

    private final JobRepository jobRepository;
    private final SkillRepository skillRepository;

    public JobService(JobRepository jobRepository,
                      SkillRepository skillRepository) {
        this.jobRepository = jobRepository;
        this.skillRepository = skillRepository;
    }

    public Optional<Job> fetchJobById(long id) {
        return this.jobRepository.findById(id);
    }

    public ResCreateJobDTO create(Job job) {
        // check skills
        if (job.getSkills() != null) {
            List<Long> reqSkills = job.getSkills()
                    .stream().map(x -> x.getId())
                    .collect(Collectors.toList());

            List<Skill> dbSkills = this.skillRepository.findByIdIn(reqSkills);
            job.setSkills(dbSkills);
        }

        // create job
        Job currentJob = this.jobRepository.save(job);

        // convert response
        ResCreateJobDTO dto = new ResCreateJobDTO();
        dto.setId(currentJob.getId());
        dto.setName(currentJob.getName());
        dto.setSalary(currentJob.getSalary());
        dto.setQuantity(currentJob.getQuantity());
        dto.setLocation(currentJob.getLocation());
        dto.setLevel(currentJob.getLevel());
        dto.setStartDate(currentJob.getStartDate());
        dto.setEndDate(currentJob.getEndDate());
        dto.setActive(currentJob.isActive());
        dto.setCreatedAt(currentJob.getCreatedAt());
        dto.setCreatedBy(currentJob.getCreatedBy());

        if (currentJob.getSkills() != null) {
            List<String> skills = currentJob.getSkills()
                    .stream().map(item -> item.getName())
                    .collect(Collectors.toList());
            dto.setSkills(skills);
        }

        return dto;
    }

    public ResUpdateJobDTO update(Job job, Job jobInDB) {

        // check skills
        if (job.getSkills() != null) {
            List<Long> reqSkills = job.getSkills()
                    .stream().map(x -> x.getId())
                    .collect(Collectors.toList());

            List<Skill> dbSkills = this.skillRepository.findByIdIn(reqSkills);
            jobInDB.setSkills(dbSkills);
        }

        // update correct info
        jobInDB.setName(job.getName());
        jobInDB.setSalary(job.getSalary());
        jobInDB.setQuantity(job.getQuantity());
        jobInDB.setLocation(job.getLocation());
        jobInDB.setLevel(job.getLevel());
        jobInDB.setStartDate(job.getStartDate());
        jobInDB.setEndDate(job.getEndDate());
        jobInDB.setActive(job.isActive());

        // update job
        Job currentJob = this.jobRepository.save(jobInDB);

        // convert response
        ResUpdateJobDTO dto = new ResUpdateJobDTO();
        dto.setId(currentJob.getId());
        dto.setName(currentJob.getName());
        dto.setSalary(currentJob.getSalary());
        dto.setQuantity(currentJob.getQuantity());
        dto.setLocation(currentJob.getLocation());
        dto.setLevel(currentJob.getLevel());
        dto.setStartDate(currentJob.getStartDate());
        dto.setEndDate(currentJob.getEndDate());
        dto.setActive(currentJob.isActive());
        dto.setUpdatedAt(currentJob.getUpdatedAt());
        dto.setUpdatedBy(currentJob.getUpdatedBy());

        if (currentJob.getSkills() != null) {
            List<String> skills = currentJob.getSkills()
                    .stream().map(item -> item.getName())
                    .collect(Collectors.toList());
            dto.setSkills(skills);
        }

        return dto;
    }

    public void delete(long id) {
        this.jobRepository.deleteById(id);
    }

    public ResultPaginationDTO fetchAll(Specification<Job> spec, Pageable pageable) {
        Page<Job> pageUser = this.jobRepository.findAll(spec, pageable);

        ResultPaginationDTO rs = new ResultPaginationDTO();
        ResultPaginationDTO.Meta mt = new ResultPaginationDTO.Meta();

        mt.setPage(pageable.getPageNumber() + 1);
        mt.setPageSize(pageable.getPageSize());

        mt.setPages(pageUser.getTotalPages());
        mt.setTotal(pageUser.getTotalElements());

        rs.setMeta(mt);

        rs.setResult(pageUser.getContent());

        return rs;
    }
}
