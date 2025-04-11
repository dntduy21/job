package com.dinhngoctranduy.controller;

import com.dinhngoctranduy.config.Translator;
import com.dinhngoctranduy.model.Role;
import com.dinhngoctranduy.model.response.ResultPaginationDTO;
import com.dinhngoctranduy.service.RoleService;
import com.dinhngoctranduy.util.annotation.Message;
import com.dinhngoctranduy.util.error.IdInvalidException;
import com.dinhngoctranduy.util.specification.RoleSpecification;
import jakarta.validation.Valid;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1")
public class RoleController {

    private final RoleService roleService;

    public RoleController(RoleService roleService) {
        this.roleService = roleService;
    }

    @PostMapping("/roles")
    @Message("Create a role")
    public ResponseEntity<Role> create(@Valid @RequestBody Role r) throws IdInvalidException {
        // check name
        if (this.roleService.existByName(r.getName())) {
            throw new IdInvalidException(Translator.toLocale("role.exists.name", r.getName()));
        }
        return ResponseEntity.status(HttpStatus.CREATED).body(this.roleService.create(r));
    }

    @PutMapping("/roles")
    @Message("Update a role")
    public ResponseEntity<Role> update(@Valid @RequestBody Role r) throws IdInvalidException {
        // check id
        if (this.roleService.fetchById(r.getId()) == null) {
            throw new IdInvalidException(Translator.toLocale("role.not.found.id", r.getId()));
        }

        return ResponseEntity.ok().body(this.roleService.update(r));
    }

    @DeleteMapping("/roles/{id}")
    @Message("Delete a role")
    public ResponseEntity<Void> delete(@PathVariable("id") long id) throws IdInvalidException {
        // check id
        if (this.roleService.fetchById(id) == null) {
            throw new IdInvalidException(Translator.toLocale("role.not.found.id", id));
        }
        this.roleService.delete(id);
        return ResponseEntity.ok().body(null);
    }

    @GetMapping("/roles")
    @Message("Fetch roles")
    public ResponseEntity<ResultPaginationDTO> getRoles(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String description,
            Pageable pageable
    ) {
        Specification<Role> spec = RoleSpecification.withFilters(name, description);
        return ResponseEntity.ok(roleService.getRoles(spec, pageable));
    }

    @GetMapping("/roles/{id}")
    @Message("Fetch role by id")
    public ResponseEntity<Role> getById(@PathVariable("id") long id) throws IdInvalidException {

        Role role = this.roleService.fetchById(id);
        if (role == null) {
            throw new IdInvalidException(Translator.toLocale("role.not.found.id", id));
        }

        return ResponseEntity.ok().body(role);
    }

}
