package mapper;

import model.EndpointHit;
import EndpointHitDto.EndpointHitDto;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface StatsMapper {
    EndpointHitDto endpointHitToEndpointHitDto(EndpointHit endpointHit);

    EndpointHit endpointHitDtoToEndpointHit(EndpointHitDto endpointHitDto);
}
