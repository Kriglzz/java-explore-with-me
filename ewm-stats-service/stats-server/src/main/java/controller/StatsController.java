package controller;

import ViewStatsDto.ViewStatsDto;
import lombok.RequiredArgsConstructor;
import EndpointHitDto.EndpointHitDto;
import model.EndpointHit;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import repository.StatsRepository;
import service.StatService;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequiredArgsConstructor
@Validated
public class StatsController {
    StatService statService;

    @PostMapping("/hit")
    public void addHit(@RequestBody @Validated EndpointHitDto endpointHitDto){
        statService.addHit(endpointHitDto);
    }

    @GetMapping("/stats")
    public ResponseEntity<List<ViewStatsDto>> getStats(@RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
                                                                                     LocalDateTime start,
                                       @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime end,
                                       @RequestParam(defaultValue = "") List<String> uris,
                                       @RequestParam(defaultValue = "false") boolean unique){
        List<ViewStatsDto> statsList = statService.getAllStats(start, end, uris, unique);
        return new ResponseEntity<>(statsList, HttpStatus.OK);
    }
}
