package com.ashvyn.tempo.service.AI;

import dev.langchain4j.service.UserMessage;
import dev.langchain4j.service.spring.AiService;

@AiService
public interface TempoAIService {

    @UserMessage("""
            Genera un saludo de bienvenida a la plataforma de gestion de tareas de Tempo. Usa menos de 120 caracteres y hazlo con un estilo basado en matrix
            """)
    String generateGreeting();
}
