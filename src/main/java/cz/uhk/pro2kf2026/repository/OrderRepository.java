package cz.uhk.pro2kf2026.repository;

import cz.uhk.pro2kf2026.model.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
    List<Order> findByCommodity_Symbol(String symbol);
    List<Order> findByUser_Username(String username);
    List<Order> findByCommodity_SymbolAndSideOrderByPriceDesc(String symbol, String side); // buys
    List<Order> findByCommodity_SymbolAndSideOrderByPriceAsc(String symbol, String side);  // sells
}
