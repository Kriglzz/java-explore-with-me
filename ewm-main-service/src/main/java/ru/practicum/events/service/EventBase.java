package ru.practicum.events.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.StatsClient;
import ru.practicum.events.dto.CountDto;
import ru.practicum.events.model.Event;
import ru.practicum.requests.repository.RequestRepository;
import ru.practicum.viewstatsdto.ViewStatsDto;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static ru.practicum.requests.model.Status.CONFIRMED;

@RequiredArgsConstructor
@Service
class EventBase {
    public static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    private final RequestRepository requestRepository;
    private final StatsClient statsClient;
    private final ObjectMapper objectMapper;

    protected Map<Long, Long> getViewsForEvents(List<Event> events) {
        /*if (events.isEmpty()) {
            return Collections.emptyMap();
        }

        // Карта для хранения uri и соответствующих id событий
        Map<String, Long> eventUrisAndIds = events.stream()
                .collect(Collectors.toMap(
                        event -> String.format("/events/%s", event.getId()),
                        Event::getId
                ));

        // Минимальная дата создания событий
        LocalDateTime startDate = events.stream()
                .map(Event::getCreatedOn)
                .min(LocalDateTime::compareTo)
                .orElse(null);

        if (startDate == null) {
            return Collections.emptyMap();
        }

        // Статистика
        ResponseEntity<Object> response = statsClient.getStats(startDate, LocalDateTime.now(),
                new ArrayList<>(eventUrisAndIds.keySet()), true);
        List<ViewStatsDto> stats = objectMapper.convertValue(response.getBody(), new TypeReference<>() {
        });

        // Карта для хранения статистики просмотров

        return stats.stream()
                .filter(stat -> eventUrisAndIds.containsKey(stat.getUri()))
                .collect(Collectors.toMap(
                        stat -> eventUrisAndIds.get(stat.getUri()),
                        ViewStatsDto::getHits
                ));*/
        List<String> uris = events.stream()
                .map(event -> String.format("/events/%s", event.getId()))
                .collect(Collectors.toList());
        List<LocalDateTime> startDates = events.stream()
                .map(Event::getCreatedOn)
                .collect(Collectors.toList());
        LocalDateTime startDate = startDates.stream()
                .min(LocalDateTime::compareTo)
                .orElse(null);
        Map<Long, Long> statsMap = new HashMap<>();
        // Получаем статистику
        if (startDate != null) {
            /*ResponseEntity<Object> response = statsClient.getStats(startDate, LocalDateTime.now(), uris, true);
            List<ViewStatsDto> stats = objectMapper.convertValue(response.getBody(), new TypeReference<>() {});*/
            statsMap = statsClient.getStats(startDate, LocalDateTime.now(), uris, true).stream().collect(Collectors.toMap(
                    statsDto -> parseEventIdFromUrl(statsDto.getUri()),
                    ViewStatsDto::getHits
            ));
        }
        return statsMap;
    }

    private Long parseEventIdFromUrl(String url) {
        String[] parts = url.split("/events/");
        if (parts.length == 2) {
            return Long.parseLong(parts[1]);
        }
        return -1L;
    }

    protected Map<Long, Long> getConfirmedRequests(List<Event> events) {
        /*if (events.isEmpty()) {
            return Collections.emptyMap();
        }

        List<Long> ids = events.stream().map(Event::getId).collect(Collectors.toList());
        List<Object[]> results = requestRepository.findByStatus(ids, Status.CONFIRMED);

        return results.stream().collect(Collectors.toMap(
                result -> (Long) result[0],  // eventId
                result -> (Long) result[1]   // count
        ));*/
        if (events.isEmpty()) return Collections.emptyMap();
        List<Long> ids = events.stream().map(Event::getId).collect(Collectors.toList());
        List<CountDto> results = requestRepository.findByStatus(ids, CONFIRMED);
        return results.stream().collect(Collectors.toMap(CountDto::getEventId, CountDto::getCount));
    }
}
