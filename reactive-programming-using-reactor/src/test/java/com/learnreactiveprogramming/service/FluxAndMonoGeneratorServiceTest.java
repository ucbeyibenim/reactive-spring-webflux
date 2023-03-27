package com.learnreactiveprogramming.service;

import org.junit.jupiter.api.Test;
import reactor.test.StepVerifier;

import static org.junit.jupiter.api.Assertions.*;

class FluxAndMonoGeneratorServiceTest {

    FluxAndMonoGeneratorService fluxAndMonoGeneratorService = new FluxAndMonoGeneratorService();

    @Test
    void namesFlux() {
        //given

        //when
        var namesFlux = fluxAndMonoGeneratorService.namesFlux();

        //then
        StepVerifier.create(namesFlux)
                //.expectNext("Hasan", "Ali", "Usmon")
                .expectNext("Hasan")
                .expectNextCount(2)
                .verifyComplete();
    }

    @Test
    void namesFlux_map() {
        //given

        //when
        var namesFlux = fluxAndMonoGeneratorService.namesFlux_map();

        //then
        StepVerifier.create(namesFlux)
                .expectNext("HASAN", "ALI", "USMON")
                //.expectNext("Hasan")
                //.expectNextCount(2)
                .verifyComplete();
    }

    @Test
    void namesFlux_immutability() {
        //given

        //when
        var namesFlux = fluxAndMonoGeneratorService.namesFlux_immutability();

        //then
        StepVerifier.create(namesFlux)
                .expectNext("HASAN", "ALI", "USMON")
                //.expectNext("Hasan")
                //.expectNextCount(2)
                .verifyComplete();
    }

    @Test
    void namesFlux_filter() {
        //given
        int stringLength = 3;

        //when
        var namesFlux = fluxAndMonoGeneratorService.namesFlux_filter(stringLength);

        //then
        StepVerifier.create(namesFlux)
                //.expectNext("HASAN", "ALI", "USMON")
                .expectNext("5-HASAN", "5-USMON")
                .verifyComplete();
    }

    @Test
    void namesFlux_flatMap() {
        //given
        int stringLength = 3;

        //when
        var namesFlux = fluxAndMonoGeneratorService.namesFlux_flatMap(stringLength);

        //then
        StepVerifier.create(namesFlux)
                .expectNext("H","A","S","A","N", "U","S","M","O","N")
                .verifyComplete();
    }

    @Test
    void namesFlux_flatMap_async() {
        //given
        int stringLength = 3;

        //when
        var namesFlux = fluxAndMonoGeneratorService.namesFlux_flatMap_async(stringLength);

        //then
        StepVerifier.create(namesFlux)
                //.expectNext("H","A","S","A","N", "U","S","M","O","N")
                .expectNextCount(10)
                .verifyComplete();
    }

    @Test
    void namesFlux_concatMap() {
        //given
        int stringLength = 3;

        //when
        var namesFlux = fluxAndMonoGeneratorService.namesFlux_concatMap(stringLength);

        //then
        StepVerifier.create(namesFlux)
                .expectNext("H","A","S","A","N", "U","S","M","O","N")
                //.expectNextCount(10)
                .verifyComplete();
    }
}