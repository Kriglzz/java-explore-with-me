package ru.practicum.service;

import ru.practicum.endpointhitdto.EndpointHitDto;
import lombok.RequiredArgsConstructor;
import ru.practicum.mapper.StatsMapper;
import ru.practicum.model.EndpointHit;
import org.springframework.stereotype.Service;
import ru.practicum.repository.StatsRepository;
import ru.practicum.viewstatsdto.ViewStatsDto;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

@Service
@RequiredArgsConstructor
public class StatServiceImpl implements StatService {

    private final StatsMapper statsMapper;
    private final StatsRepository statsRepository;

    @Override
    public void addHit(EndpointHitDto endpointHitDto) {
        EndpointHit endpointHit = statsMapper.endpointHitDtoToEndpointHit(endpointHitDto);
        statsRepository.save(endpointHit);
    }

    @Override
    public List<ViewStatsDto> getAllStats(LocalDateTime start, LocalDateTime end, List<String> uris, boolean unique) {
        if (uris == null) {
            uris = Collections.emptyList();
        }
        List<ViewStatsDto> stats;
        if (unique) {
            stats = statsRepository.findAllUnique(start, end, uris);
        } else {
            stats = statsRepository.findAll(start, end, uris);
        }
        return stats;
    }
}
