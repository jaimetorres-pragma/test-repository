package co.com.asulado.model.liquidation.gateways;

import co.com.asulado.model.liquidation.ThirdParty;
import reactor.core.publisher.Flux;

public interface ThirdPartyRepository {
    Flux<ThirdParty> findAllByBatchId(Long batchId);
}

