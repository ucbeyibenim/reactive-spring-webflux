package com.learnreactiveprogramming.service;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.List;
import java.util.Random;

public class FluxAndMonoGeneratorService {
    public Flux<String> namesFlux(){
        return Flux.fromIterable(List.of("Hasan", "Ali", "Usmon")).log(); //db or remote service call
    }

    public Mono<String> nameMono(){
        return Mono.just("Umar").log();
    }

    public Flux<String> namesFlux_map(){
        return Flux.fromIterable(List.of("Hasan", "Ali", "Usmon"))
                .map(String::toUpperCase)
                //.map(s ->s.toUpperCase())
                .log(); //db or remote service call
    }

    public Flux<String> namesFlux_immutability(){
        var namesFlux =  Flux.fromIterable(List.of("Hasan", "Ali", "Usmon"));
        var namesFlux2 = namesFlux.map(String::toUpperCase);
        return namesFlux2;

    }


    public Flux<String> namesFlux_filter(int stringLength){
        return Flux.fromIterable(List.of("Hasan", "Ali", "Usmon"))
                .map(String::toUpperCase)
                //.map(s ->s.toUpperCase())
                .filter(s->s.length()>stringLength)
                .map(s->s.length() + "-" + s)//5-HASAN, 5-USMON
                .log(); //db or remote service call
    }

    public Flux<String> namesFlux_flatMap(int stringLength){
        return Flux.fromIterable(List.of("Hasan", "Ali", "Usmon"))
                .map(String::toUpperCase)
                .filter(s->s.length()>stringLength)
                .flatMap(s->splitString(s))
                .log();
    }

    public Flux<String> splitString(String name){
        var charArray = name.split("");
        return Flux.fromArray(charArray);
    }
    public Flux<String> namesFlux_flatMap_async(int stringLength){
        return Flux.fromIterable(List.of("Hasan", "Ali", "Usmon"))
                .map(String::toUpperCase)
                .filter(s->s.length()>stringLength)
                .flatMap(s->splitStringDelay(s))
                .log();
    }
    public Flux<String> namesFlux_concatMap(int stringLength){
        return Flux.fromIterable(List.of("Hasan", "Ali", "Usmon"))
                .map(String::toUpperCase)
                .filter(s->s.length()>stringLength)
                .concatMap(s->splitStringDelay(s))
                .log();
    }

    public Flux<String> splitStringDelay(String name){
        var charArray = name.split("");
        var delay = new Random().nextInt(1000);
        return Flux.fromArray(charArray).delayElements(Duration.ofMillis(delay));
    }

    public static void main(String[] args) {
        FluxAndMonoGeneratorService fluxAndMonoGeneratorService = new FluxAndMonoGeneratorService();
        fluxAndMonoGeneratorService.namesFlux()
                .subscribe(name -> {
                    System.out.println("Flux name is : " + name);
                });
        fluxAndMonoGeneratorService.nameMono()
                .subscribe(name -> {
                    System.out.println("Mono name : " + name);
                });
    }
}
