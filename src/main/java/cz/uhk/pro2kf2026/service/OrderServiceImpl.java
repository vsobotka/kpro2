package cz.uhk.pro2kf2026.service;

import cz.uhk.pro2kf2026.dto.OrderRequest;
import cz.uhk.pro2kf2026.model.Commodity;
import cz.uhk.pro2kf2026.model.Order;
import cz.uhk.pro2kf2026.model.User;
import cz.uhk.pro2kf2026.repository.CommodityRepository;
import cz.uhk.pro2kf2026.repository.OrderRepository;
import cz.uhk.pro2kf2026.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final UserRepository userRepository;
    private final CommodityRepository commodityRepository;

    public OrderServiceImpl(OrderRepository orderRepository,
                            UserRepository userRepository,
                            CommodityRepository commodityRepository) {
        this.orderRepository = orderRepository;
        this.userRepository = userRepository;
        this.commodityRepository = commodityRepository;
    }

    @Override
    public Order placeOrder(OrderRequest request) {
        String side = request.side() == null ? "" : request.side().toLowerCase();
        if (!side.equals("buy") && !side.equals("sell")) {
            throw new IllegalArgumentException("Side must be 'buy' or 'sell'");
        }
        if (request.quantity() <= 0) {
            throw new IllegalArgumentException("Quantity must be positive");
        }
        if (request.price() <= 0) {
            throw new IllegalArgumentException("Price must be positive");
        }

        User user = userRepository.findById(request.userId())
                .orElseThrow(() -> new IllegalArgumentException("Unknown user"));
        if ("ADMIN".equalsIgnoreCase(user.getRole())) {
            throw new IllegalArgumentException("Admins cannot trade");
        }

        Commodity commodity = commodityRepository.findById(request.commodityId());
        if (commodity == null) {
            throw new IllegalArgumentException("Unknown commodity");
        }

        Order order = new Order();
        order.setUser(user);
        order.setCommodity(commodity);
        order.setSide(side);
        order.setQuantity(request.quantity());
        order.setPrice(request.price());
        return orderRepository.save(order);
    }

    @Override
    public List<Order> getOrdersBySymbol(String symbol) {
        return orderRepository.findByCommodity_Symbol(symbol);
    }
}
