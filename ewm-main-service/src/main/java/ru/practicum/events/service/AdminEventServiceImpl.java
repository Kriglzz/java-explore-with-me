package ru.practicum.events.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.StatsClient;
import ru.practicum.category.model.Category;
import ru.practicum.category.repository.CategoryRepository;
import ru.practicum.events.dto.EventDto;
import ru.practicum.events.dto.EventUpdateAdmin;
import ru.practicum.events.mapper.EventMapper;
import ru.practicum.events.model.Event;
import ru.practicum.events.model.EventParams;
import ru.practicum.events.model.State;
import ru.practicum.events.repository.EventRepository;
import ru.practicum.exception.model.NotFoundException;
import ru.practicum.exception.model.ValidationException;
import ru.practicum.exception.model.ViolationException;
import ru.practicum.location.repository.LocationRepository;
import ru.practicum.requests.repository.RequestRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class AdminEventServiceImpl extends EventBase implements AdminEventService {
    private final EventRepository eventRepository;

    private final CategoryRepository categoryRepository;

    private final LocationRepository locationRepository;

    private final EventMapper eventMapper;

    public AdminEventServiceImpl(EventRepository eventRepository,
                                 CategoryRepository categoryRepository,
                                 LocationRepository locationRepository,
                                 EventMapper eventMapper,
                                 RequestRepository requestRepository,
                                 StatsClient statsClient,
                                 ObjectMapper objectMapper) {
        super(requestRepository, statsClient, objectMapper);
        this.eventRepository = eventRepository;
        this.categoryRepository = categoryRepository;
        this.locationRepository = locationRepository;
        this.eventMapper = eventMapper;
    }

    /*@Override
    public List<EventDto> getAllAdminEvents(EventParams eventParams, PageRequest pageRequest) {
        Specification<Event> specification = createSpecification(eventParams);

        List<Event> events = eventRepository.findAll(specification, pageRequest).toList();
        Map<Long, Long> confirmedRequests = getConfirmedRequests(events);
        Map<Long, Long> viewStats = getViewsForEvents(events);

        return mapEventsToEventDtos(events, confirmedRequests, viewStats);
    }*/

    @Override
    @Transactional
    public EventDto updateEvent(Long eventId, EventUpdateAdmin eventUpdateAdmin) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new NotFoundException("Event " + eventId + " not found"));

        validateEventState(event);
        validateEventDate(event);

        updateEventFields(event, eventUpdateAdmin);
        updateEventState(event, eventUpdateAdmin.getStateAction());

        Event updatedEvent = eventRepository.save(event);
        return eventMapper.eventToEventDto(updatedEvent, 0L, 0L);
    }

    private void validateEventState(Event event) {
        if (!event.getState().equals("PENDING")) {
            throw new ViolationException("Event data cannot be changed");
        }
    }

    private void validateEventDate(Event event) {
        if (LocalDateTime.now().isAfter(event.getEventDate())) {
            throw new ValidationException("The start must be no earlier than the publication date");
        }
    }

    private void updateEventFields(Event event, EventUpdateAdmin eventUpdateAdmin) {
        Optional.ofNullable(eventUpdateAdmin.getAnnotation()).ifPresent(event::setAnnotation);
        Optional.ofNullable(eventUpdateAdmin.getDescription()).ifPresent(event::setDescription);
        Optional.ofNullable(eventUpdateAdmin.getParticipantLimit()).ifPresent(event::setParticipantLimit);

        if (eventUpdateAdmin.getLocation() != null) {
            event.setLocation(locationRepository.save(eventUpdateAdmin.getLocation()));
        }

        if (eventUpdateAdmin.getCategory() != null) {
            Category category = categoryRepository.findById(eventUpdateAdmin.getCategory())
                    .orElseThrow(() -> new NotFoundException("Category " + eventUpdateAdmin.getCategory() + " not found"));
            event.setCategory(category);
        }

        Optional.ofNullable(eventUpdateAdmin.getEventDate()).ifPresent(event::setEventDate);
        Optional.ofNullable(eventUpdateAdmin.getPaid()).ifPresent(event::setPaid);
        Optional.ofNullable(eventUpdateAdmin.getRequestModeration()).ifPresent(event::setRequestModeration);
        Optional.ofNullable(eventUpdateAdmin.getTitle()).ifPresent(event::setTitle);
    }

    private void updateEventState(Event event, String stateAction) {
        if (stateAction != null) {
            switch (stateAction) {
                case "REJECT_EVENT":
                    event.setState(State.CANCELED);
                    break;
                case "PUBLISH_EVENT":
                    event.setPublishedOn(LocalDateTime.now());
                    event.setState(State.PUBLISHED);
                    break;
                default:
                    throw new ValidationException("There is no such state " + stateAction);
            }
        }
    }

    private Specification<Event> createSpecification(EventParams eventParams) {
        List<Specification<Event>> specifications = new ArrayList<>();

        if (!eventParams.getStates().isEmpty()) {
            specifications.add(eventStateSpecification(eventParams.getStates()));
        }
        if (!eventParams.getUserIds().isEmpty()) {
            specifications.add(eventInitiatorSpecification(eventParams.getUserIds()));
        }
        if (!eventParams.getCategoriesIds().isEmpty()) {
            specifications.add(eventCategorySpecification(eventParams.getCategoriesIds()));
        }
        if (eventParams.getStart() != null) {
            specifications.add(eventStartDateSpecification(eventParams.getStart()));
        }
        if (eventParams.getEnd() != null) {
            specifications.add(eventEndDateSpecification(eventParams.getEnd()));
        }

        return specifications.stream().reduce(Specification::and).orElse(null);
    }

    private Specification<Event> eventStateSpecification(List<State> states) {
        return (root, query, criteriaBuilder) -> criteriaBuilder.in(root.get("state")).value(states);
    }

    private Specification<Event> eventInitiatorSpecification(List<Long> userIds) {
        return (root, query, criteriaBuilder) -> criteriaBuilder.in(root.get("initiator").get("id")).value(userIds);
    }

    private Specification<Event> eventCategorySpecification(List<Long> categoryIds) {
        return (root, query, criteriaBuilder) -> criteriaBuilder.in(root.get("category").get("id")).value(categoryIds);
    }

    private Specification<Event> eventStartDateSpecification(LocalDateTime start) {
        return (root, query, criteriaBuilder) -> criteriaBuilder.greaterThanOrEqualTo(root.get("eventDate"), start);
    }

    private Specification<Event> eventEndDateSpecification(LocalDateTime end) {
        return (root, query, criteriaBuilder) -> criteriaBuilder.lessThanOrEqualTo(root.get("eventDate"), end);
    }

    private List<EventDto> mapEventsToEventDtos(List<Event> events, Map<Long, Long> confirmedRequests, Map<Long, Long> viewStats) {
        return events.stream()
                .map(event -> eventMapper.eventToEventDto(
                        event,
                        confirmedRequests.getOrDefault(event.getId(), 0L),
                        viewStats.getOrDefault(event.getId(), 0L)))
                .collect(Collectors.toList());
    }


    @Override
    @Transactional(readOnly = true)
    public List<EventDto> getAllAdminEvents(EventParams eventParams, PageRequest pageRequest) {
        List<Specification<Event>> specifications = new ArrayList<>();
        if (!eventParams.getStates().isEmpty()) {
            specifications.add((root, query, criteriaBuilder) -> criteriaBuilder.in(root.get("state"))
                    .value(eventParams.getStates()));
        }
        if (!eventParams.getUserIds().isEmpty()) {
            specifications.add((root, query, criteriaBuilder) -> criteriaBuilder.in(root.get("initiator").get("id"))
                    .value(eventParams.getUserIds()));
        }
        if (!eventParams.getCategoriesIds().isEmpty()) {
            specifications.add((root, query, criteriaBuilder) -> criteriaBuilder.in(root.get("category").get("id"))
                    .value(eventParams.getCategoriesIds()));
        }
        if (eventParams.getStart() != null) {
            specifications.add((root, query, criteriaBuilder) -> criteriaBuilder
                    .greaterThanOrEqualTo(root.get("eventDate"), eventParams.getStart()));
        }
        if (eventParams.getEnd() != null) {
            specifications.add((root, query, criteriaBuilder) -> criteriaBuilder
                    .lessThanOrEqualTo(root.get("eventDate"), eventParams.getEnd()));
        }

        List<Event> events = eventRepository.findAll(specifications
                .stream()
                .reduce(Specification::and)
                .orElse(null), pageRequest).toList();
        Map<Long, Long> confirmedRequests = getConfirmedRequests(events);
        Map<Long, Long> viewStats = getViewsForEvents(events);

        return events.stream()
                .map(event -> eventMapper.eventToEventDto(
                        event,
                        confirmedRequests.getOrDefault(event.getId(), 0L),
                        viewStats.getOrDefault(event.getId(), 0L)))
                .collect(Collectors.toList());
    }
}
