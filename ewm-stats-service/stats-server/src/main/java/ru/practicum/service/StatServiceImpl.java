package ru.practicum.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.endpointhitdto.EndpointHitDto;
import ru.practicum.mapper.StatsMapper;
import ru.practicum.model.EndpointHit;
import ru.practicum.repository.StatsRepository;
import ru.practicum.viewstatsdto.ViewStatsDto;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

@Slf4j
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
    public List<ViewStatsDto> getAllStats(LocalDateTime start, LocalDateTime end, List<String> uris, Boolean unique) {
        log.info("uris в начале: {}", uris);

        if (uris == null || uris.isEmpty()) {
            uris = Collections.emptyList();
        }
        log.info("uris после проверки: {}", uris);

        List<ViewStatsDto> stats;
        if (unique) {
            stats = statsRepository.findAllUnique(start, end, uris);
        } else {
            stats = uris.isEmpty() ? statsRepository.findAll(start, end) : statsRepository.findAllUri(start, end, uris);
        }
        log.info("stats: {}", stats);

        return stats;
    }
}
