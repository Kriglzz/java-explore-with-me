package ru.practicum.events.service;

import org.springframework.data.domain.PageRequest;
import ru.practicum.events.dto.EventDto;
import ru.practicum.events.dto.EventShortDto;
import ru.practicum.events.model.UserEventParams;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

public interface PublicEventService {
    EventDto getEventById(Long eventId, HttpServletRequest request);

    List<EventShortDto> getAllPublicEvents(UserEventParams userEventParams, PageRequest pageRequest);

}
