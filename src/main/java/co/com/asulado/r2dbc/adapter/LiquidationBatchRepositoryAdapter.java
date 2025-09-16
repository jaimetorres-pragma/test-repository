package co.com.asulado.r2dbc.adapter;

import co.com.asulado.model.liquidation.LiquidationBatch;
import co.com.asulado.model.liquidation.Participant;
import co.com.asulado.model.liquidation.gateways.LiquidationBatchRepository;
import co.com.asulado.r2dbc.mapper.BatchParticipantDetailEntityMapper;
import co.com.asulado.r2dbc.mapper.LiquidationBatchEntityMapper;
import co.com.asulado.r2dbc.repository.BatchParticipantDetailDataRepository;
import co.com.asulado.r2dbc.repository.LiquidationBatchDataRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class LiquidationBatchRepositoryAdapter implements LiquidationBatchRepository {

    private final LiquidationBatchDataRepository liquidationBatchDataRepository;
    private final BatchParticipantDetailDataRepository batchParticipantDetailDataRepository;
    private final LiquidationBatchEntityMapper liquidationBatchEntityMapper;
    private final BatchParticipantDetailEntityMapper batchParticipantDetailEntityMapper;


    @Override
    @Transactional
    public Mono<LiquidationBatch> saveLiquidationBatchWithParticipants(
            LiquidationBatch liquidationBatch,
            List<Participant> participants) {

        return Mono.just(liquidationBatch)
                .map(liquidationBatchEntityMapper::toEntity)
                .flatMap(liquidationBatchDataRepository::save)
                .map(liquidationBatchEntityMapper::toDomain)
                .flatMap(savedBatch -> Flux.fromIterable(participants)
                        .map(participant -> participant.toBuilder().liquidationBatchId(savedBatch.getBatchId()).build())
                        .map(batchParticipantDetailEntityMapper::toEntity)
                        .flatMap(batchParticipantDetailDataRepository::save)
                        .map(batchParticipantDetailEntityMapper::toDomain)
                        .collectList()
                        .map(savedParticipants -> savedBatch));
    }

    @Override
    public Flux<LiquidationBatch> findByLiquidationDate(LocalDate liquidationDate) {
        return liquidationBatchDataRepository.findAllByLiquidationDate(liquidationDate)
                .map(liquidationBatchEntityMapper::toDomain);
    }

    @Override
    public Flux<LiquidationBatch> findByLiquidationDateGreaterOrEqual(LocalDate fromDate) {
        return liquidationBatchDataRepository.findAllByLiquidationDateGreaterThanEqual(fromDate)
                .map(liquidationBatchEntityMapper::toDomain);
    }

    @Override
    @Transactional
    public Mono<Void> addParticipantsToBatch(Long batchId, List<Participant> participants) {
        return Flux.fromIterable(participants)
                .map(p -> p.toBuilder().liquidationBatchId(batchId).build())
                .map(batchParticipantDetailEntityMapper::toEntity)
                .flatMap(batchParticipantDetailDataRepository::save)
                .then();
    }

    @Override
    public Mono<Long> countParticipantsInBatch(Long batchId) {
        return batchParticipantDetailDataRepository.countByLiquidationBatchId(batchId);
    }

    @Override
    public Mono<Void> updateParticipantCount(Long batchId, Integer participantCount) {
        return liquidationBatchDataRepository
                .updateParticipantCount(batchId, participantCount, LocalDateTime.now())
                .then();
    }

    @Override
    public Mono<Void> incrementParticipantCount(Long batchId, Integer delta) {
        return liquidationBatchDataRepository
                .incrementParticipantCount(batchId, delta, LocalDateTime.now())
                .then();
    }

    @Override
    public Mono<Void> updateParticipantTotals(Long batchId, Integer count, Long gross, Long deductions, Long net) {
        return liquidationBatchDataRepository
                .updateParticipantTotals(batchId, count, gross, deductions, net, LocalDateTime.now())
                .then();
    }

    @Override
    public Mono<Void> updateThirdPartyTotals(Long batchId, Integer count, Long gross, Long deductions, Long net) {
        return liquidationBatchDataRepository
                .updateThirdPartyTotals(batchId, count, gross, deductions, net, LocalDateTime.now())
                .then();
    }

    @Override
    public Mono<Void> recomputeBatchTotals(Long batchId) {
        return liquidationBatchDataRepository
                .recomputeBatchTotals(batchId, LocalDateTime.now())
                .then();
    }
}
