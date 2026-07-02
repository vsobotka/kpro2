package cz.uhk.pro2kf2026.controller.rest;

import cz.uhk.pro2kf2026.dto.TransactionRequest;
import cz.uhk.pro2kf2026.model.Transaction;
import cz.uhk.pro2kf2026.service.TransactionService;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping("/api")
public class TransactionRestController {
    private final TransactionService transactionService;

    public TransactionRestController(TransactionService transactionService) {
        this.transactionService = transactionService;
    }

    @GetMapping("/transactions")
    public List<Transaction> getTransactions(Authentication auth) {
        return transactionService.getTransactions(auth.getName());
    }

    @PostMapping("/deposit")
    public void depositFunds(Authentication auth, @RequestBody TransactionRequest request) {
        try {
            transactionService.deposit(auth.getName(), request);
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }

    @PostMapping("/withdraw")
    public void withdrawFunds(Authentication auth, @RequestBody TransactionRequest request) {
        transactionService.withdraw(auth.getName(), request);
    }
}
