package ru.practicum.shareit.configuration;

import org.springframework.beans.factory.annotation.Qualifier;
import ru.practicum.shareit.booking.BookingClient;
import ru.practicum.shareit.item.ItemClient;
import ru.practicum.shareit.itemRequest.ItemRequestClient;
import ru.practicum.shareit.user.UserClient;


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

    // 1. Создаём бины RestTemplate с уникальными именами и настройками

    @Bean("bookingRestTemplate")
    public RestTemplate bookingRestTemplate(RestTemplateBuilder builder) {
        return builder
                .uriTemplateHandler(new DefaultUriBuilderFactory(serverUrl + "/bookings"))
                .requestFactory(() -> new HttpComponentsClientHttpRequestFactory())
                .build();
    }

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

    // 2. Создаём бины клиентов, используя соответствующие RestTemplate

    @Bean
    public BookingClient bookingClient(@Qualifier("bookingRestTemplate") RestTemplate restTemplate) {
        return new BookingClient(restTemplate);
    }

    @Bean
    public ItemClient itemClient(@Qualifier("itemRestTemplate") RestTemplate restTemplate) {
        return new ItemClient(restTemplate);
    }

    @Bean
    public UserClient userClient(@Qualifier("userRestTemplate") RestTemplate restTemplate) {
        return new UserClient(restTemplate);
    }

    @Bean
    public ItemRequestClient itemRequestClient(@Qualifier("itemRequestRestTemplate") RestTemplate restTemplate) {
        return new ItemRequestClient(restTemplate);
    }
}