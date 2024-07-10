package repository;

import viewstatsdto.ViewStatsDto;
import model.EndpointHit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface StatsRepository extends JpaRepository <EndpointHit, Long>{
    @Query("SELECT new ViewStatsDto(h.app, h.uri, count(h.ip)) " +
            "FROM EndpointHit AS h " +
            "WHERE h.timestamp " +
            "BETWEEN :start AND :end AND (:uris IS NULL OR h.uri IN :uris) " +
            "GROUP BY h.app, h.uri " +
            "ORDER BY COUNT(h.ip) DESC")
    List<ViewStatsDto> findAll(@Param("start") LocalDateTime start,
                               @Param("end") LocalDateTime end,
                               @Param("uris") List<String> uris);

    @Query("SELECT new ViewStatsDto(h.app, h.uri, count(DISTINCT h.ip)) " +
            "FROM EndpointHit AS h " +
            "WHERE h.timestamp " +
            "BETWEEN :start AND :end AND (:uris IS NULL OR h.uri IN :uris) " +
            "GROUP BY h.app, h.uri " +
            "ORDER BY COUNT(DISTINCT h.ip) DESC")
    List<ViewStatsDto> findAllUnique(@Param("start") LocalDateTime start,
                                     @Param("end") LocalDateTime end,
                                     @Param("uris") List<String> uris);
}
