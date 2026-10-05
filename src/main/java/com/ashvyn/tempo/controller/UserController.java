package com.ashvyn.tempo.controller;

import com.ashvyn.tempo.dto.user.CreateUserRequest;
import com.ashvyn.tempo.dto.user.UpdateUserRequest;
import com.ashvyn.tempo.dto.user.UserResponse;
import com.ashvyn.tempo.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/users")
@Tag(name = "Users", description = "User profile endpoints")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Create user")
    public UserResponse create(@Valid @RequestBody CreateUserRequest request) {
        return userService.create(request);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get user")
    public UserResponse get(@PathVariable UUID id) {
        return userService.get(id);
    }

    @GetMapping
    @Operation(summary = "List users")
    public List<UserResponse> list() {
        return userService.list();
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update user profile")
    public UserResponse update(@PathVariable UUID id, @Valid @RequestBody UpdateUserRequest request) {
        return userService.update(id, request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Delete user")
    public void delete(@PathVariable UUID id) {
        userService.delete(id);
    }
}
