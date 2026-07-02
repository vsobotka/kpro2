package cz.uhk.pro2kf2026.service;

import cz.uhk.pro2kf2026.model.Commodity;
import cz.uhk.pro2kf2026.model.Holding;
import cz.uhk.pro2kf2026.model.User;
import cz.uhk.pro2kf2026.repository.CommodityRepository;
import cz.uhk.pro2kf2026.repository.HoldingRepository;
import cz.uhk.pro2kf2026.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class HoldingServiceImpl implements HoldingService{

    private final HoldingRepository holdingRepository;
    private final UserRepository userRepository;
    private final CommodityRepository commodityRepository;

    public HoldingServiceImpl(HoldingRepository holdingRepository, UserRepository userRepository, CommodityRepository commodityRepository) {
        this.holdingRepository = holdingRepository;
        this.userRepository = userRepository;
        this.commodityRepository = commodityRepository;
    }

    @Override
    public Holding upsertHolding(String username, String symbol, int quantity) {
        User user = userRepository.findByUsername(username);
        if (user == null) {
            throw new IllegalArgumentException("Unknown user");
        }

        Commodity commodity = commodityRepository.findBySymbol(symbol);
        if (commodity == null) {
            throw new IllegalArgumentException("Unknown commodity");
        }

        if (quantity < 0) {
            throw new IllegalArgumentException("Quantity must be positive");
        }

        Holding holding = holdingRepository.findByUser_UsernameAndCommodity_Symbol(username, symbol);
        if (holding == null) {
            holding = new Holding();
            holding.setUser(user);
            holding.setCommodity(commodity);
        }
        holding.setQuantity(quantity);
        return holdingRepository.save(holding);
    }

    @Override
    public List<Holding> getAllHoldings(String username) throws IllegalArgumentException {
        User user = userRepository.findByUsername(username);
        if (user == null) {
            throw new IllegalArgumentException("Unknown user");
        }

        return holdingRepository.findByUser_Username(username);
    }

    @Override
    public Holding getHoldingBySymbol(String username, String symbol) {
        User user = userRepository.findByUsername(username);
        if (user == null) {
            throw new IllegalArgumentException("Unknown user");
        }

        Commodity commodity = commodityRepository.findBySymbol(symbol);
        if (commodity == null) {
            throw new IllegalArgumentException("Unknown commodity");
        }

        return holdingRepository.findByUser_UsernameAndCommodity_Symbol(username, symbol);
    }
}
