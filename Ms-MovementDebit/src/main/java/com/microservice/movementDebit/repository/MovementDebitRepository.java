package com.microservice.movementDebit.repository;

import com.microservice.movementDebit.model.MovementDebit;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Mono;

public interface MovementDebitRepository extends ReactiveCrudRepository<MovementDebit, String>{
    Mono<Long> countByIdAccount(String idAccount);
}
