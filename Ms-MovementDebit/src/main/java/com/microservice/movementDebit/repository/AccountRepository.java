package com.microservice.movementDebit.repository;

import com.microservice.movementDebit.model.Account;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;

public interface AccountRepository extends ReactiveCrudRepository<Account, String> {
}
