package com.microservice.controller;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.microservice.model.MovementCredit;
import com.microservice.service.MovementCreditService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Date;

@RestController
@RequiredArgsConstructor
@RequestMapping("/movementCredit")
public class MovementCreditController {

    private final MovementCreditService movementCreditService;

    @GetMapping(value = "/allMovementCredits")
    public Mono<ResponseEntity<Flux<MovementCredit>>> getAllMovementCredits(){
        Flux<MovementCredit> listMovementCredit=this.movementCreditService.getAllMovementCredits();
        return Mono.just(ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(listMovementCredit));
    }

    @GetMapping(value = "/{id}")
    public Mono<ResponseEntity<MovementCredit>> getMovementCreditById(@PathVariable String id){
        var movementCredit=this.movementCreditService.getMovementCreditById(id);
        return movementCredit.map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    @PostMapping(value = "/create")
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<ResponseEntity<MovementCredit>> create(@RequestBody MovementCredit movementCredit) {
        return movementCreditService.createMovementCredit(movementCredit)
                .map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.badRequest().build());
    }

    @PutMapping(value = "/update/{id}")
    public Mono<ResponseEntity<MovementCredit>> updateMovementCredit(@PathVariable String id, @RequestBody MovementCredit movementCredit){
        return this.movementCreditService.updateMovementCredit(id, movementCredit)
                .map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.badRequest().build());
    }

    @DeleteMapping(value = "/delete/{id}")
    public Mono<ResponseEntity<Void>> deleteMovementCredit(@PathVariable String id){
        return this.movementCreditService.deleteMovementCredit(id)
                .map(r -> ResponseEntity.ok().<Void>build())
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

}
