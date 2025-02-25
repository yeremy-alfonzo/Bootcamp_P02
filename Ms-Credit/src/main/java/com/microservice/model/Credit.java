package com.microservice.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Document
public class Credit {

    @Id
    private String id;
    private String customerId;
    private String issuerBank;
    private String typeCredit;
    private String dateDue;
    private String cardIssuer;
    private String codeCVV;

}
