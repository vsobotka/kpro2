package cz.uhk.pro2kf2026.repository;

import cz.uhk.pro2kf2026.model.Holding;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface HoldingRepository extends JpaRepository<Holding, Long> {
    Holding findByUser_UsernameAndCommodity_Symbol(String username, String symbol);
    List<Holding> findByUser_Username(String username);
}
