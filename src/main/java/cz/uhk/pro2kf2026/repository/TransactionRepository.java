package cz.uhk.pro2kf2026.repository;

import cz.uhk.pro2kf2026.model.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long> {
    List<Transaction> findByUser_Username(String username);
}
