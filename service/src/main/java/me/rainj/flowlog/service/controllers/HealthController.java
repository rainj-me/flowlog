package me.rainj.flowlog.service.controllers;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.server.ServerResponse;

import reactor.core.publisher.Mono;


/**
 * Health check controller to check whether the service is still up.
 */
@RestController
@RequestMapping("/ping")
public class HealthController {

    /**
     * Health check request.
     */
    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public void ping() {}
    
}
