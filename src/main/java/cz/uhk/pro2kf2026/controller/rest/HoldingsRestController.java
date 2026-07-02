package cz.uhk.pro2kf2026.controller.rest;

import cz.uhk.pro2kf2026.model.Holding;
import cz.uhk.pro2kf2026.service.HoldingService;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/api/holdings")
public class HoldingsRestController {
    private final HoldingService holdingService;

    public HoldingsRestController(HoldingService holdingService) {
        this.holdingService = holdingService;
    }

    @GetMapping("/{symbol}")
    public Holding getHoldingsBySymbol(Authentication auth, @PathVariable String symbol) {
        try {
            return holdingService.getHoldingBySymbol(auth.getName(), symbol);
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }
}
