package cz.uhk.pro2kf2026;

import cz.uhk.pro2kf2026.dto.OrderRequest;
import cz.uhk.pro2kf2026.model.Commodity;
import cz.uhk.pro2kf2026.model.Holding;
import cz.uhk.pro2kf2026.model.Order;
import cz.uhk.pro2kf2026.model.Transaction;
import cz.uhk.pro2kf2026.model.User;
import cz.uhk.pro2kf2026.repository.CommodityRepository;
import cz.uhk.pro2kf2026.repository.HoldingRepository;
import cz.uhk.pro2kf2026.repository.OrderRepository;
import cz.uhk.pro2kf2026.repository.TransactionRepository;
import cz.uhk.pro2kf2026.repository.UserRepository;
import cz.uhk.pro2kf2026.service.OrderServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrderServiceImplTest {

    @Mock private OrderRepository orderRepository;
    @Mock private UserRepository userRepository;
    @Mock private CommodityRepository commodityRepository;
    @Mock private HoldingRepository holdingRepository;
    @Mock private TransactionRepository transactionRepository;

    @InjectMocks private OrderServiceImpl orderService;

    private User alice;
    private User bob;
    private Commodity gold;

    @BeforeEach
    void setUp() {
        alice = user(1, "alice", "USER", 100000);
        bob = user(2, "bob", "USER", 50000);
        gold = commodity(1, "GOLD");
    }

    // ---- validation ----

    @Test
    void placeOrder_rejectsInvalidSide() {
        var ex = assertThrows(IllegalArgumentException.class,
                () -> orderService.placeOrder("alice", new OrderRequest(1, "hold", 10, 2010)));
        assertEquals("Side must be 'buy' or 'sell'", ex.getMessage());
    }

    @Test
    void placeOrder_rejectsNonPositiveQuantity() {
        var ex = assertThrows(IllegalArgumentException.class,
                () -> orderService.placeOrder("alice", new OrderRequest(1, "buy", 0, 2010)));
        assertEquals("Quantity must be positive", ex.getMessage());
    }

    @Test
    void placeOrder_rejectsNonPositivePrice() {
        var ex = assertThrows(IllegalArgumentException.class,
                () -> orderService.placeOrder("alice", new OrderRequest(1, "buy", 10, 0)));
        assertEquals("Price must be positive", ex.getMessage());
    }

    @Test
    void placeOrder_rejectsUnknownUser() {
        when(userRepository.findByUsername("ghost")).thenReturn(null);
        var ex = assertThrows(IllegalArgumentException.class,
                () -> orderService.placeOrder("ghost", new OrderRequest(1, "buy", 10, 2010)));
        assertEquals("Unknown user", ex.getMessage());
    }

    @Test
    void placeOrder_rejectsAdmin() {
        when(userRepository.findByUsername("admin")).thenReturn(user(9, "admin", "ADMIN", 0));
        var ex = assertThrows(IllegalArgumentException.class,
                () -> orderService.placeOrder("admin", new OrderRequest(1, "buy", 10, 2010)));
        assertEquals("Admins cannot trade", ex.getMessage());
    }

    @Test
    void placeOrder_rejectsInsufficientBalanceForBuy() {
        alice.setBalance(1000);
        when(userRepository.findByUsername("alice")).thenReturn(alice);
        when(commodityRepository.findById(1L)).thenReturn(gold);
        var ex = assertThrows(IllegalArgumentException.class,
                () -> orderService.placeOrder("alice", new OrderRequest(1, "buy", 10, 2010)));
        assertEquals("Insufficient balance", ex.getMessage());
    }

    @Test
    void placeOrder_rejectsInsufficientHoldingsForSell() {
        when(userRepository.findByUsername("alice")).thenReturn(alice);
        when(commodityRepository.findById(1L)).thenReturn(gold);
        when(holdingRepository.findByUser_UsernameAndCommodity_Symbol("alice", "GOLD"))
                .thenReturn(holding(alice, gold, 5));
        var ex = assertThrows(IllegalArgumentException.class,
                () -> orderService.placeOrder("alice", new OrderRequest(1, "sell", 10, 2010)));
        assertEquals("Insufficient holdings", ex.getMessage());
    }

    // ---- resting (no trade) ----

    @Test
    void placeOrder_restsWhenBookHasNoMatch() {
        when(userRepository.findByUsername("alice")).thenReturn(alice);
        when(commodityRepository.findById(1L)).thenReturn(gold);
        when(orderRepository.findByCommodity_SymbolAndSideOrderByPriceAsc("GOLD", "sell"))
                .thenReturn(List.of());
        when(orderRepository.save(any(Order.class))).thenAnswer(inv -> inv.getArgument(0));

        Order result = orderService.placeOrder("alice", new OrderRequest(1, "buy", 5, 2010));

        assertEquals(5, result.getQuantity());          // unchanged — nothing filled
        assertEquals(100000, alice.getBalance());        // no money moved
        verify(orderRepository).save(any(Order.class));  // the order rests in the book
        verify(transactionRepository, never()).save(any());
    }

    @Test
    void placeOrder_doesNotMatchOwnRestingOrder() {
        Order aliceSell = order(alice, gold, "sell", 2010, 10);
        when(userRepository.findByUsername("alice")).thenReturn(alice);
        when(commodityRepository.findById(1L)).thenReturn(gold);
        when(orderRepository.findByCommodity_SymbolAndSideOrderByPriceAsc("GOLD", "sell"))
                .thenReturn(List.of(aliceSell));
        when(orderRepository.save(any(Order.class))).thenAnswer(inv -> inv.getArgument(0));

        Order result = orderService.placeOrder("alice", new OrderRequest(1, "buy", 10, 2010));

        assertEquals(10, result.getQuantity());          // no self-trade, so it rests in full
        assertEquals(100000, alice.getBalance());
        verify(transactionRepository, never()).save(any());
    }

    // ---- matching / settlement ----

    @Test
    void placeOrder_fullyMatchesRestingSell() {
        Order bobSell = order(bob, gold, "sell", 2010, 10);
        Holding aliceHolding = holding(alice, gold, 50);
        Holding bobHolding = holding(bob, gold, 50);

        when(userRepository.findByUsername("alice")).thenReturn(alice);
        when(commodityRepository.findById(1L)).thenReturn(gold);
        when(orderRepository.findByCommodity_SymbolAndSideOrderByPriceAsc("GOLD", "sell"))
                .thenReturn(List.of(bobSell));
        when(holdingRepository.findByUser_UsernameAndCommodity_Symbol("alice", "GOLD")).thenReturn(aliceHolding);
        when(holdingRepository.findByUser_UsernameAndCommodity_Symbol("bob", "GOLD")).thenReturn(bobHolding);

        orderService.placeOrder("alice", new OrderRequest(1, "buy", 10, 2010));

        // cash: 10 * 2010 = 20100 moves from buyer to seller
        assertEquals(79900, alice.getBalance());
        assertEquals(70100, bob.getBalance());
        // holdings: 10 units move from seller to buyer
        assertEquals(60, aliceHolding.getQuantity());
        assertEquals(40, bobHolding.getQuantity());
        // the resting order is fully consumed and leaves the book
        verify(orderRepository).delete(bobSell);
        verify(orderRepository, never()).save(any(Order.class));  // incoming fully filled, never rests

        // one transaction per party, opposite signs
        ArgumentCaptor<Transaction> tx = ArgumentCaptor.forClass(Transaction.class);
        verify(transactionRepository, times(2)).save(tx.capture());
        Transaction buy = tx.getAllValues().stream().filter(t -> t.getType().equals("buy")).findFirst().orElseThrow();
        Transaction sell = tx.getAllValues().stream().filter(t -> t.getType().equals("sell")).findFirst().orElseThrow();
        assertEquals(-20100, buy.getChange());
        assertEquals(20100, sell.getChange());
    }

    @Test
    void placeOrder_partiallyFillsRestingSellAndKeepsRemainder() {
        Order bobSell = order(bob, gold, "sell", 2010, 10);
        Holding aliceHolding = holding(alice, gold, 50);
        Holding bobHolding = holding(bob, gold, 50);

        when(userRepository.findByUsername("alice")).thenReturn(alice);
        when(commodityRepository.findById(1L)).thenReturn(gold);
        when(orderRepository.findByCommodity_SymbolAndSideOrderByPriceAsc("GOLD", "sell"))
                .thenReturn(List.of(bobSell));
        when(holdingRepository.findByUser_UsernameAndCommodity_Symbol("alice", "GOLD")).thenReturn(aliceHolding);
        when(holdingRepository.findByUser_UsernameAndCommodity_Symbol("bob", "GOLD")).thenReturn(bobHolding);

        orderService.placeOrder("alice", new OrderRequest(1, "buy", 4, 2010));

        // only 4 filled: 4 * 2010 = 8040
        assertEquals(91960, alice.getBalance());
        assertEquals(58040, bob.getBalance());
        assertEquals(54, aliceHolding.getQuantity());
        assertEquals(46, bobHolding.getQuantity());
        // resting order keeps its remaining 6 and stays in the book (saved, not deleted)
        assertEquals(6, bobSell.getQuantity());
        verify(orderRepository).save(bobSell);
        verify(orderRepository, never()).delete(any(Order.class));
    }

    // ---- fixtures ----

    private User user(long id, String username, String role, Integer balance) {
        User u = new User();
        u.setId(id);
        u.setUsername(username);
        u.setRole(role);
        u.setBalance(balance);
        return u;
    }

    private Commodity commodity(long id, String symbol) {
        Commodity c = new Commodity();
        c.setId(id);
        c.setSymbol(symbol);
        return c;
    }

    private Order order(User user, Commodity commodity, String side, double price, double quantity) {
        Order o = new Order();
        o.setUser(user);
        o.setCommodity(commodity);
        o.setSide(side);
        o.setPrice(price);
        o.setQuantity(quantity);
        return o;
    }

    private Holding holding(User user, Commodity commodity, int quantity) {
        Holding h = new Holding();
        h.setUser(user);
        h.setCommodity(commodity);
        h.setQuantity(quantity);
        return h;
    }
}
