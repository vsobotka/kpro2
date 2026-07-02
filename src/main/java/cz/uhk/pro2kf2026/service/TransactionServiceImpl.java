package cz.uhk.pro2kf2026.service;

import cz.uhk.pro2kf2026.dto.TransactionRequest;
import cz.uhk.pro2kf2026.model.Transaction;
import cz.uhk.pro2kf2026.model.User;
import cz.uhk.pro2kf2026.repository.TransactionRepository;
import cz.uhk.pro2kf2026.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TransactionServiceImpl implements TransactionService {
    private final UserRepository userRepository;
    private final TransactionRepository transactionRepository;

    public TransactionServiceImpl(UserRepository userRepository, TransactionRepository transactionRepository) {
        this.userRepository = userRepository;
        this.transactionRepository = transactionRepository;
    }

    @Override
    @Transactional
    public void deposit(String username, TransactionRequest request) {
        User user = userRepository.findByUsername(username);
        if (user == null) {
            throw new IllegalArgumentException("Unknown user");
        }

        if ("ADMIN".equalsIgnoreCase(user.getRole())) {
            throw new IllegalArgumentException("Admins cannot deposit funds");
        }

        if (request.amount() <= 0) {
            throw new IllegalArgumentException("Amount must be positive");
        }

        Transaction transaction = new Transaction();
        transaction.setUser(user);
        transaction.setChange(request.amount());
        transaction.setType("deposit");
        transactionRepository.save(transaction);

        user.setBalance(user.getBalance() + request.amount());
        userRepository.save(user);
    }

    @Override
    @Transactional
    public void withdraw(String username, TransactionRequest request) {
        User user = userRepository.findByUsername(username);
        if (user == null) {
            throw new IllegalArgumentException("Unknown user");
        }

        if ("ADMIN".equalsIgnoreCase(user.getRole())) {
            throw new IllegalArgumentException("Admins cannot withdraw funds");
        }

        if (request.amount() <= 0) {
            throw new IllegalArgumentException("Amount must be positive");
        }

        Transaction transaction = new Transaction();
        transaction.setUser(user);
        transaction.setChange(-request.amount());
        transaction.setType("withdraw");
        transactionRepository.save(transaction);

        user.setBalance(user.getBalance() - request.amount());
        userRepository.save(user);
    }

    @Override
    public List<Transaction> getTransactions(String username) {
        return transactionRepository.findByUser_Username(username);
    }
}
