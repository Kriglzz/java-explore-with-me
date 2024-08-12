package ru.practicum;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.DefaultUriBuilderFactory;
import org.springframework.web.util.UriComponentsBuilder;
import ru.practicum.endpointhitdto.EndpointHitDto;
import ru.practicum.viewstatsdto.ViewStatsDto;

import java.util.List;

@Service
public class StatsClient extends BaseClient {

    @Autowired
    public StatsClient(@Value("${statistic-server.url}") String serverUrl, RestTemplateBuilder builder) {
        super(createRestTemplate(serverUrl, builder));
        System.out.println("StatsClient initialized with serverUrl: " + serverUrl);
    }

    private static RestTemplate createRestTemplate(String serverUrl, RestTemplateBuilder builder) {
        HttpComponentsClientHttpRequestFactory requestFactory = new HttpComponentsClientHttpRequestFactory();

        RestTemplate restTemplate = builder
                .uriTemplateHandler(new DefaultUriBuilderFactory(serverUrl))
                .requestFactory(() -> requestFactory)
                .build();
        System.out.println("RestTemplate created with serverUrl: " + serverUrl);
        return restTemplate;
    }

    public void addHit(EndpointHitDto endpointHitDto) {
        post("/hit", endpointHitDto);
    }

    public List<ViewStatsDto> getStats(String start, String end, List<String> uris, boolean unique) {
        UriComponentsBuilder uriComponentsBuilder = UriComponentsBuilder
                .fromPath("/stats")
                .queryParam("start", start)
                .queryParam("end", end)
                .queryParam("uris", uris)
                .queryParam("unique", unique);
        try {
            ResponseEntity<List<ViewStatsDto>> response = rest.exchange(
                    uriComponentsBuilder.build().toString(), HttpMethod.GET, null, new ParameterizedTypeReference<>() {
                    }
            );
            if (response.getStatusCode().is2xxSuccessful()) {
                return response.getBody();
            } else {
                return List.of();
            }
        } catch (Exception e) {
            return List.of();
        }
    }
}
