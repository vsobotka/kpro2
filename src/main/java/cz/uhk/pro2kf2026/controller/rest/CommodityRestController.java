package cz.uhk.pro2kf2026.controller.rest;

import cz.uhk.pro2kf2026.model.Commodity;
import cz.uhk.pro2kf2026.repository.CommodityRepository;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/commodities")
public class CommodityRestController {

    private final CommodityRepository commodityRepository;

    public CommodityRestController(CommodityRepository commodityRepository) {
        this.commodityRepository = commodityRepository;
    }

    @GetMapping
    public List<Commodity> getCommodities() {
        return commodityRepository.findAll();
    }
}
