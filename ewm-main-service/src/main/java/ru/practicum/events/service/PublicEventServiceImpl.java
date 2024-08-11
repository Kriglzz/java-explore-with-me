package ru.practicum.events.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.StatsClient;
import ru.practicum.events.dto.EventDto;
import ru.practicum.events.dto.EventShortDto;
import ru.practicum.events.mapper.EventMapper;
import ru.practicum.events.model.Event;
import ru.practicum.events.model.UserEventParams;
import ru.practicum.events.repository.EventRepository;
import ru.practicum.exception.model.NotFoundException;
import ru.practicum.requests.repository.RequestRepository;

import javax.servlet.http.HttpServletRequest;
import java.util.*;
import java.util.stream.Collectors;

import static ru.practicum.events.model.State.PUBLISHED;

@Slf4j
@Service
public class PublicEventServiceImpl extends EventBase implements PublicEventService {

    private final EventRepository eventRepository;
    private final EventMapper eventMapper;

    public PublicEventServiceImpl(EventRepository eventRepository,
                                  EventMapper eventMapper,
                                  RequestRepository requestRepository,
                                  StatsClient statsClient,
                                  ObjectMapper objectMapper) {
        super(requestRepository, statsClient, objectMapper);
        this.eventRepository = eventRepository;
        this.eventMapper = eventMapper;
    }

    @Override
    public EventDto getEventById(Long eventId, HttpServletRequest request) {
        Event event = eventRepository.findById(eventId).orElseThrow(()
                -> new NotFoundException("Событие с id \"" + eventId + "\" не найдено"));
        if (!event.getState().equals(PUBLISHED)) {
            throw new NotFoundException("Событие с id " + eventId + " не опубликовано");
        }

        List<Event> events = List.of(event);
        return eventMapper.eventToEventDto(event,
                getConfirmedRequests(events).getOrDefault(eventId, 0L),
                getViewsForEvents(events).getOrDefault(eventId, 0L));
    }

    /*@Override
    public List<EventShortDto> getAllPublicEvents(UserEventParams userEventParams, PageRequest pageRequest) {
        Specification<Event> specification = buildSpecification(userEventParams);
        List<Event> events = eventRepository.findAll(specification, pageRequest).toList();

        Map<Long, Long> confirmedRequests = getConfirmedRequests(events);

        // Фильтрация по доступным событиям
        if (userEventParams.getOnlyAvailable()) {
            events = filterAvailableEvents(events, confirmedRequests);
        }

        Map<Long, Long> viewStats = getViewsForEvents(events);

        // Сортировка событий
        events = sortEvents(events, viewStats, userEventParams.getSort());

        return eventMapper.eventListToEventShortDtoList(events, viewStats, confirmedRequests);
    }

    private Specification<Event> buildSpecification(UserEventParams userEventParams) {
        List<Specification<Event>> specifications = new ArrayList<>();
        specifications.add(isPublished());

        addTextSearchSpecification(userEventParams.getText(), specifications);
        addCategorySpecification(userEventParams.getCategories(), specifications);
        addPaidSpecification(userEventParams.getPaid(), specifications);
        addDateRangeSpecifications(userEventParams.getRangeStart(), userEventParams.getRangeEnd(), specifications);

        return specifications.stream().reduce(Specification::and).orElse(null);
    }

    private Specification<Event> isPublished() {
        return (root, query, criteriaBuilder) -> criteriaBuilder.in(root.get("state")).value(List.of(PUBLISHED));
    }

    private void addTextSearchSpecification(String text, List<Specification<Event>> specifications) {
        if (!text.isBlank()) {
            String searchText = "%" + text.toLowerCase() + "%";
            specifications.add((root, query, criteriaBuilder) -> criteriaBuilder.or(
                    criteriaBuilder.like(criteriaBuilder.lower(root.get("annotation")), searchText),
                    criteriaBuilder.like(criteriaBuilder.lower(root.get("description")), searchText)));
        }
    }

    private void addCategorySpecification(List<Long> categories, List<Specification<Event>> specifications) {
        if (!categories.isEmpty()) {
            specifications.add((root, query, criteriaBuilder) -> criteriaBuilder.in(root.get("category").get("id"))
                    .value(categories));
        }
    }

    private void addPaidSpecification(Boolean paid, List<Specification<Event>> specifications) {
        if (paid != null) {
            specifications.add((root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get("paid"), paid));
        }
    }

    private void addDateRangeSpecifications(LocalDateTime rangeStart, LocalDateTime rangeEnd, List<Specification<Event>> specifications) {
        if (rangeStart != null) {
            specifications.add((root, query, criteriaBuilder) -> criteriaBuilder.greaterThanOrEqualTo(root.get("eventDate"), rangeStart));
        }
        if (rangeEnd != null) {
            specifications.add((root, query, criteriaBuilder) -> criteriaBuilder.lessThanOrEqualTo(root.get("eventDate"), rangeEnd));
        }
    }

    private List<Event> filterAvailableEvents(List<Event> events, Map<Long, Long> confirmedRequests) {
        return events.stream()
                .filter(event -> event.getParticipantLimit() > confirmedRequests.getOrDefault(event.getId(), 0L))
                .collect(Collectors.toList());
    }

    private List<Event> sortEvents(List<Event> events, Map<Long, Long> viewStats, String sort) {
        if (sort == null) return events;

        switch (sort) {
            case "VIEWS":
                return events.stream()
                        .sorted(Comparator.comparing(event -> viewStats.getOrDefault(event.getId(), 0L)))
                        .collect(Collectors.toList());
            case "EVENT_DATE":
                return events.stream()
                        .sorted(Comparator.comparing(Event::getEventDate))
                        .collect(Collectors.toList());
            default:
                return events;
        }
    }*/

    @Override
    @Transactional(readOnly = true)
    public List<EventShortDto> getAllPublicEvents(UserEventParams userEventParams, PageRequest pageRequest) {
        List<Specification<Event>> specifications = new ArrayList<>();
        specifications.add((root, query, criteriaBuilder) -> criteriaBuilder.in(root.get("state"))
                .value(List.of(PUBLISHED)));
        if (!userEventParams.getText().isBlank()) {
            String searchText = "%" + userEventParams.getText().toLowerCase() + "%";
            specifications.add((root, query, criteriaBuilder) -> criteriaBuilder.or(
                    criteriaBuilder.like(criteriaBuilder.lower(root.get("annotation")), searchText),
                    criteriaBuilder.like(criteriaBuilder.lower(root.get("description")), searchText)));
        }
        if (!userEventParams.getCategories().isEmpty()) {
            specifications.add((root, query, criteriaBuilder) -> criteriaBuilder.in(root.get("category").get("id"))
                    .value(userEventParams.getCategories()));
        }
        if (userEventParams.getPaid() != null) {
            specifications.add((root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get("paid"), userEventParams.getPaid()));
        }
        if (userEventParams.getRangeStart() != null) {
            specifications.add((root, query, criteriaBuilder) -> criteriaBuilder
                    .greaterThanOrEqualTo(root.get("eventDate"), userEventParams.getRangeStart()));
        }
        if (userEventParams.getRangeEnd() != null) {
            specifications.add((root, query, criteriaBuilder) -> criteriaBuilder
                    .lessThanOrEqualTo(root.get("eventDate"), userEventParams.getRangeEnd()));
        }
        specifications = specifications.stream().filter(Objects::nonNull).collect(Collectors.toList());
        Specification<Event> s = specifications
                .stream()
                .reduce(Specification::and).orElse(null);
        List<Event> events = eventRepository.findAll(s, pageRequest).toList();

        Map<Long, Long> confirmedRequests = getConfirmedRequests(events);
        if (userEventParams.getOnlyAvailable()) {
            events = events
                    .stream()
                    .filter(event -> event.getParticipantLimit() > confirmedRequests.getOrDefault(event.getId(), 0L))
                    .collect(Collectors.toList());
        }
        Map<Long, Long> viewStats = getViewsForEvents(events);
        if (userEventParams.getSort() == null)
            return eventMapper.eventListToEventShortDtoList(events, viewStats, confirmedRequests);
        switch (userEventParams.getSort()) {
            case VIEWS:
                events = events
                        .stream()
                        .sorted(Comparator.comparing(event -> viewStats.getOrDefault(event.getId(), 0L)))
                        .collect(Collectors.toList());
            case EVENT_DATE:
                events = events
                        .stream()
                        .sorted(Comparator.comparing(Event::getEventDate))
                        .collect(Collectors.toList());
            default:
                List<EventShortDto> result = eventMapper.eventListToEventShortDtoList(events, viewStats, confirmedRequests);
                return result;
        }
        /*List<Specification<Event>> specifications = new ArrayList<>();
        specifications.add((root, query, criteriaBuilder) -> {
            CriteriaBuilder.In<State> inClause = criteriaBuilder.in(root.get("state"));
            inClause.value(State.PUBLISHED);
            return inClause;
        });
        *//*specifications.add((root, query, criteriaBuilder) -> criteriaBuilder.in(root.get("state"))
                .value(List.of(PUBLISHED)));*//*
        specifications.forEach(spec -> {
            // Здесь вы можете использовать определенные методы для логирования или отладки
            log.info("Specification: {}", spec.toString());
        });

        if (!userEventParams.getText().isBlank()) {
            String searchText = "%" + userEventParams.getText().toLowerCase() + "%";
            specifications.add((root, query, criteriaBuilder) -> criteriaBuilder.or(
                    criteriaBuilder.like(criteriaBuilder.lower(root.get("annotation")), searchText),
                    criteriaBuilder.like(criteriaBuilder.lower(root.get("description")), searchText)));
        }
        if (!userEventParams.getCategories().isEmpty()) {
            specifications.add((root, query, criteriaBuilder) -> criteriaBuilder.in(root.get("category").get("id"))
                    .value(userEventParams.getCategories()));
        }
        if (userEventParams.getPaid() != null) {
            specifications.add((root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get("paid"), userEventParams.getPaid()));
        }
        if (userEventParams.getRangeStart() != null) {
            specifications.add((root, query, criteriaBuilder) -> criteriaBuilder
                    .greaterThanOrEqualTo(root.get("eventDate"), userEventParams.getRangeStart()));
        }
        if (userEventParams.getRangeEnd() != null) {
            specifications.add((root, query, criteriaBuilder) -> criteriaBuilder
                    .lessThanOrEqualTo(root.get("eventDate"), userEventParams.getRangeEnd()));
        }
        specifications = specifications.stream().filter(Objects::nonNull).collect(Collectors.toList());
        Specification<Event> s = specifications
                .stream()
                .reduce(Specification::and).orElse(null);
        if (s != null) {
            log.info("Specification: {}", s.toString());
        } else {
            log.info("Specification is null");
        }
        List<Event> events = eventRepository.findAll(s, pageRequest).toList();
        log.info("List contents: {}", events.stream()
                .map(Event::toString)
                .collect(Collectors.joining(", ")));

        Map<Long, Long> confirmedRequests = getConfirmedRequests(events);
        if (userEventParams.getOnlyAvailable()) {
            events = events
                    .stream()
                    .filter(event -> event.getParticipantLimit() > confirmedRequests.getOrDefault(event.getId(), 0L))
                    .collect(Collectors.toList());
        }
        Map<Long, Long> viewStats = getViewsForEvents(events);
        if (userEventParams.getSort() == null)
            return eventMapper.eventListToEventShortDtoList(events, viewStats, confirmedRequests);
        switch (userEventParams.getSort()) {
            case VIEWS:
                events = events
                        .stream()
                        .sorted(Comparator.comparing(event -> viewStats.getOrDefault(event.getId(), 0L)))
                        .collect(Collectors.toList());
            case EVENT_DATE:
                events = events
                        .stream()
                        .sorted(Comparator.comparing(Event::getEventDate))
                        .collect(Collectors.toList());
            default:
                return eventMapper.eventListToEventShortDtoList(events, viewStats, confirmedRequests);
        }*/
        /*if (userEventParams.getRangeStart() != null && userEventParams.getRangeEnd() != null
                && userEventParams.getRangeStart().isAfter(userEventParams.getRangeEnd())) {
            throw new ValidationException("Некорректная дата");
        }

        List<Event> events = eventRepository.getEventsByUserParameters(userEventParams.getText(),
                userEventParams.getCategories(), userEventParams.getPaid(), userEventParams.getRangeStart(),
                userEventParams.getRangeEnd(), pageRequest).getContent();

        Map<Long, Long> confirmedRequests = getConfirmedRequests(events);
        if (userEventParams.getOnlyAvailable()) {
            events = events
                    .stream()
                    .filter(event -> event.getParticipantLimit() > confirmedRequests.getOrDefault(event.getId(), 0L))
                    .collect(Collectors.toList());
        }
        Map<Long, Long> viewStats = getViewsForEvents(events);
        if (userEventParams.getSort() == null)
            return eventMapper.eventListToEventShortDtoList(events, viewStats, confirmedRequests);
        switch (userEventParams.getSort()) {
            case VIEWS:
                events = events
                        .stream()
                        .sorted(Comparator.comparing(event -> viewStats.getOrDefault(event.getId(), 0L)))
                        .collect(Collectors.toList());
            case EVENT_DATE:
                events = events
                        .stream()
                        .sorted(Comparator.comparing(Event::getEventDate))
                        .collect(Collectors.toList());
            default:
                return eventMapper.eventListToEventShortDtoList(events, viewStats, confirmedRequests);

        }*/
    }

    /*private Map<Long, Long> getViewsForEvents(List<Event> events) {
        if (events.isEmpty()) {
            return Collections.emptyMap();
        }

        // Создаем карту для хранения uri и соответствующих id событий
        Map<String, Long> eventUrisAndIds = events.stream()
                .collect(Collectors.toMap(
                        event -> String.format("/events/%s", event.getId()),
                        Event::getId
                ));

        // Получаем минимальную дату создания событий
        LocalDateTime startDate = events.stream()
                .map(Event::getCreatedOn)
                .min(LocalDateTime::compareTo)
                .orElse(null);

        if (startDate == null) {
            return Collections.emptyMap();
        }

        // Получаем статистику
        ResponseEntity<Object> response = statsClient.getStats(startDate, LocalDateTime.now(),
                new ArrayList<>(eventUrisAndIds.keySet()), true);
        List<ViewStatsDto> stats = objectMapper.convertValue(response.getBody(), new TypeReference<>() {
        });

        // Создаем карту для хранения статистики просмотров

        return stats.stream()
                .filter(stat -> eventUrisAndIds.containsKey(stat.getUri()))
                .collect(Collectors.toMap(
                        stat -> eventUrisAndIds.get(stat.getUri()),
                        ViewStatsDto::getHits
                ));
    }

    private Map<Long, Long> getConfirmedRequests(List<Event> events) {
        if (events.isEmpty()) {
            return Collections.emptyMap();
        }

        List<Long> ids = events.stream().map(Event::getId).collect(Collectors.toList());
        List<Object[]> results = requestRepository.findByStatus(ids, Status.CONFIRMED);

        return results.stream().collect(Collectors.toMap(
                result -> (Long) result[0],  // eventId
                result -> (Long) result[1]   // count
        ));
    }*/
}
