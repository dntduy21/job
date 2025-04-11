package com.dinhngoctranduy.controller;

import com.dinhngoctranduy.config.Translator;
import com.dinhngoctranduy.model.Skill;
import com.dinhngoctranduy.model.response.ResultPaginationDTO;
import com.dinhngoctranduy.service.SkillService;
import com.dinhngoctranduy.util.annotation.Message;
import com.dinhngoctranduy.util.error.IdInvalidException;
import com.dinhngoctranduy.util.specification.SkillSpecification;
import jakarta.validation.Valid;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1")
public class SkillController {

    private final SkillService skillService;

    public SkillController(SkillService skillService) {
        this.skillService = skillService;
    }

    @PostMapping("/skills")
    @Message("Create a skill")
    public ResponseEntity<Skill> create(@Valid @RequestBody Skill s) throws IdInvalidException {
        // check name
        if (s.getName() != null && this.skillService.isNameExist(s.getName())) {
            throw new IdInvalidException(Translator.toLocale("skill.exists.name", s.getName()));
        }
        return ResponseEntity.status(HttpStatus.CREATED).body(this.skillService.createSkill(s));
    }

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
