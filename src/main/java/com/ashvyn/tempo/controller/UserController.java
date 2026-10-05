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
/** Exposes the Sprint 1 operations for user profiles. */
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    /** Creates a user after validating the request and uniqueness rules. */
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Create user")
    public UserResponse create(@Valid @RequestBody CreateUserRequest request) {
        return userService.create(request);
    }

    /** Returns the public profile for the requested user identifier. */
    @GetMapping("/{id}")
    @Operation(summary = "Get user")
    public UserResponse get(@PathVariable UUID id) {
        return userService.get(id);
    }

    /** Lists the public profiles currently stored in Tempo. */
    @GetMapping
    @Operation(summary = "List users")
    public List<UserResponse> list() {
        return userService.list();
    }

    /** Replaces the editable profile information without exposing or changing the password. */
    @PutMapping("/{id}")
    @Operation(summary = "Update user profile")
    public UserResponse update(@PathVariable UUID id, @Valid @RequestBody UpdateUserRequest request) {
        return userService.update(id, request);
    }

    /** Physically deletes a user because the current User entity has no soft-delete field. */
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Delete user")
    public void delete(@PathVariable UUID id) {
        userService.delete(id);
    }
}
