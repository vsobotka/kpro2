package cz.uhk.pro2kf2026.service;

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
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final UserRepository userRepository;
    private final CommodityRepository commodityRepository;
    private final HoldingRepository holdingRepository;
    private final TransactionRepository transactionRepository;

    public OrderServiceImpl(OrderRepository orderRepository,
                            UserRepository userRepository,
                            CommodityRepository commodityRepository,
                            HoldingRepository holdingRepository,
                            TransactionRepository transactionRepository) {
        this.orderRepository = orderRepository;
        this.userRepository = userRepository;
        this.commodityRepository = commodityRepository;
        this.holdingRepository = holdingRepository;
        this.transactionRepository = transactionRepository;
    }

    @Override
    @Transactional
    public Order placeOrder(String username, OrderRequest request) {
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

        User user = userRepository.findByUsername(username);
        if (user == null) {
            throw new IllegalArgumentException("Unknown user");
        }
        if ("ADMIN".equalsIgnoreCase(user.getRole())) {
            throw new IllegalArgumentException("Admins cannot trade");
        }

        Commodity commodity = commodityRepository.findById(request.commodityId());
        if (commodity == null) {
            throw new IllegalArgumentException("Unknown commodity");
        }

        if (side.equals("buy")) {
            int maxCost = (int) Math.round(request.quantity() * request.price());
            if (user.getBalance() < maxCost) {
                throw new IllegalArgumentException("Insufficient balance");
            }
        } else {
            if (ownedQuantity(user, commodity) < request.quantity()) {
                throw new IllegalArgumentException("Insufficient holdings");
            }
        }

        Order order = new Order();
        order.setUser(user);
        order.setCommodity(commodity);
        order.setSide(side);
        order.setQuantity(request.quantity());
        order.setPrice(request.price());

        matchOrder(order);

        if (order.getQuantity() > 0) {
            return orderRepository.save(order);
        }
        return order;
    }

    @Override
    public List<Order> getOrdersBySymbol(String symbol) {
        return orderRepository.findByCommodity_Symbol(symbol);
    }

    private void matchOrder(Order incoming) {
        String symbol = incoming.getCommodity().getSymbol();
        boolean incomingIsBuy = incoming.getSide().equals("buy");

        List<Order> book = incomingIsBuy
                ? orderRepository.findByCommodity_SymbolAndSideOrderByPriceAsc(symbol, "sell")
                : orderRepository.findByCommodity_SymbolAndSideOrderByPriceDesc(symbol, "buy");

        for (Order resting : book) {
            if (incoming.getQuantity() <= 0) {
                break;
            }

            if (resting.getUser().getId() == incoming.getUser().getId()) {
                continue;
            }

            boolean crosses = incomingIsBuy
                    ? incoming.getPrice() >= resting.getPrice()
                    : incoming.getPrice() <= resting.getPrice();
            if (!crosses) {
                break;
            }

            double fillQty = Math.min(incoming.getQuantity(), resting.getQuantity());
            double fillPrice = resting.getPrice();

            Order buyOrder = incomingIsBuy ? incoming : resting;
            Order sellOrder = incomingIsBuy ? resting : incoming;
            settle(buyOrder.getUser(), sellOrder.getUser(), incoming.getCommodity(), fillQty, fillPrice);

            incoming.setQuantity(incoming.getQuantity() - fillQty);
            resting.setQuantity(resting.getQuantity() - fillQty);
            if (resting.getQuantity() <= 0) {
                orderRepository.delete(resting);
            } else {
                orderRepository.save(resting);
            }
        }
    }

    private void settle(User buyer, User seller, Commodity commodity, double qty, double price) {
        int amount = (int) Math.round(qty * price);

        buyer.setBalance(buyer.getBalance() - amount);
        seller.setBalance(seller.getBalance() + amount);
        userRepository.save(buyer);
        userRepository.save(seller);

        adjustHolding(buyer, commodity, (int) qty);
        adjustHolding(seller, commodity, -(int) qty);

        recordTransaction(buyer, commodity, "buy", qty, price, -amount);
        recordTransaction(seller, commodity, "sell", qty, price, amount);
    }

    private void adjustHolding(User user, Commodity commodity, int delta) {
        Holding holding = holdingRepository.findByUser_UsernameAndCommodity_Symbol(
                user.getUsername(), commodity.getSymbol());
        if (holding == null) {
            holding = new Holding();
            holding.setUser(user);
            holding.setCommodity(commodity);
            holding.setQuantity(0);
        }
        holding.setQuantity(holding.getQuantity() + delta);
        holdingRepository.save(holding);
    }

    private void recordTransaction(User user, Commodity commodity, String type,
                                   double qty, double price, int change) {
        Transaction tx = new Transaction();
        tx.setUser(user);
        tx.setCommodity(commodity);
        tx.setType(type);
        tx.setQuantity(qty);
        tx.setPrice(price);
        tx.setChange(change);
        transactionRepository.save(tx);
    }

    private int ownedQuantity(User user, Commodity commodity) {
        Holding holding = holdingRepository.findByUser_UsernameAndCommodity_Symbol(
                user.getUsername(), commodity.getSymbol());
        return holding == null || holding.getQuantity() == null ? 0 : holding.getQuantity();
    }
}
