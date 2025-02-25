package com.microservice.controller;

import com.microservice.model.Credit;
import com.microservice.service.CreditService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequiredArgsConstructor
@RequestMapping("/credit")
public class CreditController {

    private final CreditService creditService;

    @GetMapping(value = "/allCredits")
    public Mono<ResponseEntity<Flux<Credit>>>getAllCredit(){
        Flux<Credit> listCredit = this.creditService.getAllCredit();
        return Mono.just(ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(listCredit));
    }

    @GetMapping(value = "/{id}")
    public Mono<ResponseEntity<Credit>> getCreditById(@PathVariable String id){
        var credit = this.creditService.getCreditById(id);
        return credit.map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    @PostMapping(value = "/create")
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<Credit> createCredit(@RequestBody Credit credit){
        return this.creditService.createCredit(credit);
    }

}
