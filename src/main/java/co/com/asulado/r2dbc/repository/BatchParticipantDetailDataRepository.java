package co.com.asulado.r2dbc.repository;

import co.com.asulado.r2dbc.entity.liquidation.ParticipantEntity;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository
public interface BatchParticipantDetailDataRepository extends ReactiveCrudRepository<ParticipantEntity, Long> {
    Mono<Long> countByLiquidationBatchId(Long liquidationBatchId);
    Flux<ParticipantEntity> findAllByLiquidationBatchId(Long liquidationBatchId);
}
