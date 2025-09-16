package co.com.asulado.r2dbc.repository;

import co.com.asulado.r2dbc.entity.liquidation.DeductionEntity;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;

public interface DeductionDataRepository extends ReactiveCrudRepository<DeductionEntity, Long> {
    Flux<DeductionEntity> findAllByParticipantId(Long participantId);
}
