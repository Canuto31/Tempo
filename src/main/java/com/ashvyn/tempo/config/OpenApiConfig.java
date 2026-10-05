package com.ashvyn.tempo.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
/** Supplies the title, description and public version shown by Swagger UI. */
public class OpenApiConfig {

    /** Creates the root OpenAPI metadata model discovered by springdoc. */
    @Bean
    OpenAPI tempoOpenApi() {
        return new OpenAPI().info(new Info()
                .title("Tempo API")
                .description("REST API for Tempo task and productivity management")
                .version("v1"));
    }
}
