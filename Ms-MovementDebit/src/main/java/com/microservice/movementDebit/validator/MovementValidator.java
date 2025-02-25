package com.microservice.movementDebit.validator;

import lombok.Getter;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
@Getter
public class MovementValidator {
    private final Map<String, Integer> transactionLimits;

    public MovementValidator() {
        transactionLimits = new HashMap<>();
        transactionLimits.put("AHORRO", 5);
        transactionLimits.put("CUENTA_CORRIENTE", 10);
        transactionLimits.put("PLAZO_FIJO", 2);
    }

    public int getLimitForType(String type) {
        return transactionLimits.getOrDefault(type, 0);
    }
}
