package ru.practicum.events.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.StatsClient;
import ru.practicum.endpointhitdto.EndpointHitDto;
import ru.practicum.events.dto.EventDto;
import ru.practicum.events.dto.EventShortDto;
import ru.practicum.events.model.EventSort;
import ru.practicum.events.model.UserEventParams;
import ru.practicum.events.service.PublicEventService;
import ru.practicum.exception.ValidationException;

import jakarta.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping(path = "/events")
@RequiredArgsConstructor
public class PublicEventController {

    private final PublicEventService publicEventService;

    private final StatsClient statsClient;

    @GetMapping("/{eventId}")
    public ResponseEntity<EventDto> getEventById(@PathVariable Long eventId, HttpServletRequest request) {
        EventDto event = publicEventService.getEventById(eventId, request);
        statsClient.addHit(EndpointHitDto.builder()
                .app("explore-with-me")
                .uri(request.getRequestURI())
                .ip(request.getRemoteAddr())
                .timestamp(LocalDateTime.now())
                .build());
        return new ResponseEntity<>(event, HttpStatus.OK);
    }

    @GetMapping
    public ResponseEntity<List<EventShortDto>> getAllPublicEvents(
            @RequestParam(defaultValue = "") String text,
            @RequestParam(defaultValue = "") List<Long> categories,
            @RequestParam(required = false) Boolean paid,
            @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime rangeStart,
            @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime rangeEnd,
            @RequestParam(value = "onlyAvailable", defaultValue = "false", required = false) Boolean onlyAvailable,
            @RequestParam(required = false) EventSort sort,
            @RequestParam(value = "from", defaultValue = "0", required = false) Integer from,
            @RequestParam(value = "size", defaultValue = "10", required = false) Integer size,
            HttpServletRequest request) {
        if (rangeStart != null && rangeEnd != null && rangeStart.isAfter(rangeEnd)) {
            throw new ValidationException("Ошибка даты");
        }
        int page = from / size;
        PageRequest pageRequest = PageRequest.of(page, size);
        List<EventShortDto> events = publicEventService.getAllPublicEvents(
                new UserEventParams(text, categories, paid, rangeStart, rangeEnd, onlyAvailable, sort), pageRequest);
        statsClient.addHit(EndpointHitDto.builder()
                .app("explore-with-me")
                .uri(request.getRequestURI())
                .ip(request.getRemoteAddr())
                .timestamp(LocalDateTime.now())
                .build());
        return new ResponseEntity<>(events, HttpStatus.OK);
    }
}
