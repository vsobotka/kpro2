package cz.uhk.pro2kf2026.service;

import cz.uhk.pro2kf2026.dto.TransactionRequest;
import cz.uhk.pro2kf2026.model.Transaction;

import java.util.List;

public interface TransactionService {
    void deposit(String username, TransactionRequest request);
    void withdraw(String username, TransactionRequest request);
    List<Transaction> getTransactions(String username);
}
