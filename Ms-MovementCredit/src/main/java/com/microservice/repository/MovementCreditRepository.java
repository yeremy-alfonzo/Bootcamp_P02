package com.microservice.repository;

import com.microservice.model.MovementCredit;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MovementCreditRepository extends ReactiveCrudRepository<MovementCredit, String> {
}
