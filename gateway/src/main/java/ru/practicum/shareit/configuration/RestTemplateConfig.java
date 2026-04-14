package ru.practicum.shareit.configuration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.DefaultUriBuilderFactory;

@Configuration
public class RestTemplateConfig {

    @Value("${shareit-server.url}")
    private String serverUrl;

    // Для BookingClient
    @Bean("bookingRestTemplate")
    public RestTemplate bookingRestTemplate(RestTemplateBuilder builder) {
        return builder
                .uriTemplateHandler(new DefaultUriBuilderFactory(serverUrl + "/bookings"))
                .requestFactory(() -> new HttpComponentsClientHttpRequestFactory())
                .build();
    }

    // Для ItemClient
    @Bean("itemRestTemplate")
    public RestTemplate itemRestTemplate(RestTemplateBuilder builder) {
        return builder
                .uriTemplateHandler(new DefaultUriBuilderFactory(serverUrl + "/items"))
                .requestFactory(() -> new HttpComponentsClientHttpRequestFactory())
                .build();
    }

    @Bean("userRestTemplate")
    public RestTemplate userRestTemplate(RestTemplateBuilder builder) {
        return builder
                .uriTemplateHandler(new DefaultUriBuilderFactory(serverUrl + "/users"))
                .requestFactory(() -> new HttpComponentsClientHttpRequestFactory())
                .build();
    }

    @Bean("itemRequestRestTemplate")
    public RestTemplate itemRequestRestTemplate(RestTemplateBuilder builder) {
        return builder
                .uriTemplateHandler(new DefaultUriBuilderFactory(serverUrl + "/requests"))
                .requestFactory(() -> new HttpComponentsClientHttpRequestFactory())
                .build();
    }
}