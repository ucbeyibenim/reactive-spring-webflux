package com.reactivespring.moviesinfoservice.repository;

import com.reactivespring.moviesinfoservice.domain.MovieInfo;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.test.context.ActiveProfiles;
import reactor.test.StepVerifier;

import java.time.LocalDate;
import java.util.List;


@DataMongoTest
@ActiveProfiles("test")
class MovieInfoRepositoryIntgTest {

    @Autowired
    MovieInfoRepository movieInfoRepository;

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
    void findAll() {
        //given

        //when
        var moviesInfoFlux = movieInfoRepository.findAll().log();

        //then
        StepVerifier.create(moviesInfoFlux)
                .expectNextCount(3)
                .verifyComplete();
    }
    @Test
    void findById() {
        //given
        String movieId = "abc";

        //when
        var moviesInfoMono = movieInfoRepository.findById(movieId).log();

        //then
        StepVerifier.create(moviesInfoMono)
                .assertNext(movieInfo -> {
                    assert "Dark Knight Rises".equals(movieInfo.getName());
                })
                .verifyComplete();
    }
    @Test
    void save() {
        //given
        MovieInfo movieInfo = new MovieInfo(null, "Batman Begins2",
                2005, List.of("Christian Bale", "Michael Cane"), LocalDate.parse("2007-07-15"));

        //when
        var moviesInfoMono = movieInfoRepository.save(movieInfo).log();

        //then
        StepVerifier.create(moviesInfoMono)
                .assertNext(movie -> {
                    assert movie.getMovieInfoId() != null;
                    assert "Batman Begins2".equals(movie.getName());
                })
                .verifyComplete();
    }
    @Test
    void update() {
        //given
        String id = "abc";
        Integer year = 2021;
        var movieInfo = movieInfoRepository.findById(id).block();
        assert movieInfo != null;
        movieInfo.setYear(year);
        //when
        var moviesInfoMono = movieInfoRepository.save(movieInfo).log();

        //then
        StepVerifier.create(moviesInfoMono)
                .assertNext(movie -> {
                    assert movie.getYear().equals(year);
                })
                .verifyComplete();
    }

    @Test
    void deleteById() {
        //given
        String id = "abc";
        //when
        movieInfoRepository.deleteById(id).block();
        var moviesInfoFlux = movieInfoRepository.findAll().log();

        //then
        StepVerifier.create(moviesInfoFlux)
                .expectNextCount(2)
                .verifyComplete();
    }

    @Test
    void findByYear() {
        //given

        //when
        var moviesInfoFlux = movieInfoRepository.findByYear(2005).log();

        //then
        StepVerifier.create(moviesInfoFlux)
                .expectNextCount(1)
                .verifyComplete();
    }
}