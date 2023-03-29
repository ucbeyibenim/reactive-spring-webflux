package com.reactivespring.moviesinfoservice.controller;

import com.reactivespring.moviesinfoservice.domain.MovieInfo;
import com.reactivespring.moviesinfoservice.repository.MovieInfoRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.util.UriComponentsBuilder;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@AutoConfigureWebTestClient
class MoviesInfoControllerIntgTest {
    @Autowired
    MovieInfoRepository movieInfoRepository;

    @Autowired
    WebTestClient webTestClient;

    static String MOVIE_INFO_URL = "/v1/movieinfos/";

    @BeforeEach
    void setUp() {
        var movieinfos = List.of(new MovieInfo(null, "Batman Begins",
                        2005, List.of("Christian Bale", "Michael Cane"), LocalDate.parse("2005-06-15")),
                new MovieInfo(null, "The Dark Knight",
                        2008, List.of("Christian Bale", "HeathLedger"), LocalDate.parse("2008-07-18")),
                new MovieInfo("abc", "Dark Knight Rises",
                        2012, List.of("Christian Bale", "Tom Hardy"), LocalDate.parse("2012-07-20")));

        movieInfoRepository.saveAll(movieinfos)
                .blockLast();
    }

    @AfterEach
    void tearDown() {
        movieInfoRepository.deleteAll().block();
    }

    @Test
    void addMovieInfo() {
        //given
        var movieInfo = new MovieInfo(null, "Batman Begins1",
                2005, List.of("Christian Bale", "Michael Cane"), LocalDate.parse("2005-06-15"));
        //when
        webTestClient
                .post()
                .uri(MOVIE_INFO_URL)
                .bodyValue(movieInfo)
                .exchange()
                .expectStatus()
                .isCreated()
                .expectBody(MovieInfo.class)
                .consumeWith(movieInfoEntityExchangeResult -> {
                   var savedMovieInfo = movieInfoEntityExchangeResult.getResponseBody();
                   assert savedMovieInfo != null;
                   assert savedMovieInfo.getMovieInfoId() != null;
                });
    }

    @Test
    void getAllMovieInfos(){
        webTestClient
                .get()
                .uri(MOVIE_INFO_URL)
                .exchange()
                .expectStatus()
                .is2xxSuccessful()
                .expectBodyList(MovieInfo.class)
                .hasSize(3);
    }
    @Test
    void getMovieInfosByYear(){
        var uri = UriComponentsBuilder.fromUriString(MOVIE_INFO_URL)
                        .queryParam("year", 2005)
                                .buildAndExpand().toUri();
        webTestClient
                .get()
                .uri(uri)
                .exchange()
                .expectStatus()
                .is2xxSuccessful()
                .expectBodyList(MovieInfo.class)
                .hasSize(1);
    }
    @Test
    void getMovieInfoById(){

        //given
        String name = "Dark Knight Rises";
        String movieId = "abc";

        //when
        webTestClient
                .get()
                .uri(MOVIE_INFO_URL + "/{id}", movieId)
                .exchange()
                .expectStatus()
                .is2xxSuccessful()
                .expectBody()
                .jsonPath("$.name").isEqualTo(name);
//                .expectBody(MovieInfo.class)
//                .consumeWith(movieInfoEntityExchangeResult -> {
//                   var movieInfo = movieInfoEntityExchangeResult.getResponseBody();
//                   assert movieInfo != null;
//                });
    }


    @Test
    void updateMovieInfo() {

        //given
        String name = "Batman Begins-Updated";
        String movieId = "abc";
        var movieInfo = new MovieInfo(null, name,
                2005, List.of("Christian Bale", "Michael Cane"), LocalDate.parse("2005-06-15"));
        //when
        webTestClient
                .put()
                .uri(MOVIE_INFO_URL + "/{id}", movieId)
                .bodyValue(movieInfo)
                .exchange()
                .expectStatus()
                .is2xxSuccessful()
                .expectBody(MovieInfo.class)
                .consumeWith(movieInfoEntityExchangeResult -> {
                    var updatedMovieInfo = movieInfoEntityExchangeResult.getResponseBody();
                    assert updatedMovieInfo != null;
                    assert updatedMovieInfo.getMovieInfoId() != null;
                    assert updatedMovieInfo.getName().equals(name);
                });
    }

    @Test
    void deleteMovieInfo() {
        //given
        String movieId = "abc";
        Long count = 2L;
        //when
        webTestClient
                .delete()
                .uri(MOVIE_INFO_URL + "/{id}", movieId)
                .exchange()
                .expectStatus()
                .isNoContent();
        var fluxMovieInfoList = movieInfoRepository.findAll().log();
        StepVerifier
                .create(fluxMovieInfoList)
                .expectNextCount(count)
                .verifyComplete();
    }


    @Test
    void updateMovieInfo_notfound() {

        //given
        String name = "Batman Begins-Updated";
        String movieId = "abcddddd";
        var movieInfo = new MovieInfo(null, name,
                2005, List.of("Christian Bale", "Michael Cane"), LocalDate.parse("2005-06-15"));
        //when
        webTestClient
                .put()
                .uri(MOVIE_INFO_URL + "/{id}", movieId)
                .bodyValue(movieInfo)
                .exchange()
                .expectStatus()
                .isNotFound();
    }
}