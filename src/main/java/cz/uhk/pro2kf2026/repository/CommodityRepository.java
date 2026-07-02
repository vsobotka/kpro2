package cz.uhk.pro2kf2026.repository;

import cz.uhk.pro2kf2026.model.Commodity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CommodityRepository extends JpaRepository<Commodity, Long> {
    Commodity findById(long id);
    Commodity findBySymbol(String symbol);
}
