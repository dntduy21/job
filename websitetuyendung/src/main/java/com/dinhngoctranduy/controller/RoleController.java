package com.dinhngoctranduy.controller;

import com.dinhngoctranduy.config.Translator;
import com.dinhngoctranduy.model.Role;
import com.dinhngoctranduy.model.response.ResultPaginationDTO;
import com.dinhngoctranduy.service.RoleService;
import com.dinhngoctranduy.util.annotation.Message;
import com.dinhngoctranduy.util.error.IdInvalidException;
import com.dinhngoctranduy.util.specification.RoleSpecification;
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
        name = "Quản lý vai trò",
        description = "Các API cho phép tạo, cập nhật, xoá và truy vấn thông tin vai trò người dùng trong hệ thống."
)
public class RoleController {

    private final RoleService roleService;

    public RoleController(RoleService roleService) {
        this.roleService = roleService;
    }

    @Operation(
            summary = "Tạo vai trò mới",
            description = "Tạo một vai trò mới nếu tên vai trò chưa tồn tại trong hệ thống."
    )
    @PostMapping("/roles")
    @Message("Create a role")
    public ResponseEntity<Role> create(@Valid @RequestBody Role r) throws IdInvalidException {
        // check name
        if (this.roleService.existByName(r.getName())) {
            throw new IdInvalidException(Translator.toLocale("role.exists.name", r.getName()));
        }
        return ResponseEntity.status(HttpStatus.CREATED).body(this.roleService.create(r));
    }

    @Operation(
            summary = "Cập nhật vai trò",
            description = "Cập nhật thông tin của vai trò đã tồn tại theo ID. Trả lỗi nếu không tìm thấy vai trò."
    )
    @PutMapping("/roles")
    @Message("Update a role")
    public ResponseEntity<Role> update(@Valid @RequestBody Role r) throws IdInvalidException {
        // check id
        if (this.roleService.fetchById(r.getId()) == null) {
            throw new IdInvalidException(Translator.toLocale("role.not.found.id", r.getId()));
        }

        return ResponseEntity.ok().body(this.roleService.update(r));
    }

    @Operation(
            summary = "Xoá vai trò",
            description = "Xoá vai trò khỏi hệ thống theo ID nếu vai trò tồn tại."
    )
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

    @Operation(
            summary = "Lấy danh sách vai trò",
            description = "Trả về danh sách các vai trò có hỗ trợ phân trang và lọc theo tên, mô tả."
    )
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

    @Operation(
            summary = "Lấy vai trò theo ID",
            description = "Trả về thông tin chi tiết của vai trò theo ID nếu tồn tại."
    )
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
