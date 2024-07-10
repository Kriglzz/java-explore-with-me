package mapper;

import endpointhitdto.EndpointHitDto;
import model.EndpointHit;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface StatsMapper {
    EndpointHitDto endpointHitToEndpointHitDto(EndpointHit endpointHit);

    EndpointHit endpointHitDtoToEndpointHit(EndpointHitDto endpointHitDto);
}
