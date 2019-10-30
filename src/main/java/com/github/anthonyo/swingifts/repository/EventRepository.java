package com.github.anthonyo.swingifts.repository;
import com.github.anthonyo.swingifts.domain.Event;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Spring Data  repository for the Event entity.
 */
@SuppressWarnings("unused")
@Repository
public interface EventRepository extends JpaRepository<Event, Long> {

    @Query("select event from Event event where event.admin.login = ?#{principal.username}")
    List<Event> findByAdminIsCurrentUser();

}
