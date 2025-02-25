package com.microservice.account.controller;

import lombok.extern.slf4j.Slf4j;
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

import com.microservice.account.model.Account;
import com.microservice.account.service.AccountService;

import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/account")
public class AccountController{

    private final AccountService accountService;

    @GetMapping(value = "/allAccounts")
    public Mono<ResponseEntity<Flux<Account>>>getAllAccount() {
        Flux<Account> list=this.accountService.getAllAccount();
        return  Mono.just(ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(list));
    }

    @GetMapping(value = "/{id}")
    public Mono<ResponseEntity<Account>> getAccountById(@PathVariable String id){
        var account=this.accountService.getAccountById(id);
        return account.map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    @PostMapping(value = "/create")
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<Account> create(@RequestBody Account account){
        return this.accountService.createAccount(account);
    }

    @PutMapping(value = "/update/{id}")
    public Mono<ResponseEntity<Account>> updateAccountById(@PathVariable String id, @RequestBody Account account){

        return this.accountService.updateAccount(id,account)
                .map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.badRequest().build());
    }

    @DeleteMapping(value = "/delete/{id}")
    public Mono<ResponseEntity<Void>> deleteAccountById(@PathVariable String id){
        return this.accountService.deleteAccount(id)
                .map(r -> ResponseEntity.ok().<Void>build())
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }
}
