package com.microservice.account.repository;

import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;

import com.microservice.account.model.Account;
import reactor.core.publisher.Mono;

@Repository
public interface AccountRepository extends ReactiveMongoRepository<Account, String> {
    Mono<Boolean> existsByIdCustomerAndTypeAccount(String idCustomer, String typeAccount);

}
