package com.github.anthonyo.swingifts.repository;
import com.github.anthonyo.swingifts.domain.GiftIdeaReservation;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;


/**
 * Spring Data  repository for the GiftIdeaReservation entity.
 */
@SuppressWarnings("unused")
@Repository
public interface GiftIdeaReservationRepository extends JpaRepository<GiftIdeaReservation, Long> {

}
