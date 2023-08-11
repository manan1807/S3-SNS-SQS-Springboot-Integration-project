package com.reactivespring.routes;

import com.reactivespring.domain.Review;
import com.reactivespring.repository.IReviewRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.reactive.server.WebTestClient;

import java.util.List;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@AutoConfigureWebTestClient
public class ReviewsIntgTest {

    @Autowired
    WebTestClient webTestClient;

    @Autowired
    IReviewRepository iReviewRepository;

    @BeforeEach
    void setUP() {
        final List<Review> reviews = List.of(new Review(null, 1L, "Good", 9.0),
                new Review(null, 12L, "Good", 9.0),
                new Review("abc", 3L, "Good", 9.0));
        iReviewRepository.saveAll(reviews).blockLast();
    }

    @AfterEach
    void tearDown() {
        iReviewRepository.deleteAll().block();
    }

    @Test
    void addReview() {
        final Review review = new Review(null, 1L, "Good", 9.0);

        webTestClient.post()
                .uri("/v1/reviews")
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

        webTestClient.get()
                .uri("/v1/allreviews")
                .exchange()
                .expectStatus()
                .is2xxSuccessful()
                .expectBodyList(Review.class)
                .hasSize(3);


    }

    @Test
    void fetchReviewsById() {

        final Review average = new Review("",3L ,"Average", 7.0);

        webTestClient.put()
                .uri("/v1/updateReview/{id}", "abc")
                .bodyValue(average)
                .exchange()
                .expectStatus()
                .is2xxSuccessful()
                .expectBody(Review.class)
                .consumeWith(reviewEntityExchangeResult -> {
                    final Review responseBody = reviewEntityExchangeResult.getResponseBody();
                   assert  responseBody.getReviewId()!=null;
                   assert responseBody.getRating()!=null;
                   assert responseBody.getComment().equalsIgnoreCase("average");
                });
    }
    @Test
    void testDeleteReview(){
        webTestClient.delete()
                .uri("/v1/deleteReview/{id}","abc")
                .exchange()
                .expectStatus()
                .isOk();
    }
    @Test
    void fetchReviewById(){

        webTestClient.get()
                .uri(uriBuilder ->
                        uriBuilder
                                .path("/v1/reviews/all")
                                .queryParam("movieInfoId",3L)
                                .build())
                .accept(MediaType.TEXT_PLAIN)
                .exchange()
                .expectStatus()
                .isOk()
                .expectBodyList(Review.class)
                .hasSize(1);
    }
}
