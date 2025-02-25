package com.microservice.repository;

import com.microservice.model.Credit;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CreditRepository extends ReactiveCrudRepository<Credit, String> {
}
