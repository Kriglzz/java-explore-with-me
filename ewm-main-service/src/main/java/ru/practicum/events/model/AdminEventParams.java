package ru.practicum.events.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AdminEventParams {

    private List<Long> userIds;

    private List<State> states;

    private List<Long> categoriesIds;

    private LocalDateTime start;

    private LocalDateTime end;

}
