package service;

import viewstatsdto.ViewStatsDto;
import endpointhitdto.EndpointHitDto;

import java.time.LocalDateTime;
import java.util.List;

public interface StatService {
    void addHit(EndpointHitDto endpointHitDto);

    List<ViewStatsDto> getAllStats(LocalDateTime start, LocalDateTime end, List<String> uris, boolean unique);
}
