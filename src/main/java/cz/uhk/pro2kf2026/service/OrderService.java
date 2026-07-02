package cz.uhk.pro2kf2026.service;

import cz.uhk.pro2kf2026.dto.OrderRequest;
import cz.uhk.pro2kf2026.model.Order;

import java.util.List;

public interface OrderService {
    Order placeOrder(OrderRequest request);
    List<Order> getOrdersBySymbol(String symbol);
}
