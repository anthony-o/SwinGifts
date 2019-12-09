package com.github.anthonyo.swingifts.repository;

import com.github.anthonyo.swingifts.domain.Participation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
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

    List<Participation> findByEventId(Long eventId);

    @Query("select count(participation) > 0 from Participation participation join participation.event event left join event.participations eventParticipation" +
        " where participation.id = :id and (event.admin.login = :login or eventParticipation.user.login = :login)")
    boolean existsByIdAndEventParticipationsUserLoginOrEventAdminLogin(@Param("id") Long id, @Param("login") String login);

    @Query("select count(participation) > 0 from Participation participation left join participation.user user where participation.id = :id and (user.login = :login or participation.event.admin.login = :login)")
    boolean existsByIdAndUserLoginOrEventAdminLogin(@Param("id") Long id, @Param("login") String login);

    Optional<Participation> findByEventIdAndUserLogin(Long eventId, String userLogin);
}
