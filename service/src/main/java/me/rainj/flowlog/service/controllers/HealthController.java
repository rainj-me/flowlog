package me.rainj.flowlog.service.controllers;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.server.ServerResponse;

import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/ping")
public class HealthController {
    
    @GetMapping
    public Mono<ServerResponse> ping() {
        return ServerResponse.ok().build();
    }
    
}
