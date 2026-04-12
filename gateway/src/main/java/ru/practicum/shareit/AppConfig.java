package ru.practicum.shareit;

import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration
public class AppConfig {
    @Bean
    public RestTemplate restTemplate(RestTemplateBuilder builder) {
        return builder.build();
        /*Spring Boot не внедряет RestTemplate автоматически «из коробки».
Хотя Spring Boot предоставляет много удобных автоконфигураций (например, для HttpClient, WebClient), бин RestTemplate по умолчанию не создаётся автоматически.
Почему так?
RestTemplate считается достаточно низкоуровневым инструментом, и Spring поощряет использование более современных альтернатив (например, WebClient).
Автоматическое создание могло бы привести к конфликтам в сложных проектах с кастомной настройкой HTTP-клиентов.*/
    }
}
