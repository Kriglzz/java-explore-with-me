package service;

import EndpointHitDto.EndpointHitDto;
import ViewStatsDto.ViewStatsDto;
import lombok.RequiredArgsConstructor;
import mapper.StatsMapper;
import model.EndpointHit;
import org.springframework.stereotype.Service;
import repository.StatsRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class StatServiceImpl implements StatService{
    private final StatsMapper statsMapper;
    private final StatsRepository statsRepository;
    @Override
    public void addHit(EndpointHitDto endpointHitDto) {
        EndpointHit endpointHit = statsMapper.endpointHitDtoToEndpointHit(endpointHitDto);
        statsRepository.save(endpointHit);
    }

    @Override
    public List<ViewStatsDto> getAllStats(ViewStatsDto viewStatsDto) {
        return null;
    }


}
