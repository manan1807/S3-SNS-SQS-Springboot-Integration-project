package com.reactivespring.router;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.reactivespring.domain.Review;
import com.reactivespring.exception.ReviewDataValidationException;
import com.reactivespring.repository.IReviewRepository;
import com.reactivespring.sns.SNSPublisher;
import com.reactivespring.sqs.Publisher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.publisher.Sinks;

import javax.validation.ConstraintViolation;
import javax.validation.Validator;
import java.util.stream.Collectors;

@Component
public class ReviewHandler {
    @Autowired
    private Validator validator;
   @Autowired
    private SNSPublisher snsPublisher;

    Sinks.Many<Review> reviewSinks = Sinks.many().replay().all();

    public ReviewHandler(IReviewRepository iReviewRepository) {
        this.iReviewRepository = iReviewRepository;
    }

    private IReviewRepository iReviewRepository;

    public Mono<ServerResponse> addReviews(ServerRequest request) {
        return request.bodyToMono(Review.class)
                .doOnNext(this::validate)
                .flatMap(iReviewRepository::save).log()
                .flatMap(ServerResponse.status(HttpStatus.CREATED)::bodyValue);
    }

    private void validate(Review review) {
        var constraintViolation =  validator.validate(review);
        if (constraintViolation.size() > 0){
            var collect = constraintViolation.stream()
                    .map(ConstraintViolation::getMessage)
                    .collect(Collectors.joining(","));
            throw new ReviewDataValidationException(collect);
        }
        snsPublisher.sendMessageToSQS(review);
    }

    public Mono<ServerResponse> fetchAllReviews(ServerRequest request) {
        var movieInfoId =  request.queryParam("movieInfoId");
        if(movieInfoId.isPresent()){
           var fluxReviews= iReviewRepository.findReviewsByMovieInfoId(Long.valueOf(movieInfoId.get())).log();
           return ServerResponse.ok().contentType(MediaType.APPLICATION_JSON).body(fluxReviews, Review.class);
        }else{
            final Flux<Review> all = iReviewRepository.findAll().log();
            return ServerResponse.ok().contentType(MediaType.APPLICATION_JSON).body(all, Review.class);
        }
    }

    public Mono<ServerResponse> updateReview(ServerRequest request) {
        final String id = request.pathVariable("id");
        return iReviewRepository.findById(id)
                .flatMap(review ->
                        request.bodyToMono(Review.class)
                                .map(review1 -> mappingReview(review, review1))
                                .flatMap(iReviewRepository::save)
                                .flatMap(savedReview -> ServerResponse.ok().bodyValue(savedReview))
                );
    }

    private Review mappingReview(Review review, Review review1) {
            review.setComment(review1.getComment());
            review.setRating(review1.getRating());
           return review;
    }

    public Mono<ServerResponse> deleteReview(ServerRequest request) {
        final String id = request.pathVariable("id");
        return iReviewRepository.deleteById(id)
                .flatMap(unused -> ServerResponse.noContent().build());
    }

    public Mono<ServerResponse> fetchAllReviewsStream(ServerRequest request) {
        return ServerResponse.ok()
                .contentType(MediaType.APPLICATION_NDJSON)
                .body(reviewSinks.asFlux(), Review.class);
    }

    public Mono<ServerResponse> addReviewsStream(ServerRequest request) {
        return request.bodyToMono(Review.class)
                .doOnNext(this::validate)
                .flatMap(iReviewRepository::save).log()
                .doOnNext(review -> reviewSinks.tryEmitNext(review))
                .flatMap(ServerResponse.status(HttpStatus.CREATED)::bodyValue);
    }
}