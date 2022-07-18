package me.rainj.flowlog.service.routers;

import java.util.NoSuchElementException;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.web.server.WebExceptionHandler;

import reactor.core.publisher.Mono;

@Configuration
public class CustomErrorsHandler {
    
    @Bean
    public WebExceptionHandler exceptionHandler() {
        return (exchange, throwable) -> {
            ServerHttpResponse response = exchange.getResponse();
            if (throwable instanceof NoSuchElementException) {
                response.setStatusCode(HttpStatus.NOT_FOUND);
            } else {
            response.setStatusCode(HttpStatus.BAD_REQUEST);
            }
            return Mono.empty();
        };
    }
}
