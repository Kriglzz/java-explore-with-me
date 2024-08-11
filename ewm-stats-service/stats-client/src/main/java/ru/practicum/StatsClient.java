package ru.practicum;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.client.RestTemplate;
import ru.practicum.endpointhitdto.EndpointHitDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.util.DefaultUriBuilderFactory;
import ru.practicum.viewstatsdto.ViewStatsDto;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class StatsClient extends BaseClient {


    private static final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");


    @Autowired
    public StatsClient(@Value("${statistic-server.url}") String serverUrl, RestTemplateBuilder builder) {
        super(builder.uriTemplateHandler(new DefaultUriBuilderFactory(serverUrl))
                .requestFactory(HttpComponentsClientHttpRequestFactory::new)
                .build());
    }

    public void addHit(EndpointHitDto endpointHitDto) {
        post("/hit", endpointHitDto);
    }

    public List<ViewStatsDto> getStats(LocalDateTime start, LocalDateTime end, List<String> uris,
                                                       Boolean unique) {
        Map<String, Object> parameters = Map.of(
                "start", start.format(dateTimeFormatter),
                "end", end.format(dateTimeFormatter),
                "uris", String.join(",", uris),
                "unique", unique
        );

        ResponseEntity<Object> response = rest.getForEntity("/stats?start={start}&end={end}&uris={uris}&unique={unique}", Object.class, parameters);

        List<Map<String, Object>> data = (List<Map<String, Object>>) response.getBody();

        List<ViewStatsDto> viewStatsDtos = data.stream()
                .map(item -> {
                    ViewStatsDto viewStatsDto = new ViewStatsDto();
                    viewStatsDto.setHits(Long.parseLong(item.get("hits").toString()));
                    viewStatsDto.setApp(item.get("app").toString());
                    viewStatsDto.setUri(item.get("uri").toString());
                    return viewStatsDto;
                })
                .collect(Collectors.toList());

        return viewStatsDtos;
    }
}
