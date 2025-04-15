package com.dinhngoctranduy.controller;

import com.dinhngoctranduy.config.Translator;
import com.dinhngoctranduy.model.Job;
import com.dinhngoctranduy.model.response.ResultPaginationDTO;
import com.dinhngoctranduy.model.response.job.ResCreateJobDTO;
import com.dinhngoctranduy.model.response.job.ResUpdateJobDTO;
import com.dinhngoctranduy.service.JobService;
import com.dinhngoctranduy.util.annotation.Message;
import com.dinhngoctranduy.util.error.IdInvalidException;
import com.dinhngoctranduy.util.specification.JobSpecification;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/api/v1")
@Tag(
        name = "Quản lý công việc",
        description = "Các API phục vụ tạo, cập nhật, xoá và truy xuất thông tin công việc."
)
public class JobController {

    private final JobService jobService;

    public JobController(JobService jobService) {
        this.jobService = jobService;
    }

    @Operation(
            summary = "Tạo công việc mới",
            description = "Tạo mới một công việc và trả về thông tin công việc vừa được tạo."
    )
    @PostMapping("/jobs")
    @Message("Create a job")
    public ResponseEntity<ResCreateJobDTO> create(@Valid @RequestBody Job job) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(this.jobService.create(job));
    }

    @Operation(
            summary = "Cập nhật công việc",
            description = "Cập nhật thông tin công việc dựa theo ID. Nếu không tìm thấy, sẽ trả về lỗi."
    )
    @PutMapping("/jobs")
    @Message("Update a job")
    public ResponseEntity<ResUpdateJobDTO> update(@Valid @RequestBody Job job) throws IdInvalidException {
        Optional<Job> currentJob = this.jobService.fetchJobById(job.getId());
        if (!currentJob.isPresent()) {
            throw new IdInvalidException(Translator.toLocale("job.not.found"));
        }

        return ResponseEntity.ok()
                .body(this.jobService.update(job, currentJob.get()));
    }

    @Operation(
            summary = "Xoá công việc theo ID",
            description = "Xoá công việc khỏi hệ thống bằng ID. Nếu không tồn tại, sẽ trả về lỗi."
    )
    @DeleteMapping("/jobs/{id}")
    @Message("Delete a job by id")
    public ResponseEntity<Void> delete(@PathVariable("id") long id) throws IdInvalidException {
        Optional<Job> currentJob = this.jobService.fetchJobById(id);
        if (!currentJob.isPresent()) {
            throw new IdInvalidException(Translator.toLocale("job.not.found"));
        }
        this.jobService.delete(id);
        return ResponseEntity.ok().body(null);
    }

    @Operation(
            summary = "Lấy thông tin công việc theo ID",
            description = "Trả về chi tiết thông tin công việc theo ID. Nếu không tồn tại, sẽ trả về lỗi."
    )
    @GetMapping("/jobs/{id}")
    @Message("Get a job by id")
    public ResponseEntity<Job> getJob(@PathVariable("id") long id) throws IdInvalidException {
        Optional<Job> currentJob = this.jobService.fetchJobById(id);
        if (!currentJob.isPresent()) {
            throw new IdInvalidException(Translator.toLocale("job.not.found"));
        }

        return ResponseEntity.ok().body(currentJob.get());
    }

    @Operation(
            summary = "Lấy danh sách công việc",
            description = "Lấy danh sách công việc có hỗ trợ phân trang và lọc theo tên, vị trí và cấp độ."
    )
    @GetMapping("/jobs")
    @Message("Get job with pagination")
    public ResponseEntity<ResultPaginationDTO> getAllJobs(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String location,
            @RequestParam(required = false) String level,
            Pageable pageable
    ) {
        Specification<Job> spec = JobSpecification.withFilters(name, location, level);
        ResultPaginationDTO result = jobService.fetchAll(spec, pageable);
        return ResponseEntity.ok(result);
    }
}
