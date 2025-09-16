package co.com.asulado.model.liquidation.gateways;

import co.com.asulado.model.liquidation.LiquidationBatch;
import co.com.asulado.model.liquidation.Participant;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDate;
import java.util.List;

public interface LiquidationBatchRepository {

    Mono<LiquidationBatch> saveLiquidationBatchWithParticipants(
            LiquidationBatch liquidationBatch,
            List<Participant> participants);

    Flux<LiquidationBatch> findByLiquidationDate(LocalDate liquidationDate);

    Flux<LiquidationBatch> findByLiquidationDateGreaterOrEqual(LocalDate fromDate);

    Mono<Void> addParticipantsToBatch(Long batchId, List<Participant> participants);

    Mono<Long> countParticipantsInBatch(Long batchId);

    Mono<Void> updateParticipantCount(Long batchId, Integer participantCount);

    Mono<Void> incrementParticipantCount(Long batchId, Integer delta);

    Mono<Void> updateParticipantTotals(Long batchId, Integer count, Long gross, Long deductions, Long net);

    Mono<Void> updateThirdPartyTotals(Long batchId, Integer count, Long gross, Long deductions, Long net);

    Mono<Void> recomputeBatchTotals(Long batchId);
}
