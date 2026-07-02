package cz.uhk.pro2kf2026.service;

import cz.uhk.pro2kf2026.model.Commodity;
import cz.uhk.pro2kf2026.model.User;
import cz.uhk.pro2kf2026.repository.CommodityRepository;
import cz.uhk.pro2kf2026.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CommodityServiceImpl implements CommodityService {

    private final CommodityRepository commodityRepository;
    private final UserRepository userRepository;
    private final HoldingService holdingService;

    public CommodityServiceImpl(CommodityRepository commodityRepository, UserRepository userRepository, HoldingService holdingService) {
        this.commodityRepository = commodityRepository;
        this.userRepository = userRepository;
        this.holdingService = holdingService;
    }

    @Override
    @Transactional
    public Commodity addCommodity(String username, String symbol, String name, String unit) {
        User user = userRepository.findByUsername(username);
        if (user == null) {
            throw new IllegalArgumentException("Unknown user");
        }

        if (!"ADMIN".equalsIgnoreCase(user.getRole())) {
            throw new IllegalArgumentException("You cannot perform this action");
        }

        Commodity commodity = new Commodity();
        commodity.setSymbol(symbol);
        commodity.setName(name);
        commodity.setUnit(unit);
        commodityRepository.save(commodity);

        // Give each user initial holdings so they can start trading
        for (User u : userRepository.findAll()) {
            holdingService.upsertHolding(u.getUsername(), symbol, 100);
        }

        return commodity;
    }

    @Override
    public List<Commodity> getAllCommodities() {
        return commodityRepository.findAll();
    }

    @Override
    public Commodity getCommodityBySymbol(String symbol) {
        return commodityRepository.findBySymbol(symbol);
    }
}
