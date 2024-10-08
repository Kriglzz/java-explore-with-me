package ru.practicum.compilations.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.compilations.dto.CompilationDto;
import ru.practicum.compilations.service.PublicCompilationService;

import java.util.List;

@RestController
@RequestMapping("/compilations")
@RequiredArgsConstructor
@Validated
public class PublicCompilationController {

    private final PublicCompilationService publicCompilationService;

    @GetMapping
    public ResponseEntity<List<CompilationDto>> getAll(@RequestParam(required = false) Boolean pinned,
                                                       @RequestParam(defaultValue = "0") int from,
                                                       @RequestParam(defaultValue = "10") int size) {
        int page = from / size;
        PageRequest pageRequest = PageRequest.of(page, size);
        List<CompilationDto> compilations = publicCompilationService.getAllCompilations(pinned, pageRequest);
        return new ResponseEntity<>(compilations, HttpStatus.OK);
    }

    @GetMapping("/{compilationId}")
    public ResponseEntity<CompilationDto> getById(@PathVariable Long compilationId) {
        CompilationDto compilationDto = publicCompilationService.getCompilationById(compilationId);
        return new ResponseEntity<>(compilationDto, HttpStatus.OK);
    }

}
