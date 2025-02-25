package com.microservice.movementDebit.controller;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.microservice.movementDebit.model.MovementDebit;
import com.microservice.movementDebit.service.MovementDebitService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Date;

@RestController
@RequiredArgsConstructor
@RequestMapping("/movementDebit")
public class MovementDebitController {

    private final MovementDebitService movementDebitService;

    @GetMapping(value = "/allMovementCredits")
    public Mono<ResponseEntity<Flux<MovementDebit>>>getAllCreditMovement() {
        Flux<MovementDebit> list=this.movementDebitService.getAllCreditMovement();
        return  Mono.just(ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(list));
    }

    @GetMapping(value = "/{id}")
    public Mono<ResponseEntity<MovementDebit>> getCreditMovementById(@PathVariable String id){
        var creditMovement=this.movementDebitService.getCreditMovementById(id);
        return creditMovement.map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    @PostMapping(value = "/create")
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<ResponseEntity<MovementDebit>> create(@RequestBody MovementDebit movementDebit) {
        return movementDebitService.createCreditMovement(movementDebit)
                .map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.badRequest().build());
    }

    @PutMapping(value = "/update/{id}")
    public Mono<ResponseEntity<MovementDebit>> updateCreditMovementById(@PathVariable String id, @RequestBody MovementDebit movementDebit){

        return this.movementDebitService.updateCreditMovement(id,movementDebit)
                .map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.badRequest().build());
    }

    @DeleteMapping(value = "/delete/{id}")
    public Mono<ResponseEntity<Void>> deleteCreditMovementById(@PathVariable String id){
        return this.movementDebitService.deleteCreditMovement(id)
                .map(r -> ResponseEntity.ok().<Void>build())
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

}
