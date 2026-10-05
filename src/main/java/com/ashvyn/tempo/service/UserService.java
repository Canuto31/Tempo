package com.ashvyn.tempo.service;

import com.ashvyn.tempo.dto.user.CreateUserRequest;
import com.ashvyn.tempo.dto.user.UpdateUserRequest;
import com.ashvyn.tempo.dto.user.UserResponse;
import com.ashvyn.tempo.entity.User;
import com.ashvyn.tempo.exception.ConflictException;
import com.ashvyn.tempo.exception.ResourceNotFoundException;
import com.ashvyn.tempo.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.UUID;

@Service
/** Coordinates user persistence and enforces unique email and username values. */
public class UserService {

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    /** Creates a user and initializes its audit timestamps. */
    @Transactional
    public UserResponse create(CreateUserRequest request) {
        ensureUnique(request.email(), request.username(), null);
        Date now = new Date();
        User user = new User();
        user.setName(request.name());
        user.setUsername(request.username());
        user.setEmail(request.email());
        user.setPassword(request.password());
        user.setAuthProvider(request.authProvider());
        user.setProfileImage(request.profileImage());
        user.setCreatedAt(now);
        user.setUpdatedAt(now);
        return toResponse(userRepository.save(user));
    }

    /** Retrieves one user and maps it to a response that excludes the password. */
    @Transactional(readOnly = true)
    public UserResponse get(UUID id) {
        return toResponse(requireUser(id));
    }

    /** Lists users without exposing passwords or entity internals. */
    @Transactional(readOnly = true)
    public List<UserResponse> list() {
        return userRepository.findAll().stream().map(this::toResponse).toList();
    }

    /** Updates profile fields while preserving authentication data. */
    @Transactional
    public UserResponse update(UUID id, UpdateUserRequest request) {
        User user = requireUser(id);
        ensureUnique(request.email(), request.username(), id);
        user.setName(request.name());
        user.setUsername(request.username());
        user.setEmail(request.email());
        user.setProfileImage(request.profileImage());
        user.setUpdatedAt(new Date());
        return toResponse(userRepository.save(user));
    }

    /** Deletes a user physically because the entity does not model soft deletion. */
    @Transactional
    public void delete(UUID id) {
        userRepository.delete(requireUser(id));
    }

    /** Resolves a user entity for services that need to establish a relationship. */
    public User requireUser(UUID id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + id));
    }

    /** Rejects email or username values already assigned to another user. */
    private void ensureUnique(String email, String username, UUID currentId) {
        userRepository.findByEmail(email)
                .filter(user -> !user.getId().equals(currentId))
                .ifPresent(user -> { throw new ConflictException("Email is already in use"); });
        userRepository.findByUsername(username)
                .filter(user -> !user.getId().equals(currentId))
                .ifPresent(user -> { throw new ConflictException("Username is already in use"); });
    }

    /** Converts the persistence entity into the safe public API representation. */
    private UserResponse toResponse(User user) {
        return new UserResponse(user.getId(), user.getName(), user.getUsername(), user.getEmail(),
                user.getAuthProvider(), user.getProfileImage(), user.getCreatedAt(), user.getUpdatedAt());
    }
}
