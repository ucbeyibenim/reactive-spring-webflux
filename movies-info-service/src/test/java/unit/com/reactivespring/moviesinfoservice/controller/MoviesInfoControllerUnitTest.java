package com.reactivespring.moviesinfoservice.controller;

import com.reactivespring.moviesinfoservice.domain.MovieInfo;
import com.reactivespring.moviesinfoservice.service.MoviesInfoService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import static org.mockito.ArgumentMatchers.isA;
import static org.mockito.Mockito.when;
import java.time.LocalDate;
import java.util.List;


@WebFluxTest(controllers = MoviesInfoController.class)
@AutoConfigureWebTestClient
public class MoviesInfoControllerUnitTest {
    @Autowired
    WebTestClient webTestClient;

    @MockBean
    private MoviesInfoService moviesInfoServiceMock;

    static String MOVIE_INFO_URL = "/v1/movieinfos/";

     @Test
    void getAllMoviesInfo(){
         var movieinfos = List.of(new MovieInfo(null, "Batman Begins",
                         2005, List.of("Christian Bale", "Michael Cane"), LocalDate.parse("2005-06-15")),
                 new MovieInfo(null, "The Dark Knight",
                         2008, List.of("Christian Bale", "HeathLedger"), LocalDate.parse("2008-07-18")),
                 new MovieInfo("abc", "Dark Knight Rises",
                         2012, List.of("Christian Bale", "Tom Hardy"), LocalDate.parse("2012-07-20")));

         when(moviesInfoServiceMock.getAllMovieInfos()).thenReturn(Flux.fromIterable(movieinfos));

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
    void addMovieInfo() {
        //given
        String mockId = "mockId";
        var movieInfo = new MovieInfo(null, "Batman Begins1",
                2005, List.of("Christian Bale", "Michael Cane"), LocalDate.parse("2005-06-15"));
        //when
        when(moviesInfoServiceMock.addMovieInfo(isA(MovieInfo.class))).thenReturn(
                Mono.just(new MovieInfo(mockId, "Batman Begins1",
                        2005, List.of("Christian Bale", "Michael Cane"), LocalDate.parse("2005-06-15")))
        );

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
                    assert mockId.equals(savedMovieInfo.getMovieInfoId());
                });
    }

    @Test
    void addMovieInfo_validation() {
        //given
        String mockId = "mockId";
        var movieInfo = new MovieInfo(null, "",
                -2005, List.of("Christian Bale", "Michael Cane"), LocalDate.parse("2005-06-15"));


        webTestClient
                .post()
                .uri(MOVIE_INFO_URL)
                .bodyValue(movieInfo)
                .exchange()
                .expectStatus()
                .isBadRequest()
                .expectBody(String.class)
                .consumeWith(stringEntityExchangeResult -> {
                    var responseBody = stringEntityExchangeResult.getResponseBody();
                    assert responseBody!=null;
                    System.out.println("responseBoyd: " + responseBody);
                });

    }


    @Test
    void updateMovieInfo() {

        //given
        String name = "Batman Begins-Updated";
        String mockId = "abc";
        var movieInfo = new MovieInfo(null, name,
                2005, List.of("Christian Bale", "Michael Cane"), LocalDate.parse("2005-06-15"));

        //when
        when(moviesInfoServiceMock.updateMovieInfo(isA(MovieInfo.class), isA(String.class))).thenReturn(
                Mono.just(new MovieInfo(mockId, "Batman Begins-Updated",
                        2005, List.of("Christian Bale", "Michael Cane"), LocalDate.parse("2005-06-15")))
        );

        //when
        webTestClient
                .put()
                .uri(MOVIE_INFO_URL + "/{id}", mockId)
                .bodyValue(movieInfo)
                .exchange()
                .expectStatus()
                .is2xxSuccessful()
                .expectBody(MovieInfo.class)
                .consumeWith(movieInfoEntityExchangeResult -> {
                    var updatedMovieInfo = movieInfoEntityExchangeResult.getResponseBody();
                    assert updatedMovieInfo != null;
                    assert updatedMovieInfo.getMovieInfoId() != null;
                    assert updatedMovieInfo.getMovieInfoId().equals(mockId);
                    assert updatedMovieInfo.getName().equals(name);
                });
    }

}
