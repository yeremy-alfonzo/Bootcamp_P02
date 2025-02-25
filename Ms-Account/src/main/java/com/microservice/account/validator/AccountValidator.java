package com.microservice.account.validator;

import com.microservice.account.entity.dto.Customer;
import com.microservice.account.exception.BusinessException;
import com.microservice.account.model.Account;
import com.microservice.account.repository.AccountRepository;
import com.microservice.account.repository.CustomerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

@Component
@RequiredArgsConstructor
public class AccountValidator {

    private final CustomerRepository customerRepository;
    private final AccountRepository accountRepository;
    private final CustomerRepository creditRepository;

    public Mono<Void> validate(Account account, Flux<Account> existingAccounts) {
        if (account.getAvailableBalanceAccount() < 0) {
            return Mono.error(new BusinessException("El monto de apertura no puede ser menor a 0."));
        }

        return customerRepository.findById(account.getIdCustomer())
                .switchIfEmpty(Mono.error(new BusinessException("El cliente no existe.")))
                .flatMap(customer -> validateAccountRestrictions(account, customer, existingAccounts));
    }

    private Mono<Void> validateAccountRestrictions(Account account, Customer customer, Flux<Account> existingAccounts) {
        return existingAccounts.collectList().flatMap(accounts -> {
            if ("PERSONAL".equalsIgnoreCase(customer.getTypeCustomer())) {
                return validatePersonalCustomer(accounts, account, customer);
            } else if ("EMPRESARIAL".equalsIgnoreCase(customer.getTypeCustomer())) {
                return validateBusinessCustomer(accounts, account, customer);
            }
            return Mono.empty();
        });
    }

    private Mono<Void> validatePersonalCustomer(List<Account> existingAccounts, Account newAccount, Customer customer) {
        long countAhorro = existingAccounts.stream()
                .filter(acc -> "AHORRO".equalsIgnoreCase(acc.getTypeAccount()))
                .count();
        long countCorriente = existingAccounts.stream()
                .filter(acc -> "CUENTA_CORRIENTE".equalsIgnoreCase(acc.getTypeAccount()))
                .count();

        if ("AHORRO".equalsIgnoreCase(newAccount.getTypeAccount()) && countAhorro > 0) {
            return Mono.error(new BusinessException("El cliente personal ya tiene una cuenta de ahorro."));
        }
        if ("CUENTA_CORRIENTE".equalsIgnoreCase(newAccount.getTypeAccount()) && countCorriente > 0) {
            return Mono.error(new BusinessException("El cliente personal ya tiene una cuenta corriente."));
        }

        if ("VIP".equalsIgnoreCase(customer.getTypeProfile())) {
            return validateVipAccount(newAccount, customer);
        }

        return Mono.empty();
    }

    private Mono<Void> validateVipAccount(Account account, Customer customer) {
        if (!"AHORRO".equalsIgnoreCase(account.getTypeAccount())) {
            return Mono.error(new BusinessException("Los clientes VIP solo pueden abrir cuentas de ahorro."));
        }

        return accountRepository.existsByIdCustomerAndTypeAccount(customer.getId(),"TARJETA_CREDITO")
                .flatMap(hasCreditCard -> hasCreditCard
                        ? Mono.empty()
                        : Mono.error(new BusinessException("El cliente VIP debe tener una tarjeta de crédito.")));
    }

    private Mono<Void> validateBusinessCustomer(List<Account> existingAccounts, Account newAccount, Customer customer) {
        if ("AHORRO".equalsIgnoreCase(newAccount.getTypeAccount()) ||
                "PLAZO_FIJO".equalsIgnoreCase(newAccount.getTypeAccount())) {
            return Mono.error(new BusinessException("Los clientes empresariales no pueden tener cuentas de ahorro o plazo fijo."));
        }

        if ("PYME".equalsIgnoreCase(customer.getTypeProfile())) {
            return validatePymeAccount(newAccount, customer);
        }

        return Mono.empty();
    }

    private Mono<Void> validatePymeAccount(Account account, Customer customer) {
        if (!"CUENTA_CORRIENTE".equalsIgnoreCase(account.getTypeAccount())) {
            return Mono.error(new BusinessException("Los clientes PYME solo pueden abrir cuentas corrientes."));
        }

        return accountRepository.existsByIdCustomerAndTypeAccount(customer.getId(),"TARJETA_CREDITO")
                .flatMap(hasCreditCard -> hasCreditCard
                        ? Mono.empty()
                        : Mono.error(new BusinessException("El cliente PYME debe tener una tarjeta de crédito.")));
    }
}