package cz.uhk.pro2kf2026.controller.rest;

import cz.uhk.pro2kf2026.model.Commodity;
import cz.uhk.pro2kf2026.repository.CommodityRepository;
import cz.uhk.pro2kf2026.service.CommodityService;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping("/api/commodities")
public class CommodityRestController {

    private final CommodityService commodityService;

    public CommodityRestController(CommodityService commodityService) {
        this.commodityService = commodityService;
    }

    @GetMapping
    public List<Commodity> getCommodities() {
        return commodityService.getAllCommodities();
    }

    @GetMapping("/{symbol}")
    public Commodity getCommodityBySymbol(@PathVariable String symbol) {
        return commodityService.getCommodityBySymbol(symbol);
    }

    @PostMapping
    public Commodity createCommodity(Authentication auth, @RequestBody Commodity commodity) {
        try {
            return commodityService.addCommodity(auth.getName(), commodity.getName(), commodity.getSymbol(), commodity.getUnit());
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }
}
