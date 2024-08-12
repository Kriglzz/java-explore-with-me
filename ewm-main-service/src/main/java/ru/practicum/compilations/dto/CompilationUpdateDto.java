package ru.practicum.compilations.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.Size;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CompilationUpdateDto {

    private List<Long> events = List.of();

    private Boolean pinned;

    @Size(max = 50)
    private String title;
}
