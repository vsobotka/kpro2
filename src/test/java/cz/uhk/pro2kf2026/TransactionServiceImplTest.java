package cz.uhk.pro2kf2026;

import cz.uhk.pro2kf2026.dto.TransactionRequest;
import cz.uhk.pro2kf2026.model.Transaction;
import cz.uhk.pro2kf2026.model.User;
import cz.uhk.pro2kf2026.repository.TransactionRepository;
import cz.uhk.pro2kf2026.repository.UserRepository;
import cz.uhk.pro2kf2026.service.TransactionServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TransactionServiceImplTest {

    @Mock private UserRepository userRepository;
    @Mock private TransactionRepository transactionRepository;

    @InjectMocks private TransactionServiceImpl transactionService;

    private User alice;

    @BeforeEach
    void setUp() {
        alice = new User();
        alice.setId(1);
        alice.setUsername("alice");
        alice.setRole("USER");
        alice.setBalance(100);
    }

    @Test
    void deposit_increasesBalanceAndRecordsPositiveTransaction() {
        when(userRepository.findByUsername("alice")).thenReturn(alice);

        transactionService.deposit("alice", new TransactionRequest(50));

        assertEquals(150, alice.getBalance());
        ArgumentCaptor<Transaction> tx = ArgumentCaptor.forClass(Transaction.class);
        verify(transactionRepository).save(tx.capture());
        assertEquals(50, tx.getValue().getChange());
        assertEquals("deposit", tx.getValue().getType());
        verify(userRepository).save(alice);
    }

    @Test
    void withdraw_decreasesBalanceAndRecordsNegativeTransaction() {
        when(userRepository.findByUsername("alice")).thenReturn(alice);

        transactionService.withdraw("alice", new TransactionRequest(30));

        assertEquals(70, alice.getBalance());
        ArgumentCaptor<Transaction> tx = ArgumentCaptor.forClass(Transaction.class);
        verify(transactionRepository).save(tx.capture());
        assertEquals(-30, tx.getValue().getChange());
        assertEquals("withdraw", tx.getValue().getType());
    }

    @Test
    void deposit_rejectsNonPositiveAmount() {
        when(userRepository.findByUsername("alice")).thenReturn(alice);
        var ex = assertThrows(IllegalArgumentException.class,
                () -> transactionService.deposit("alice", new TransactionRequest(0)));
        assertEquals("Amount must be positive", ex.getMessage());
        assertEquals(100, alice.getBalance());   // untouched
        verify(transactionRepository, never()).save(any());
    }

    @Test
    void deposit_rejectsAdmin() {
        User admin = new User();
        admin.setUsername("admin");
        admin.setRole("ADMIN");
        admin.setBalance(0);
        when(userRepository.findByUsername("admin")).thenReturn(admin);

        assertThrows(IllegalArgumentException.class,
                () -> transactionService.deposit("admin", new TransactionRequest(50)));
        verify(transactionRepository, never()).save(any());
    }

    @Test
    void deposit_rejectsUnknownUser() {
        when(userRepository.findByUsername("ghost")).thenReturn(null);
        var ex = assertThrows(IllegalArgumentException.class,
                () -> transactionService.deposit("ghost", new TransactionRequest(50)));
        assertEquals("Unknown user", ex.getMessage());
    }
}
