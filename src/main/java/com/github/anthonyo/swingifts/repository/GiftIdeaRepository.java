package com.github.anthonyo.swingifts.repository;

import com.github.anthonyo.swingifts.domain.GiftIdea;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;


/**
 * Spring Data  repository for the GiftIdea entity.
 */
@SuppressWarnings("unused")
@Repository
public interface GiftIdeaRepository extends JpaRepository<GiftIdea, Long> {

}
