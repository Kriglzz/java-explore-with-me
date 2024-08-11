package ru.practicum.events.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.events.dto.EventDto;
import ru.practicum.events.dto.EventShortDto;
import ru.practicum.events.dto.EventUpdateUser;
import ru.practicum.events.dto.NewEventDto;
import ru.practicum.events.service.PrivateEventService;
import ru.practicum.exception.model.ValidationException;
import ru.practicum.requests.dto.EventRequestStatusUpdateRequest;
import ru.practicum.requests.dto.EventRequestStatusUpdateResult;
import ru.practicum.requests.dto.ParticipationRequestDto;
import ru.practicum.requests.service.RequestService;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/users/{userId}/events")
@RequiredArgsConstructor
@Validated
public class PrivateEventController {

    private final PrivateEventService privateEventService;

    private final RequestService requestService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<EventDto> addEvent(@PathVariable Long userId,
                                             @Valid @RequestBody NewEventDto dto) {
        if (dto.getEventDate() != null
                && dto.getEventDate().isBefore(LocalDateTime.now().plusHours(1))) {
            throw new ValidationException("Ошибка даты");
        }
        EventDto result = privateEventService.addEvent(userId, dto);
        return new ResponseEntity<>(result, HttpStatus.CREATED);
    }

    @PatchMapping("/{eventId}")
    public ResponseEntity<EventDto> update(@PathVariable Long userId,
                                           @PathVariable Long eventId,
                                           @RequestBody @Valid EventUpdateUser eventUpdateUser
    ) {
        if (eventUpdateUser.getEventDate() != null
                && eventUpdateUser.getEventDate().isBefore(LocalDateTime.now().plusHours(1))) {
            throw new ValidationException("Ошибка даты");
        }
        EventDto result = privateEventService.updateEvent(userId, eventId, eventUpdateUser);
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @GetMapping
    public ResponseEntity<List<EventShortDto>> getEventsByUser(
            @PathVariable @Positive Long userId,
            @RequestParam(value = "from", defaultValue = "0", required = false) @PositiveOrZero int from,
            @RequestParam(value = "size", defaultValue = "10", required = false) @Positive int size) {
        int page = from / size;
        PageRequest pageRequest = PageRequest.of(page, size);
        List<EventShortDto> list = privateEventService.getEventsByUser(userId, pageRequest);
        return new ResponseEntity<>(list, HttpStatus.OK);
    }

    @GetMapping("/{eventId}")
    public ResponseEntity<EventDto> getEventByUserAndEvent(@PathVariable Long userId, @PathVariable Long eventId) {
        EventDto result = privateEventService.getEventByUserAndEvent(userId, eventId);
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @GetMapping("/{eventId}/requests")
    public ResponseEntity<List<ParticipationRequestDto>> getEventRequests(@PathVariable Long userId, @PathVariable Long eventId) {
        List<ParticipationRequestDto> requests = requestService.getEventRequests(userId, eventId);
        return new ResponseEntity<>(requests, HttpStatus.OK);
    }

    @PatchMapping("/{eventId}/requests")
    public ResponseEntity<EventRequestStatusUpdateResult> updateRequestStatus(
            @PathVariable Long userId,
            @PathVariable Long eventId,
            @RequestBody @Valid EventRequestStatusUpdateRequest eventRequestStatusUpdateRequest) {
        EventRequestStatusUpdateResult result = requestService.updateRequestStatus(userId, eventId, eventRequestStatusUpdateRequest);
        return new ResponseEntity<>(result, HttpStatus.OK);
    }


}
