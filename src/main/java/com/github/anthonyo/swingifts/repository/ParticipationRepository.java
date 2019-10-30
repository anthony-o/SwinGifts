package com.github.anthonyo.swingifts.repository;
import com.github.anthonyo.swingifts.domain.Participation;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Spring Data  repository for the Participation entity.
 */
@Repository
public interface ParticipationRepository extends JpaRepository<Participation, Long> {

    @Query("select participation from Participation participation where participation.user.login = ?#{principal.username}")
    List<Participation> findByUserIsCurrentUser();

    @Query(value = "select distinct participation from Participation participation left join fetch participation.recipients",
        countQuery = "select count(distinct participation) from Participation participation")
    Page<Participation> findAllWithEagerRelationships(Pageable pageable);

    @Query("select distinct participation from Participation participation left join fetch participation.recipients")
    List<Participation> findAllWithEagerRelationships();

    @Query("select participation from Participation participation left join fetch participation.recipients where participation.id =:id")
    Optional<Participation> findOneWithEagerRelationships(@Param("id") Long id);

}
