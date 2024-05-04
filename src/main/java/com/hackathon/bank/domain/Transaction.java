package com.hackathon.bank.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Data
@NoArgsConstructor
public class Transaction {

    @Id
    @GeneratedValue
    private Long id;

    @Column(name="device_code")
    private String deviceCode;

    @Column(name="oper_date_time")
    private LocalDate operDate;

    @Column(name="curr")
    private String curr;

    @Column(name="amnt")
    private BigDecimal amount;

    @Column(name="card_number")
    private String cardNumber;


    public Transaction(String deviceCode, LocalDate operDate, double amount, String currency, String cardNumber) {
        this.deviceCode = deviceCode;
        this.operDate = operDate;
        this.amount = new BigDecimal(amount);
        this.curr = currency;
        this.cardNumber = cardNumber;
    }
}
