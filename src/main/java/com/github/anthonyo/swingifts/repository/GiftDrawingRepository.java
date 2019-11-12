package com.github.anthonyo.swingifts.repository;
import com.github.anthonyo.swingifts.domain.GiftDrawing;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;


/**
 * Spring Data  repository for the GiftDrawing entity.
 */
@SuppressWarnings("unused")
@Repository
public interface GiftDrawingRepository extends JpaRepository<GiftDrawing, Long> {

}
