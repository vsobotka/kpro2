package cz.uhk.pro2kf2026.controller.rest;

import cz.uhk.pro2kf2026.dto.OrderRequest;
import cz.uhk.pro2kf2026.model.Order;
import cz.uhk.pro2kf2026.service.OrderService;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping("/api/orders")
public class OrderRestController {
    private final OrderService orderService;

    public OrderRestController(OrderService orderService) {
        this.orderService = orderService;
    }

    @GetMapping("/{symbol}")
    public List<Order> getOrdersBySymbol(@PathVariable String symbol) {
        return orderService.getOrdersBySymbol(symbol);
    }

    @PostMapping
    public Order create(Authentication auth, @RequestBody OrderRequest request) {
        try {
            return orderService.placeOrder(auth.getName(), request);
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }
}
