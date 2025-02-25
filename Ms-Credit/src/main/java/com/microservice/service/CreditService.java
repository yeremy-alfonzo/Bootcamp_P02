package com.microservice.service;

import com.microservice.model.Credit;
import com.microservice.repository.CreditRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CreditService {

    private final CreditRepository creditRepository;

    public Flux<Credit> getAllCredit(){
        return creditRepository.findAll();
    }

    public Mono<Credit> getCreditById(String id){
        return creditRepository.findById(id);
    }

    public Mono<Credit> createCredit(Credit credit) {
        return validateCreditRestrictions(credit)
                .then(creditRepository.save(credit));
    }

    private Mono<Void> validateCreditRestrictions(Credit credit) {
        return getCreditsByCustomerId(credit.getCustomerId())
                .collectList()
                .flatMap(existingCredits -> {
                    if ("PERSONAL".equalsIgnoreCase(credit.getTypeCredit())) {
                        return validatePersonalCredit(existingCredits);
                    } else if ("TARJETA_CREDITO".equalsIgnoreCase(credit.getTypeCredit())) {
                        return validateCreditCardFields(credit);
                    }
                    return Mono.empty();
                })
                .then(validateCVVFormat(credit.getCodeCVV()));
    }

    private Mono<Void> validatePersonalCredit(List<Credit> existingCredits) {
        long countPersonal = existingCredits.stream()
                .filter(c -> "PERSONAL".equalsIgnoreCase(c.getTypeCredit()))
                .count();
        if (countPersonal > 0) {
            return Mono.error(new IllegalArgumentException("El cliente ya tiene un crédito personal activo."));
        }
        return Mono.empty();
    }

    private Mono<Void> validateCreditCardFields(Credit credit) {
        if (credit.getIssuerBank() == null || credit.getIssuerBank().isEmpty()) {
            return Mono.error(new IllegalArgumentException("El banco emisor es obligatorio para tarjetas de crédito."));
        }
        if (credit.getCardIssuer() == null || credit.getCardIssuer().isEmpty()) {
            return Mono.error(new IllegalArgumentException("El emisor de la tarjeta es obligatorio."));
        }
        return Mono.empty();
    }

    private Mono<Void> validateCVVFormat(String codeCVV) {
        if (codeCVV != null && !codeCVV.matches("\\d{3,4}")) {
            return Mono.error(new IllegalArgumentException("El código CVV debe contener 3 o 4 dígitos numéricos."));
        }
        return Mono.empty();
    }

    // Metodo para obtener créditos por cliente
    public Flux<Credit> getCreditsByCustomerId(String customerId) {
        return creditRepository.findAll().filter(c -> c.getCustomerId().equals(customerId));
    }

}
