package com.github.anthonyo.swingifts.repository;

import com.github.anthonyo.swingifts.domain.DrawingExclusionGroup;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Spring Data  repository for the DrawingExclusionGroup entity.
 */
@Repository
public interface DrawingExclusionGroupRepository extends JpaRepository<DrawingExclusionGroup, Long> {

    @Query(value = "select distinct drawingExclusionGroup from DrawingExclusionGroup drawingExclusionGroup left join fetch drawingExclusionGroup.participations",
        countQuery = "select count(distinct drawingExclusionGroup) from DrawingExclusionGroup drawingExclusionGroup")
    Page<DrawingExclusionGroup> findAllWithEagerRelationships(Pageable pageable);

    @Query("select distinct drawingExclusionGroup from DrawingExclusionGroup drawingExclusionGroup left join fetch drawingExclusionGroup.participations")
    List<DrawingExclusionGroup> findAllWithEagerRelationships();

    @Query("select drawingExclusionGroup from DrawingExclusionGroup drawingExclusionGroup left join fetch drawingExclusionGroup.participations where drawingExclusionGroup.id =:id")
    Optional<DrawingExclusionGroup> findOneWithEagerRelationships(@Param("id") Long id);

}
