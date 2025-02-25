package com.microservice.account.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "account")
public class Account {

    @Id
    private String id;
    private String typeAccount;
    private String numberAccount;
    private String keyAccount;
    private double availableBalanceAccount;
    @JsonFormat(pattern="dd-MM-yyyy hh:mm:ss", timezone="GMT-05:00")
    private Date dateCreationAccount = new Date();
    private String statusAccount;
    private String idCustomer;

}
