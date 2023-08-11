package com.reactivespring.router;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerResponse;

import static org.springframework.web.reactive.function.server.RouterFunctions.route;

@Configuration
public class ReviewRouter {

    private static final String ROOT_PATH = "/v1/reviews";

    @Bean
    public RouterFunction<ServerResponse> reviewsRoute(ReviewHandler reviewHandler){
        return route()
                .GET(ROOT_PATH,(request -> ServerResponse.ok().bodyValue("heyyy")))
                .POST(ROOT_PATH+"/add", reviewHandler::addReviews)
                .GET(ROOT_PATH+"/all", reviewHandler::fetchAllReviews)
                .PUT(ROOT_PATH+"/update/{id}", reviewHandler::updateReview)
                .DELETE(ROOT_PATH+"/delete/{id}", reviewHandler::deleteReview)
                .POST(ROOT_PATH+"/addStream", reviewHandler::addReviewsStream)
                .GET(ROOT_PATH+"/allStream", reviewHandler::fetchAllReviewsStream)
                .build();
    }
}

