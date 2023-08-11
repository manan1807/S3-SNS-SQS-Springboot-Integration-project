package com.reactivespring.routes;

import com.reactivespring.domain.Review;
import com.reactivespring.exceptionhandler.GlobalExceptionHandler;
import com.reactivespring.repository.IReviewRepository;
import com.reactivespring.router.ReviewHandler;
import com.reactivespring.router.ReviewRouter;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

import static org.mockito.Mockito.when;


@WebFluxTest
@ContextConfiguration(classes = {ReviewRouter.class, ReviewHandler.class, GlobalExceptionHandler.class})
@AutoConfigureWebTestClient
public class ReviewsUnitTest {

    @MockBean
    private IReviewRepository iReviewRepository;

    @Autowired
    private WebTestClient webTestClient;

    @Test
    void addReview() {
        final Review review = new Review(null, null, "Good", 9.0);
        when(iReviewRepository.save(review))
                .thenReturn(Mono.just(new Review("abc", 1L, "Average", 10.0)));

        webTestClient.post()
                .uri("/v1/reviews/add")
                .bodyValue(review)
                .exchange()
                .expectStatus()
                .isCreated()
                .expectBody(Review.class)
                .consumeWith(reviewEntityExchangeResult -> {
                    final Review savedReview = reviewEntityExchangeResult.getResponseBody();
                    assert savedReview != null;
                    assert savedReview.getReviewId() != null;
                });
    }

    @Test
    void fetchAllReviews() {
        when(iReviewRepository.findAll()).thenReturn(Flux.fromIterable(List.of(new Review(null, 1L, "Good", 9.0),
                new Review(null, 12L, "Good", 9.0),
                new Review("abc", 3L, "Good", 9.0))));
        webTestClient.get()
                .uri("/v1/all")
                .exchange()
                .expectStatus()
                .is2xxSuccessful()
                .expectBodyList(Review.class)
                .hasSize(3);


    }

    @Test
    void addReview_validation() {
        final Review review = new Review(null, null, "Good", 9.0);
        when(iReviewRepository.save(review))
                .thenReturn(Mono.just(new Review("abc", 1L, "Average", 10.0)));

        webTestClient.post()
                .uri("/v1/reviews/add")
                .bodyValue(review)
                .exchange()
                .expectStatus()
                .isBadRequest();
    }
    @Test
    void fetchReviewsPerMovies(){
        when(iReviewRepository.findReviewsByMovieInfoId(1L))
                .thenReturn(
                        Flux.fromIterable(List.of(new Review(null, 12L, "Good", 9.0),
                                new Review("abc", 3L, "Good", 9.0)))
                );

        webTestClient.get()
                .uri(uriBuilder ->
                    uriBuilder.path("/v1/reviews/all")
                            .queryParam("movieInfoId","1")
                            .build()
                )
                .exchange()
                .expectStatus()
                .is2xxSuccessful()
                .expectBodyList(Review.class)
                .hasSize(2);

    }}
