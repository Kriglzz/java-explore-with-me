package ru.practicum.events.service;

import org.springframework.data.domain.PageRequest;
import ru.practicum.events.dto.EventDto;
import ru.practicum.events.dto.EventUpdateAdmin;
import ru.practicum.events.model.EventParams;

import java.util.List;

public interface AdminEventService {

    List<EventDto> getAllAdminEvents(EventParams eventParams, PageRequest pageRequest);

    EventDto updateEvent(Long eventId, EventUpdateAdmin eventUpdateAdmin);

}
