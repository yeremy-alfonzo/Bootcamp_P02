package com.microservice.customer.controller;

import com.microservice.customer.model.Customer;
import com.microservice.customer.service.CustomerService;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequiredArgsConstructor
@RequestMapping("/customer")
public class CustomerController {

    private final CustomerService customerService;

    @GetMapping(value = "/allCustomers")
    public Mono<ResponseEntity<Flux<Customer>>>getAllCustomer() {
        Flux<Customer> list=this.customerService.getAllCustomer();
        return  Mono.just(ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(list));
    }

    @GetMapping(value = "/{id}")
    public Mono<ResponseEntity<Customer>> getCustomerById(@PathVariable String id){
        var customer=this.customerService.getCustomerById(id);
        return customer.map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    @PostMapping(value = "/create")
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<Customer> create(@RequestBody Customer customer){
        return this.customerService.createCustomer(customer);
    }

    @PutMapping(value = "/update/{id}")
    public Mono<ResponseEntity<Customer>> updateCustomerById(@PathVariable String id, @RequestBody Customer customer){

        return this.customerService.updateCustomer(id,customer)
                .map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.badRequest().build());
    }

    @DeleteMapping(value = "/delete/{id}")
    public Mono<ResponseEntity<Void>> deleteCustomerById(@PathVariable String id){
        return this.customerService.deleteCustomer(id)
                .map(r -> ResponseEntity.ok().<Void>build())
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

}
