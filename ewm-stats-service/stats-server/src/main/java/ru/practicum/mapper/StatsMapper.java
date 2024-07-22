package ru.practicum.mapper;

import ru.practicum.endpointhitdto.EndpointHitDto;
import ru.practicum.model.EndpointHit;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface StatsMapper {
    EndpointHitDto endpointHitToEndpointHitDto(EndpointHit endpointHit);

    EndpointHit endpointHitDtoToEndpointHit(EndpointHitDto endpointHitDto);
}
