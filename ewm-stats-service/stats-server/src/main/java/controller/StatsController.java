package controller;

import lombok.RequiredArgsConstructor;
import EndpointHitDto.EndpointHitDto;
import model.EndpointHit;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import service.StatService;

@RestController
@RequiredArgsConstructor
@Validated
public class StatsController {
    StatService statService;

    @PostMapping
    public void addHit(@RequestBody @Validated EndpointHitDto endpointHitDto){

    }
}
