package com.dinhngoctranduy.controller;

import com.dinhngoctranduy.config.Translator;
import com.dinhngoctranduy.model.User;
import com.dinhngoctranduy.model.response.ResCreateUserDTO;
import com.dinhngoctranduy.model.response.ResUpdateUserDTO;
import com.dinhngoctranduy.model.response.ResUserDTO;
import com.dinhngoctranduy.model.response.ResultPaginationDTO;
import com.dinhngoctranduy.service.UserService;
import com.dinhngoctranduy.util.annotation.Message;
import com.dinhngoctranduy.util.error.IdInvalidException;
import com.dinhngoctranduy.util.specification.UserSpecification;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1")
@Tag(
        name = "Quản lý người dùng",
        description = "Các API phục vụ tạo, cập nhật, xoá và truy vấn thông tin người dùng trong hệ thống."
)
public class UserController {
    private final UserService userService;
    private final PasswordEncoder passwordEncoder;

    public UserController(UserService userService, PasswordEncoder passwordEncoder) {
        this.userService = userService;
        this.passwordEncoder = passwordEncoder;
    }

    @Operation(
            summary = "Tạo người dùng mới",
            description = "Tạo một người dùng mới với mật khẩu được mã hoá. Kiểm tra trùng email trước khi tạo."
    )
    @PostMapping("/users")
    @Message("create new user")
    public ResponseEntity<ResCreateUserDTO> createNewUser(@Valid @RequestBody User user) throws IdInvalidException {
        boolean existsEmail = this.userService.isEmailExists(user.getEmail());
        if (existsEmail) {
            throw new IdInvalidException(Translator.toLocale("user.email.exists", user.getEmail()));
        }
        String hashPassword = this.passwordEncoder.encode(user.getPassword());
        user.setPassword(hashPassword);
        User createUser = this.userService.handleCreateUser(user);
        return ResponseEntity.status(HttpStatus.CREATED).body(this.userService.resCreateUserDTO(createUser));
    }

    @Operation(
            summary = "Xoá người dùng theo ID",
            description = "Xoá người dùng khỏi hệ thống theo ID nếu tồn tại."
    )
    @DeleteMapping("/users/{id}")
    @Message("delete user")
    public ResponseEntity<Void> deleteUser(@PathVariable long id) throws IdInvalidException {
        User curUser = this.userService.fetchUserById(id);
        if (curUser == null) {
            throw new IdInvalidException(Translator.toLocale("user.not.found.id", id));
        }
        this.userService.handleDeleteUser(id);
        return ResponseEntity.ok(null);
    }

    @Operation(
            summary = "Lấy thông tin người dùng theo ID",
            description = "Trả về thông tin chi tiết của người dùng nếu tồn tại theo ID."
    )
    @GetMapping("/users/{id}")
    @Message("get user by id")
    public ResponseEntity<ResUserDTO> getUserById(@PathVariable long id) throws IdInvalidException {
        User curUser = this.userService.fetchUserById(id);
        if (curUser == null) {
            throw new IdInvalidException(Translator.toLocale("user.not.found.id", id));
        }
        return ResponseEntity.ok(this.userService.resUserDTO(curUser));
    }

    @Operation(
            summary = "Lấy danh sách người dùng",
            description = "Lấy toàn bộ người dùng với phân trang và bộ lọc theo tên, email, tuổi, giới tính, địa chỉ và vai trò."
    )
    @GetMapping("/users")
    @Message("get all users")
    public ResponseEntity<ResultPaginationDTO> getAllUser(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String email,
            @RequestParam(required = false) Integer age,
            @RequestParam(required = false) String gender,
            @RequestParam(required = false) String address,
            @RequestParam(required = false) Long roleId,
            Pageable pageable
    ) {
        Specification<User> spec = UserSpecification.withFilters(name, email, age, gender, address, roleId);
        return ResponseEntity.ok(userService.fetchAllUser(spec, pageable));
    }

    @Operation(
            summary = "Cập nhật thông tin người dùng",
            description = "Cập nhật thông tin người dùng theo ID. Nếu không tồn tại, sẽ trả về lỗi."
    )
    @PutMapping("/users")
    public ResponseEntity<ResUpdateUserDTO> updateUser(@RequestBody User user) throws IdInvalidException {
        User updateUser = this.userService.handleUpdateUser(user);
        if (updateUser == null) {
            throw new IdInvalidException(Translator.toLocale("user.not.found.id", user.getId()));
        }
        return ResponseEntity.ok(this.userService.resUpdateUserDTO(updateUser));
    }
}
