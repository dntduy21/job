package com.dinhngoctranduy.controller;

import com.dinhngoctranduy.config.Translator;
import com.dinhngoctranduy.model.Skill;
import com.dinhngoctranduy.model.response.ResultPaginationDTO;
import com.dinhngoctranduy.service.SkillService;
import com.dinhngoctranduy.util.annotation.Message;
import com.dinhngoctranduy.util.error.IdInvalidException;
import com.dinhngoctranduy.util.specification.SkillSpecification;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1")
@Tag(
        name = "Quản lý kỹ năng",
        description = "Các API cho phép tạo, cập nhật, xoá và truy vấn thông tin kỹ năng trong hệ thống."
)
public class SkillController {

    private final SkillService skillService;

    public SkillController(SkillService skillService) {
        this.skillService = skillService;
    }

    @Operation(
            summary = "Tạo kỹ năng mới",
            description = "Tạo một kỹ năng mới nếu tên kỹ năng chưa tồn tại trong hệ thống."
    )
    @PostMapping("/skills")
    @Message("Create a skill")
    public ResponseEntity<Skill> create(@Valid @RequestBody Skill s) throws IdInvalidException {
        // check name
        if (s.getName() != null && this.skillService.isNameExist(s.getName())) {
            throw new IdInvalidException(Translator.toLocale("skill.exists.name", s.getName()));
        }
        return ResponseEntity.status(HttpStatus.CREATED).body(this.skillService.createSkill(s));
    }

    @Operation(
            summary = "Cập nhật kỹ năng",
            description = "Cập nhật thông tin kỹ năng đã có. Kiểm tra trùng tên và tồn tại trước khi cập nhật."
    )
    @PutMapping("/skills")
    @Message("Update a skill")
    public ResponseEntity<Skill> update(@Valid @RequestBody Skill s) throws IdInvalidException {
        // check id
        Skill currentSkill = this.skillService.fetchSkillById(s.getId());
        if (currentSkill == null) {
            throw new IdInvalidException(Translator.toLocale("skill.not.found.id", s.getId()));
        }

        // check name
        if (s.getName() != null && this.skillService.isNameExist(s.getName())) {
            throw new IdInvalidException(Translator.toLocale("skill.exists.name", s.getName()));
        }

        currentSkill.setName(s.getName());
        return ResponseEntity.ok().body(this.skillService.updateSkill(currentSkill));
    }

    @Operation(
            summary = "Xoá kỹ năng theo ID",
            description = "Xoá kỹ năng khỏi hệ thống nếu tồn tại theo ID."
    )
    @DeleteMapping("/skills/{id}")
    @Message("Delete a skill")
    public ResponseEntity<Void> delete(@PathVariable("id") long id) throws IdInvalidException {
        // check id
        Skill currentSkill = this.skillService.fetchSkillById(id);
        if (currentSkill == null) {
            throw new IdInvalidException(Translator.toLocale("skill.not.found.id", id));
        }
        this.skillService.deleteSkill(id);
        return ResponseEntity.ok().body(null);
    }

    @Operation(
            summary = "Lấy danh sách kỹ năng",
            description = "Trả về danh sách kỹ năng có hỗ trợ phân trang và lọc theo tên kỹ năng."
    )
    @GetMapping("/skills")
    @Message("fetch all skills")
    public ResponseEntity<ResultPaginationDTO> getAllSkills(
            @RequestParam(required = false) String name,
            Pageable pageable
    ) {
        Specification<Skill> spec = SkillSpecification.withFilters(name);
        return ResponseEntity.ok(skillService.fetchAllSkills(spec, pageable));
    }
}
