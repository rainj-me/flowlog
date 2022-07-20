package me.rainj.flowlog.service.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.reactive.function.server.ServerResponse;


import me.rainj.flowlog.domain.Message;
import me.rainj.flowlog.service.services.MessageService;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

/**
 * Flow log controller to process logs send by agent and load logs from data store.
 */
@RestController
@RequestMapping("/flows")
public class FlowLogController {

    /**
     * Message service.
     */
    @Autowired
    private MessageService service;

    /**
     * Load message from data store.
     * @param hour the hour of the message.
     * @return a set of flow log messages.
     */
    @GetMapping
    public Flux<Message> load(@RequestParam(name = "hour", required = true) Integer hour) {
        return service.loadMessageByHour(hour);
    }

    /**
     * Process the message.
     * @param messages the flowlog messages.
     */
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public void process(@RequestBody List<Message> messages) {
        Flux.fromIterable(messages).subscribe(service::sendMessage);
    }
}
