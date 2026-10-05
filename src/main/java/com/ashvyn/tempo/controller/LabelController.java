package com.ashvyn.tempo.controller;

import com.ashvyn.tempo.dto.label.CreateLabelRequest;
import com.ashvyn.tempo.dto.label.LabelResponse;
import com.ashvyn.tempo.dto.label.UpdateLabelRequest;
import com.ashvyn.tempo.service.LabelService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/labels")
@Tag(name = "Labels", description = "Global and user label endpoints")
public class LabelController {

    private final LabelService labelService;

    public LabelController(LabelService labelService) {
        this.labelService = labelService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Create label")
    public LabelResponse create(@Valid @RequestBody CreateLabelRequest request) {
        return labelService.create(request);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get active label")
    public LabelResponse get(@PathVariable UUID id) {
        return labelService.get(id);
    }

    @GetMapping
    @Operation(summary = "List active labels")
    public List<LabelResponse> list(@RequestParam(required = false) UUID ownerId,
                                    @RequestParam(defaultValue = "false") boolean global) {
        return labelService.list(ownerId, global);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update label")
    public LabelResponse update(@PathVariable UUID id, @Valid @RequestBody UpdateLabelRequest request) {
        return labelService.update(id, request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Soft delete label")
    public void delete(@PathVariable UUID id) {
        labelService.delete(id);
    }
}
