package co.com.asulado.r2dbc.repository;

import co.com.asulado.r2dbc.entity.liquidation.ThirdPartyEntity;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;

@Repository
public interface ThirdPartyDataRepository extends ReactiveCrudRepository<ThirdPartyEntity, Long> {
    Flux<ThirdPartyEntity> findAllByLiquidationBatchId(Long liquidationBatchId);
}

