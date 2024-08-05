package ru.practicum.controller;

import ru.practicum.endpointhitdto.EndpointHitDto;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.service.StatService;
import ru.practicum.viewstatsdto.ViewStatsDto;

import javax.validation.Valid;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequiredArgsConstructor
public class StatsController {
    private final StatService statService;

    @PostMapping("/hit")
    public ResponseEntity<EndpointHitDto> addHit(@RequestBody @Valid EndpointHitDto endpointHitDto) {
        statService.addHit(endpointHitDto);
        return new ResponseEntity<>(endpointHitDto, HttpStatus.OK);
    }

    @GetMapping("/stats")
    public ResponseEntity<List<ViewStatsDto>> getStats(@RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
                                                       LocalDateTime start,
                                                       @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime end,
                                                       @RequestParam(defaultValue = "") List<String> uris,
                                                       @RequestParam(defaultValue = "false") boolean unique) {
        List<ViewStatsDto> statsList = statService.getAllStats(start, end, uris, unique);
        return new ResponseEntity<>(statsList, HttpStatus.OK);
    }
}
