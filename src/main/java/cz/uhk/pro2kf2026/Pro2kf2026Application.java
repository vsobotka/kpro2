package cz.uhk.pro2kf2026;

import cz.uhk.pro2kf2026.model.Commodity;
import cz.uhk.pro2kf2026.model.Order;
import cz.uhk.pro2kf2026.model.User;
import cz.uhk.pro2kf2026.repository.CommodityRepository;
import cz.uhk.pro2kf2026.repository.OrderRepository;
import cz.uhk.pro2kf2026.repository.UserRepository;
import cz.uhk.pro2kf2026.service.HoldingService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;

@SpringBootApplication
public class Pro2kf2026Application {

    public static void main(String[] args) {
        SpringApplication.run(Pro2kf2026Application.class, args);
    }

    @Bean
    PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    CommandLineRunner seedAdmin(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        return args -> {
            if (userRepository.findByUsername("admin") == null) {
                User user = new User();
                user.setUsername("admin");
                user.setName("Admin");
                user.setPassword(passwordEncoder.encode("heslo"));
                user.setRole("ADMIN");
                user.setBalance(0);
                userRepository.save(user);
            }
        };
    }

    @Bean
    CommandLineRunner seedUsers(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        return args -> {
            if (userRepository.findByUsername("alice") == null) {
                User user = new User();
                user.setUsername("alice");
                user.setName("Alice");
                user.setPassword(passwordEncoder.encode("alicepw"));
                user.setRole("USER");
                user.setBalance(100000);
                userRepository.save(user);
            }

            if (userRepository.findByUsername("bob") == null) {
                User user = new User();
                user.setUsername("bob");
                user.setName("Bob");
                user.setPassword(passwordEncoder.encode("bobpw"));
                user.setRole("USER");
                user.setBalance(50000);
                userRepository.save(user);
            }
        };
    }

    @Bean
    CommandLineRunner seedCommodities(CommodityRepository commodityRepository) {
        return args -> {
            upsertCommodity(commodityRepository, "GOLD", "Gold", "oz");
            upsertCommodity(commodityRepository, "OIL", "Brent Crude", "barrel");
            upsertCommodity(commodityRepository, "WHEAT", "Milling Wheat", "tonne");
        };
    }

    @Bean
    CommandLineRunner seedHoldings(CommodityRepository commodityRepository, UserRepository userRepository, HoldingService holdingService) {
        return args -> {
            List<Commodity> commodities = commodityRepository.findAll();
            List<User> users = userRepository.findAll();

            for (Commodity commodity : commodities) {
                for (User user: users) {
                    holdingService.upsertHolding(user.getUsername(), commodity.getSymbol(), 50);
                }
            }
        };
    }

    @Bean
    CommandLineRunner seedBobOrders(UserRepository userRepository,
                                    CommodityRepository commodityRepository,
                                    OrderRepository orderRepository) {
        return args -> {
            User bob = userRepository.findByUsername("bob");
            if (bob == null || !orderRepository.findByUser_Username("bob").isEmpty()) {
                return;
            }

            Commodity gold = commodityRepository.findBySymbol("GOLD");   // base ~2000 / oz
            if (gold != null) {
                createOrder(orderRepository, bob, gold, "sell", 2010, 10);
                createOrder(orderRepository, bob, gold, "sell", 2035, 8);
                createOrder(orderRepository, bob, gold, "sell", 2070, 5);
                createOrder(orderRepository, bob, gold, "buy", 1985, 5);
                createOrder(orderRepository, bob, gold, "buy", 1960, 4);
                createOrder(orderRepository, bob, gold, "buy", 1930, 3);
            }

            Commodity oil = commodityRepository.findBySymbol("OIL");     // base ~80 / barrel
            if (oil != null) {
                createOrder(orderRepository, bob, oil, "sell", 82, 15);
                createOrder(orderRepository, bob, oil, "sell", 86, 10);
                createOrder(orderRepository, bob, oil, "sell", 90, 8);
                createOrder(orderRepository, bob, oil, "buy", 78, 15);
                createOrder(orderRepository, bob, oil, "buy", 74, 10);
                createOrder(orderRepository, bob, oil, "buy", 70, 8);
            }

            Commodity wheat = commodityRepository.findBySymbol("WHEAT"); // base ~250 / tonne
            if (wheat != null) {
                createOrder(orderRepository, bob, wheat, "sell", 256, 12);
                createOrder(orderRepository, bob, wheat, "sell", 264, 8);
                createOrder(orderRepository, bob, wheat, "sell", 275, 5);
                createOrder(orderRepository, bob, wheat, "buy", 244, 12);
                createOrder(orderRepository, bob, wheat, "buy", 236, 8);
                createOrder(orderRepository, bob, wheat, "buy", 225, 5);
            }
        };
    }

    private void createOrder(OrderRepository repo, User user, Commodity commodity,
                             String side, double price, double quantity) {
        Order order = new Order();
        order.setUser(user);
        order.setCommodity(commodity);
        order.setSide(side);
        order.setPrice(price);
        order.setQuantity(quantity);
        repo.save(order);
    }

    private void upsertCommodity(CommodityRepository repo, String symbol, String name, String unit) {
        Commodity commodity = repo.findBySymbol(symbol);
        if (commodity == null) {
            commodity = new Commodity();
            commodity.setSymbol(symbol);
        }
        commodity.setName(name);
        commodity.setUnit(unit);
        repo.save(commodity);
    }
}
