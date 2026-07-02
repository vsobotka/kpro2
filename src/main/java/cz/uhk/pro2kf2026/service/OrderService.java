package cz.uhk.pro2kf2026.service;

import cz.uhk.pro2kf2026.dto.OrderRequest;
import cz.uhk.pro2kf2026.model.Order;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface OrderService {
    Order placeOrder(String username, OrderRequest request);
    List<Order> getOrdersBySymbol(String symbol);
}
