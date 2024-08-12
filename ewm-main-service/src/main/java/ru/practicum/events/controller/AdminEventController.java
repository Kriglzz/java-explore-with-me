package ru.practicum.events.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.events.dto.EventDto;
import ru.practicum.events.dto.EventUpdateAdmin;
import ru.practicum.events.model.AdminEventParams;
import ru.practicum.events.model.State;
import ru.practicum.events.service.AdminEventService;
import ru.practicum.exception.ValidationException;

import jakarta.validation.Valid;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/admin/events")
@RequiredArgsConstructor
public class AdminEventController {

    private final AdminEventService adminEventService;

    @GetMapping
    public ResponseEntity<List<EventDto>> getAllAdminEvents(@RequestParam(defaultValue = "") List<Long> users,
                                                            @RequestParam(defaultValue = "") List<String> states,
                                                            @RequestParam(defaultValue = "") List<Long> categories,
                                                            @RequestParam(required = false)
                                                            @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime rangeStart,
                                                            @RequestParam(required = false)
                                                            @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime rangeEnd,
                                                            @RequestParam(value = "from", defaultValue = "0", required = false) Integer from,
                                                            @RequestParam(value = "size", defaultValue = "10", required = false) Integer size) {
        int page = from / size;
        PageRequest pageRequest = PageRequest.of(page, size, Sort.unsorted());

        List<State> statesList = states.stream()
                .map(State::fromString)
                .collect(Collectors.toList());

        List<EventDto> fullDtoList = adminEventService.getAllAdminEvents(
                new AdminEventParams(users, statesList, categories, rangeStart,
                        rangeEnd), pageRequest);
        return new ResponseEntity<>(fullDtoList, HttpStatus.OK);
    }

    @PatchMapping("/{eventId}")
    public ResponseEntity<EventDto> adminUpdate(@PathVariable Long eventId,
                                                @RequestBody @Valid EventUpdateAdmin eventUpdateAdmin) {
        if (eventUpdateAdmin.getEventDate() != null
                && eventUpdateAdmin.getEventDate().isBefore(LocalDateTime.now().plusHours(1))) {
            throw new ValidationException("Ошибка даты.");
        }
        EventDto updatedEvent = adminEventService.updateEvent(eventId, eventUpdateAdmin);
        return new ResponseEntity<>(updatedEvent, HttpStatus.OK);
    }
}
