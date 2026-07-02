package cz.uhk.pro2kf2026.service;

import cz.uhk.pro2kf2026.model.Holding;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface HoldingService {
    Holding upsertHolding(String username, String symbol, int quantity);
    List<Holding> getAllHoldings(String username) throws IllegalArgumentException;
    Holding getHoldingBySymbol(String username, String symbol) throws IllegalArgumentException;
}
