package co.com.asulado.model.liquidation.gateways;

import co.com.asulado.model.liquidation.Participant;
import reactor.core.publisher.Flux;

public interface ParticipantRepository {
    Flux<Participant> findAllByBatchId(Long batchId);
}

