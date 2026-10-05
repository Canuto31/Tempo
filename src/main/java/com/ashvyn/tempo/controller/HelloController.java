package com.ashvyn.tempo.controller;

import com.ashvyn.tempo.service.AI.TempoAIService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
/** Keeps the pre-existing experimental AI greeting endpoint. */
public class HelloController {
    private final String platform;

    private final TempoAIService tempoAIService;

    public HelloController(@Value("${spring.application.name}") String platform, TempoAIService tempoAIService) {
        this.platform = platform;
        this.tempoAIService = tempoAIService;
    }

    /** Generates a short greeting through the configured AI service. */
    @GetMapping("/")
    public String hello() {
        return this.tempoAIService.generateGreeting(platform);
    }
}
