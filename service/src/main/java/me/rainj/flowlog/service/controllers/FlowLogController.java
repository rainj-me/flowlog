package me.rainj.flowlog.service.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.server.ServerResponse;


import me.rainj.flowlog.domain.Message;
import me.rainj.flowlog.service.services.MessageService;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

@RestController
@RequestMapping("/flows")
public class FlowLogController {
    
    @Autowired
    private MessageService service;

    @GetMapping
    public Flux<Message> load(@RequestParam(name = "hour", required = true) Integer hour) {
        return service.loadMessageByHour(hour);
    }

    @PostMapping
    public Mono<ServerResponse> save(@RequestBody List<Message> messages) {
        Flux.fromIterable(messages).subscribe(service::sendMessage);
        return ServerResponse.status(HttpStatus.CREATED).build();
    }
}
