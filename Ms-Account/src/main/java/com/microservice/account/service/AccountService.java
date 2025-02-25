package com.microservice.account.service;

import com.microservice.account.exception.BusinessException;
import com.microservice.account.validator.AccountValidator;
import org.springframework.stereotype.Service;
import com.microservice.account.model.Account;
import com.microservice.account.repository.AccountRepository;

import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AccountService {

    private  final AccountRepository accountRepository;
    private final AccountValidator accountValidator;

    public Flux<Account> getAllAccount(){
        return accountRepository.findAll();
    }

    public Flux<Account> getFindAccountByIdCustomer(String idCustomer){
        return accountRepository.findAll().filter(x -> x.getIdCustomer().equals(idCustomer));
    }

    public Mono<Account> getAccountById(String id){
        return  accountRepository.findById(id);
    }
    public Mono<Account> createAccount(Account account){
        if (account.getAvailableBalanceAccount() < 0) {
            return Mono.error(new BusinessException("El monto de apertura no puede ser menor a 0."));
        }
        return validateAccountRestrictions(account)
               .then(accountRepository.save(account));
    }
    public Mono<Account> updateAccount(String id,  Account account){
        return accountRepository.findById(id)
                .flatMap(bean -> {
                    bean.setTypeAccount(account.getTypeAccount());
                    bean.setNumberAccount(account.getNumberAccount());
                    bean.setKeyAccount(account.getKeyAccount());
                    bean.setAvailableBalanceAccount(account.getAvailableBalanceAccount());
                    bean.setStatusAccount(account.getStatusAccount());
                    bean.setIdCustomer(account.getIdCustomer());
                    return accountRepository.save(bean);
                });
    }
    public Mono<Account> deleteAccount(String id) {
        return accountRepository.findById(id)
                .flatMap(account -> {
                    if (account.getAvailableBalanceAccount() > 0) {
                        return Mono.error(new IllegalArgumentException("No se puede eliminar la cuenta porque aún tiene saldo disponible."));
                    }
                    return accountRepository.delete(account).then(Mono.just(account));
                });
    }


    private Mono<Void> validateAccountRestrictions(Account account) {
        return getFindAccountByIdCustomer(account.getIdCustomer())
                .collectList()
                .flatMap(existingAccounts -> {
                    if ("PERSONAL".equalsIgnoreCase(account.getTypeAccount())) {
                        return validatePersonalCustomer(existingAccounts, account);
                    } else if ("EMPRESARIAL".equalsIgnoreCase(account.getTypeAccount())) {
                        return validateBusinessCustomer(existingAccounts, account);
                    }
                    return Mono.empty();
                });
    }

    private Mono<Void> validatePersonalCustomer(List<Account> existingAccounts, Account newAccount) {
        long countAhorro = existingAccounts.stream()
                .filter(acc -> "AHORRO".equalsIgnoreCase(acc.getTypeAccount()))
                .count();
        long countCorriente = existingAccounts.stream()
                .filter(acc -> "CUENTA_CORRIENTE".equalsIgnoreCase(acc.getTypeAccount()))
                .count();

        if ("AHORRO".equalsIgnoreCase(newAccount.getTypeAccount()) && countAhorro > 0) {
            return Mono.error(new IllegalArgumentException("El cliente personal ya tiene una cuenta de ahorro."));
        }
        if ("CUENTA_CORRIENTE".equalsIgnoreCase(newAccount.getTypeAccount()) && countCorriente > 0) {
            return Mono.error(new IllegalArgumentException("El cliente personal ya tiene una cuenta corriente."));
        }
        return Mono.empty();
    }

    private Mono<Void> validateBusinessCustomer(List<Account> existingAccounts, Account newAccount) {
        if ("AHORRO".equalsIgnoreCase(newAccount.getTypeAccount()) ||
                "PLAZO_FIJO".equalsIgnoreCase(newAccount.getTypeAccount())) {
            return Mono.error(new IllegalArgumentException("Los clientes empresariales no pueden tener cuentas de ahorro o plazo fijo."));
        }
        return Mono.empty();
    }

}
