package com.dinhngoctranduy.controller;

import com.dinhngoctranduy.config.Translator;
import com.dinhngoctranduy.model.Permission;
import com.dinhngoctranduy.model.response.ResultPaginationDTO;
import com.dinhngoctranduy.service.PermissionService;
import com.dinhngoctranduy.util.annotation.Message;
import com.dinhngoctranduy.util.error.IdInvalidException;
import com.dinhngoctranduy.util.specification.PermissionSpecification;
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
        name = "Quản lý phân quyền",
        description = "Các API cho phép tạo, cập nhật, xoá và truy vấn thông tin quyền truy cập (permissions) trong hệ thống."
)
public class PermissionController {

    private final PermissionService permissionService;

    public PermissionController(PermissionService permissionService) {
        this.permissionService = permissionService;
    }

    @Operation(
            summary = "Tạo quyền mới",
            description = "Tạo một quyền mới nếu chưa tồn tại theo module, apiPath và phương thức HTTP."
    )
    @PostMapping("/permissions")
    @Message("Create a permission")
    public ResponseEntity<Permission> create(@Valid @RequestBody Permission p) throws IdInvalidException {
        // check exist
        if (this.permissionService.isPermissionExist(p)) {
            throw new IdInvalidException(Translator.toLocale("permission.exists"));
        }

        // create new permission
        return ResponseEntity.status(HttpStatus.CREATED).body(this.permissionService.create(p));
    }

    @Operation(
            summary = "Cập nhật quyền",
            description = "Cập nhật thông tin quyền nếu tồn tại. Nếu quyền đã tồn tại với module + path + method nhưng khác tên, sẽ cho phép cập nhật."
    )
    @PutMapping("/permissions")
    @Message("Update a permission")
    public ResponseEntity<Permission> update(@Valid @RequestBody Permission p) throws IdInvalidException {
        // check exist by id
        if (this.permissionService.fetchById(p.getId()) == null) {
            throw new IdInvalidException(Translator.toLocale("permission.not.found.id", p.getId()));
        }

        // check exist by module, apiPath and method
        if (this.permissionService.isPermissionExist(p)) {
            // check name
            if (this.permissionService.isSameName(p)) {
                throw new IdInvalidException(Translator.toLocale("permission.exists"));
            }
        }

        // update permission
        return ResponseEntity.ok().body(this.permissionService.update(p));
    }

    @Operation(
            summary = "Xoá quyền theo ID",
            description = "Xoá quyền khỏi hệ thống nếu tìm thấy theo ID."
    )
    @DeleteMapping("/permissions/{id}")
    @Message("delete a permission")
    public ResponseEntity<Void> delete(@PathVariable("id") long id) throws IdInvalidException {
        // check exist by id
        if (this.permissionService.fetchById(id) == null) {
            throw new IdInvalidException(Translator.toLocale("permission.not.found.id", id));
        }
        this.permissionService.delete(id);
        return ResponseEntity.ok().body(null);
    }

    @Operation(
            summary = "Lấy danh sách quyền",
            description = "Trả về danh sách quyền có hỗ trợ phân trang và bộ lọc theo tên, mô tả và module."
    )
    @GetMapping("/permissions")
    @Message("Fetch permissions")
    public ResponseEntity<ResultPaginationDTO> getPermissions(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String description,
            @RequestParam(required = false) String module,
            Pageable pageable
    ) {
        Specification<Permission> spec = PermissionSpecification.withFilters(name, description, module);
        return ResponseEntity.ok(permissionService.getPermissions(spec, pageable));
    }
}
