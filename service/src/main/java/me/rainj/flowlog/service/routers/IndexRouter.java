package me.rainj.flowlog.service.routers;

import java.net.URI;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.server.RequestPredicates;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;

@Configuration
public class IndexRouter {

    @Bean
    public RouterFunction<ServerResponse> home() {
        return RouterFunctions.route(RequestPredicates.GET("/"),
                (request) -> ServerResponse.permanentRedirect(URI.create("/apis.html")).build()
        );
    }
}
