package cz.uhk.pro2kf2026.service;

import cz.uhk.pro2kf2026.model.Commodity;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface CommodityService {
    Commodity addCommodity(String username, String symbol, String name, String unit);
    List<Commodity> getAllCommodities();
    Commodity getCommodityBySymbol(String symbol);
}
