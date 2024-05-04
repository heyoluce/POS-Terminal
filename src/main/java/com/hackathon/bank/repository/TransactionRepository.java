package com.hackathon.bank.repository;

import com.hackathon.bank.domain.Curr;
import com.hackathon.bank.domain.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long> {

    List<Transaction> findByCurr(String curr);

    List<Transaction> findByDeviceCode(String deviceCode);

    List<Transaction> findByOperDate(LocalDate date);

    List<Transaction> findByDeviceCodeAndCurrAndOperDate(String deviceCode, String currency, LocalDate date);

    List<Transaction> findByDeviceCodeAndOperDate(String deviceCode, LocalDate date);

}
