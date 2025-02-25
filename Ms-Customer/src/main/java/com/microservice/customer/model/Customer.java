package com.microservice.customer.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "customers")
public class Customer {

    @Id
    private String id;
    private String name;
    private String lastName;
    private String typeDocument;
    private String numberDocument;
    private String typeCustomer;
    private String typeProfile;
}
