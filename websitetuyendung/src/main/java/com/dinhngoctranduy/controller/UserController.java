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
import jakarta.validation.Valid;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1")
public class UserController {
    private final UserService userService;
    private final PasswordEncoder passwordEncoder;

    public UserController(UserService userService, PasswordEncoder passwordEncoder) {
        this.userService = userService;
        this.passwordEncoder = passwordEncoder;
    }

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

    @GetMapping("/users/{id}")
    @Message("get user by id")
    public ResponseEntity<ResUserDTO> getUserById(@PathVariable long id) throws IdInvalidException {
        User curUser = this.userService.fetchUserById(id);
        if (curUser == null) {
            throw new IdInvalidException(Translator.toLocale("user.not.found.id", id));
        }
        return ResponseEntity.ok(this.userService.resUserDTO(curUser));
    }

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

    @PutMapping("/users")
    public ResponseEntity<ResUpdateUserDTO> updateUser(@RequestBody User user) throws IdInvalidException {
        User updateUser = this.userService.handleUpdateUser(user);
        if (updateUser == null) {
            throw new IdInvalidException(Translator.toLocale("user.not.found.id", user.getId()));
        }
        return ResponseEntity.ok(this.userService.resUpdateUserDTO(updateUser));
    }
}
