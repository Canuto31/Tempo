package com.ashvyn.tempo.controller;

import com.ashvyn.tempo.service.AI.TempoAIService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HelloController {

    private final TempoAIService tempoAIService;

    public HelloController(TempoAIService tempoAIService) {
        this.tempoAIService = tempoAIService;
    }

    @GetMapping("/")
    public String hello() {
        return this.tempoAIService.generateGreeting();
    }
}
