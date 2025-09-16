package co.com.asulado.model.payments.gateways;

import co.com.asulado.model.deduction.ParticipantDeduction;
import co.com.asulado.model.payments.ParticipantProjection;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

public interface ParticipantProjectionRepository {

    Flux<ParticipantProjection> findAllParticipantProjectionsExcludingExistingInLiquidationBatch();
    Flux<ParticipantDeduction> findAllParticipantsDeductions(String participantsIds);
    Mono<Void> updateAllParticipantsDeductions(List<ParticipantDeduction> participantDeductions);
}
