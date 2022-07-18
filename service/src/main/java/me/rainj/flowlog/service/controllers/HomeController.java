package me.rainj.flowlog.service.controllers;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.result.view.RedirectView;

@RestController
@RequestMapping("/")
public class HomeController {
    
    public RedirectView home() {
        return new RedirectView("/apis.html", HttpStatus.PERMANENT_REDIRECT);
    }
    
}
