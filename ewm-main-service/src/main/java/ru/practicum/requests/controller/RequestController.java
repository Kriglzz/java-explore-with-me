package ru.practicum.requests.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.requests.dto.ParticipationRequestDto;
import ru.practicum.requests.service.RequestService;

import jakarta.validation.constraints.Positive;
import java.util.List;

@RestController
@RequestMapping("/users/{userId}/requests")
@RequiredArgsConstructor
public class RequestController {

    private final RequestService requestService;

    @PostMapping
    public ResponseEntity<ParticipationRequestDto> addRequest(@PathVariable @Positive Long userId,
                                                              @RequestParam @Positive Long eventId) {
        ParticipationRequestDto participationRequestDto = requestService.addRequest(userId, eventId);
        return new ResponseEntity<>(participationRequestDto, HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<ParticipationRequestDto>> getAllRequests(@PathVariable @Positive Long userId) {
        List<ParticipationRequestDto> requests = requestService.getAllRequests(userId);
        return new ResponseEntity<>(requests, HttpStatus.OK);
    }

    @PatchMapping("/{requestId}/cancel")
    public ResponseEntity<ParticipationRequestDto> cancelRequest(@PathVariable @Positive Long userId,
                                                                 @PathVariable @Positive Long requestId) {
        ParticipationRequestDto participationRequestDto = requestService.cancelRequest(userId, requestId);
        return new ResponseEntity<>(participationRequestDto, HttpStatus.OK);
    }
}
