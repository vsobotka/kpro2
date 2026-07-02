package cz.uhk.pro2kf2026.dto;

public record OrderRequest(long commodityId, long userId, String side, double quantity, double price) { }
