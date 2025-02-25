package com.microservice.model;

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
@Document
public class MovementCredit {
    @Id
    private String id;
    private double amount;
    @JsonFormat(pattern="dd-MM-yyyy" , timezone="GMT-05:00")
    private Date dateStart = new Date();
    private String dateLimit;
    private String commission;
    private String description;
    private String idCredit;
}
