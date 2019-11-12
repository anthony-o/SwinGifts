package com.github.anthonyo.swingifts.repository;
import com.github.anthonyo.swingifts.domain.GiftIdea;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

import java.util.List;


/**
 * Spring Data  repository for the GiftIdea entity.
 */
@Repository
public interface GiftIdeaRepository extends JpaRepository<GiftIdea, Long> {

    List<GiftIdea> findByRecipientId(Long participationId);
}
