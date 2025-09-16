package co.com.asulado.r2dbc.adapter;

import co.com.asulado.model.liquidation.Participant;
import co.com.asulado.model.liquidation.gateways.ParticipantRepository;
import co.com.asulado.r2dbc.mapper.BatchParticipantDetailEntityMapper;
import co.com.asulado.r2dbc.repository.BatchParticipantDetailDataRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;

@Repository
@RequiredArgsConstructor
public class ParticipantRepositoryAdapter implements ParticipantRepository {

    private final BatchParticipantDetailDataRepository repository;
    private final BatchParticipantDetailEntityMapper mapper;

    @Override
    public Flux<Participant> findAllByBatchId(Long batchId) {
        return repository.findAllByLiquidationBatchId(batchId)
                .map(mapper::toDomain);
    }
}

