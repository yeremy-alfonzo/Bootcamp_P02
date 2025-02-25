package com.microservice.service;

import com.microservice.model.Credit;
import com.microservice.model.MovementCredit;
import com.microservice.repository.CreditRepository;
import com.microservice.repository.MovementCreditRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class MovementCreditService {

    private final MovementCreditRepository movementCreditRepository;
    private final CreditRepository creditRepository;

    public Flux<MovementCredit> getAllMovementCredits(){
        return movementCreditRepository.findAll();
    }
    public Mono<MovementCredit> getMovementCreditById(String id){
        return movementCreditRepository.findById(id);
    }
    public Mono<MovementCredit> createMovementCredit(MovementCredit movementCredit){
        return creditRepository.findById(movementCredit.getIdCredit())  // Buscar el crédito asociado
                .flatMap(credit -> {
                    if (movementCredit.getAmount() > 0) {
                        return processCreditPayment(credit, movementCredit);
                    } else {
                        return Mono.error(new IllegalArgumentException("El monto del pago debe ser mayor a 0."));
                    }
                });
    }
    public Mono<MovementCredit> updateMovementCredit(String id, MovementCredit movementCredit){
        return movementCreditRepository.findById(id)
                .flatMap(bean ->{
            bean.setAmount(movementCredit.getAmount());
            bean.setDateStart(movementCredit.getDateStart());
            bean.setDateLimit(movementCredit.getDateLimit());
            bean.setCommission(movementCredit.getCommission());
            bean.setDescription(movementCredit.getDescription());
            bean.setIdCredit(movementCredit.getIdCredit());
            return movementCreditRepository.save(bean);
        });
    }
    public Mono<MovementCredit> deleteMovementCredit(String id){
        return movementCreditRepository.findById(id)
                .flatMap(existMovementCredit -> movementCreditRepository.delete(existMovementCredit)
                        .then(Mono.just(existMovementCredit)));
    }


    private Mono<MovementCredit> processCreditPayment(Credit credit, MovementCredit movementCredit) {
        if (movementCredit.getAmount() > credit.getPendingAmount()) {
            return Mono.error(new IllegalArgumentException("El pago excede el saldo pendiente del crédito."));
        }
        credit.setPendingAmount(credit.getPendingAmount() - movementCredit.getAmount());

        return creditRepository.save(credit)
                .then(movementCreditRepository.save(movementCredit));
    }

}
