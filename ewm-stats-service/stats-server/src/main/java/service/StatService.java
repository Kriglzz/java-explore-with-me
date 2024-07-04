package service;

import ViewStatsDto.ViewStatsDto;
import EndpointHitDto.EndpointHitDto;

import java.util.List;

public interface StatService {
    void addHit(EndpointHitDto endpointHitDto);

    List<ViewStatsDto> getAllStats(ViewStatsDto viewStatsDto);
}
