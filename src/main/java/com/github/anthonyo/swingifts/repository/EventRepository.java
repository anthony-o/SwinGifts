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
@Repository
public interface EventRepository extends JpaRepository<Event, Long> {

    @Query("select event from Event event where event.admin.login = ?#{principal.username}")
    List<Event> findByAdminIsCurrentUser();

    @Query("select distinct event from Event event left join event.participations participation left join participation.user participationUser" +
        " where event.admin.login = :login or participationUser.login = :login")
    List<Event> findByParticipationsUserLoginIsOrAdminLoginIs(@Param("login") String login);

    @Query("select count(event) > 0 from Event event left join event.participations participation left join participation.user participationUser" +
        " where event.id = :id and (event.admin.login = :login or participationUser.login = :login)")
    boolean existsByIdAndParticipationsUserLoginOrAdminLogin(@Param("id") Long id, @Param("login") String login);

    Optional<Event> findOneByPublicKeyAndPublicKeyEnabledIsTrue(String publicKey);
}
