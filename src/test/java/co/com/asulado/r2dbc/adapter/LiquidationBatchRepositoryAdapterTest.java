package co.com.asulado.r2dbc.adapter;

import co.com.asulado.model.liquidation.LiquidationBatch;
import co.com.asulado.model.liquidation.Participant;
import co.com.asulado.r2dbc.entity.liquidation.LiquidationBatchEntity;
import co.com.asulado.r2dbc.entity.liquidation.ParticipantEntity;
import co.com.asulado.r2dbc.mapper.BatchParticipantDetailEntityMapper;
import co.com.asulado.r2dbc.mapper.LiquidationBatchEntityMapper;
import co.com.asulado.r2dbc.repository.BatchParticipantDetailDataRepository;
import co.com.asulado.r2dbc.repository.LiquidationBatchDataRepository;
import co.com.asulado.r2dbc.testdata.LiquidationBatchRepositoryAdapterTestData;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("Pruebas de LiquidationBatchRepositoryAdapter")
class LiquidationBatchRepositoryAdapterTest {

    @Mock
    private LiquidationBatchDataRepository liquidationBatchDataRepository;

    @Mock
    private BatchParticipantDetailDataRepository batchParticipantDetailDataRepository;

    @Mock
    private LiquidationBatchEntityMapper liquidationBatchEntityMapper;

    @Mock
    private BatchParticipantDetailEntityMapper batchParticipantDetailEntityMapper;

    @InjectMocks
    private LiquidationBatchRepositoryAdapter adapter;

    @Test
    @DisplayName("Debe guardar lote con participantes exitosamente")
    void shouldSaveLiquidationBatchWithParticipantsSuccessfully() {
        LiquidationBatch batch = LiquidationBatch.builder()
            .liquidationDate(LiquidationBatchRepositoryAdapterTestData.DATE_2025_08_26)
            .dispersionDate(LiquidationBatchRepositoryAdapterTestData.DATE_2025_08_29)
            .status(LiquidationBatchRepositoryAdapterTestData.STATUS_NEW)
            .participantCount(2)
            .build();

        List<Participant> participants = List.of(
            Participant.builder()
                .participantPaymentId(UUID.randomUUID())
                .participantPaymentStatus("ACTIVO")
                .grossAmountCop(LiquidationBatchRepositoryAdapterTestData.AMOUNT_1000)
                .build(),
            Participant.builder()
                .participantPaymentId(UUID.randomUUID())
                .participantPaymentStatus("ACTIVO")
                .grossAmountCop(LiquidationBatchRepositoryAdapterTestData.AMOUNT_2000)
                .build()
        );

        LiquidationBatchEntity batchEntity = LiquidationBatchEntity.builder()
            .batchId(LiquidationBatchRepositoryAdapterTestData.BATCH_ID_1)
            .status(LiquidationBatchRepositoryAdapterTestData.STATUS_NEW)
            .build();

        LiquidationBatch savedBatch = batch.toBuilder()
            .batchId(LiquidationBatchRepositoryAdapterTestData.BATCH_ID_1)
            .build();

        ParticipantEntity participantEntity1 = ParticipantEntity.builder()
            .participantId(1L)
            .liquidationBatchId(LiquidationBatchRepositoryAdapterTestData.BATCH_ID_1)
            .build();

        ParticipantEntity participantEntity2 = ParticipantEntity.builder()
            .participantId(2L)
            .liquidationBatchId(LiquidationBatchRepositoryAdapterTestData.BATCH_ID_1)
            .build();

        when(liquidationBatchEntityMapper.toEntity(batch)).thenReturn(batchEntity);
        when(liquidationBatchDataRepository.save(batchEntity)).thenReturn(Mono.just(batchEntity));
        when(liquidationBatchEntityMapper.toDomain(batchEntity)).thenReturn(savedBatch);

        when(batchParticipantDetailEntityMapper.toEntity(any(Participant.class)))
            .thenReturn(participantEntity1, participantEntity2);
        when(batchParticipantDetailDataRepository.save(any(ParticipantEntity.class)))
            .thenReturn(Mono.just(participantEntity1), Mono.just(participantEntity2));
        when(batchParticipantDetailEntityMapper.toDomain(any(ParticipantEntity.class)))
            .thenReturn(participants.getFirst(), participants.get(1));

        StepVerifier.create(adapter.saveLiquidationBatchWithParticipants(batch, participants))
            .assertNext(result -> {
                assertNotNull(result);
                assertEquals(LiquidationBatchRepositoryAdapterTestData.BATCH_ID_1, result.getBatchId());
            })
            .verifyComplete();

        verify(liquidationBatchDataRepository).save(batchEntity);
        verify(batchParticipantDetailDataRepository, times(2)).save(any(ParticipantEntity.class));
    }

    @Test
    @DisplayName("Debe manejar error cuando falla el guardado del lote")
    void shouldHandleErrorWhenBatchSaveFails() {
        LiquidationBatch batch = LiquidationBatch.builder()
            .status(LiquidationBatchRepositoryAdapterTestData.STATUS_NEW)
            .build();
        List<Participant> participants = List.of();

        LiquidationBatchEntity batchEntity = LiquidationBatchEntity.builder()
            .status(LiquidationBatchRepositoryAdapterTestData.STATUS_NEW)
            .build();

        when(liquidationBatchEntityMapper.toEntity(batch)).thenReturn(batchEntity);
        when(liquidationBatchDataRepository.save(batchEntity))
            .thenReturn(Mono.error(new RuntimeException("Database error")));

        StepVerifier.create(adapter.saveLiquidationBatchWithParticipants(batch, participants))
            .expectError(RuntimeException.class)
            .verify();
    }

    @Test
    @DisplayName("Debe manejar lista vacía de participantes")
    void shouldHandleEmptyParticipantsList() {
        LiquidationBatch batch = LiquidationBatch.builder()
            .status(LiquidationBatchRepositoryAdapterTestData.STATUS_NEW)
            .build();
        List<Participant> participants = List.of();

        LiquidationBatchEntity batchEntity = LiquidationBatchEntity.builder()
            .batchId(LiquidationBatchRepositoryAdapterTestData.BATCH_ID_1)
            .status(LiquidationBatchRepositoryAdapterTestData.STATUS_NEW)
            .build();

        LiquidationBatch savedBatch = batch.toBuilder()
            .batchId(LiquidationBatchRepositoryAdapterTestData.BATCH_ID_1)
            .build();

        when(liquidationBatchEntityMapper.toEntity(batch)).thenReturn(batchEntity);
        when(liquidationBatchDataRepository.save(batchEntity)).thenReturn(Mono.just(batchEntity));
        when(liquidationBatchEntityMapper.toDomain(batchEntity)).thenReturn(savedBatch);

        StepVerifier.create(adapter.saveLiquidationBatchWithParticipants(batch, participants))
            .assertNext(result -> {
                assertNotNull(result);
                assertEquals(LiquidationBatchRepositoryAdapterTestData.BATCH_ID_1, result.getBatchId());
            })
            .verifyComplete();

        verify(batchParticipantDetailDataRepository, never()).save(any());
    }

    @Test
    @DisplayName("Debe encontrar por fecha de liquidación y mapear a dominio")
    void shouldFindByLiquidationDateMapEntitiesToDomain() {
        LocalDate date = LiquidationBatchRepositoryAdapterTestData.DATE_2025_08_27;
        LiquidationBatchEntity e1 = LiquidationBatchEntity.builder().batchId(LiquidationBatchRepositoryAdapterTestData.BATCH_ID_10).build();
        LiquidationBatchEntity e2 = LiquidationBatchEntity.builder().batchId(LiquidationBatchRepositoryAdapterTestData.BATCH_ID_20).build();
        LiquidationBatch d1 = LiquidationBatch.builder().batchId(LiquidationBatchRepositoryAdapterTestData.BATCH_ID_10).build();
        LiquidationBatch d2 = LiquidationBatch.builder().batchId(LiquidationBatchRepositoryAdapterTestData.BATCH_ID_20).build();

        when(liquidationBatchDataRepository.findAllByLiquidationDate(date)).thenReturn(Flux.just(e1, e2));
        when(liquidationBatchEntityMapper.toDomain(e1)).thenReturn(d1);
        when(liquidationBatchEntityMapper.toDomain(e2)).thenReturn(d2);

        StepVerifier.create(adapter.findByLiquidationDate(date))
            .assertNext(b -> assertEquals(LiquidationBatchRepositoryAdapterTestData.BATCH_ID_10, b.getBatchId()))
            .assertNext(b -> assertEquals(LiquidationBatchRepositoryAdapterTestData.BATCH_ID_20, b.getBatchId()))
            .verifyComplete();
    }

    @Test
    @DisplayName("Debe encontrar por fecha de liquidación mayor o igual y mapear a dominio")
    void shouldFindByLiquidationDateGreaterOrEqualMapEntitiesToDomain() {
        LocalDate date = LiquidationBatchRepositoryAdapterTestData.DATE_2025_08_27;
        LiquidationBatchEntity e1 = LiquidationBatchEntity.builder().batchId(LiquidationBatchRepositoryAdapterTestData.BATCH_ID_30).build();
        LiquidationBatchEntity e2 = LiquidationBatchEntity.builder().batchId(LiquidationBatchRepositoryAdapterTestData.BATCH_ID_40).build();
        LiquidationBatch d1 = LiquidationBatch.builder().batchId(LiquidationBatchRepositoryAdapterTestData.BATCH_ID_30).build();
        LiquidationBatch d2 = LiquidationBatch.builder().batchId(LiquidationBatchRepositoryAdapterTestData.BATCH_ID_40).build();

        when(liquidationBatchDataRepository.findAllByLiquidationDateGreaterThanEqual(date)).thenReturn(Flux.just(e1, e2));
        when(liquidationBatchEntityMapper.toDomain(e1)).thenReturn(d1);
        when(liquidationBatchEntityMapper.toDomain(e2)).thenReturn(d2);

        StepVerifier.create(adapter.findByLiquidationDateGreaterOrEqual(date))
            .assertNext(b -> assertEquals(LiquidationBatchRepositoryAdapterTestData.BATCH_ID_30, b.getBatchId()))
            .assertNext(b -> assertEquals(LiquidationBatchRepositoryAdapterTestData.BATCH_ID_40, b.getBatchId()))
            .verifyComplete();
    }

    @Test
    @DisplayName("Debe agregar participantes al lote y completar")
    void shouldAddParticipantsToBatchAndComplete() {
        Long batchId = LiquidationBatchRepositoryAdapterTestData.BATCH_ID_99;
        Participant p1 = Participant.builder().participantPaymentId(UUID.randomUUID()).grossAmountCop(LiquidationBatchRepositoryAdapterTestData.AMOUNT_1).build();
        Participant p2 = Participant.builder().participantPaymentId(UUID.randomUUID()).grossAmountCop(LiquidationBatchRepositoryAdapterTestData.AMOUNT_2).build();
        ParticipantEntity e1 = ParticipantEntity.builder().participantId(1L).liquidationBatchId(batchId).build();
        ParticipantEntity e2 = ParticipantEntity.builder().participantId(2L).liquidationBatchId(batchId).build();

        when(batchParticipantDetailEntityMapper.toEntity(any(Participant.class))).thenReturn(e1, e2);
        when(batchParticipantDetailDataRepository.save(any(ParticipantEntity.class)))
            .thenReturn(Mono.just(e1), Mono.just(e2));

        StepVerifier.create(adapter.addParticipantsToBatch(batchId, List.of(p1, p2)))
            .verifyComplete();

        verify(batchParticipantDetailDataRepository, times(2)).save(any(ParticipantEntity.class));
    }

    @Test
    @DisplayName("Debe contar participantes en el lote")
    void shouldCountParticipantsInBatch() {
        when(batchParticipantDetailDataRepository.countByLiquidationBatchId(LiquidationBatchRepositoryAdapterTestData.BATCH_ID_10)).thenReturn(Mono.just(5L));
        StepVerifier.create(adapter.countParticipantsInBatch(LiquidationBatchRepositoryAdapterTestData.BATCH_ID_10))
            .assertNext(count -> assertEquals(5L, count))
            .verifyComplete();
    }

    @Test
    @DisplayName("Debe actualizar cantidad de participantes y completar")
    void shouldUpdateParticipantCountComplete() {
        when(liquidationBatchDataRepository.updateParticipantCount(anyLong(), anyInt(), any())).thenReturn(Mono.just(1));
        StepVerifier.create(adapter.updateParticipantCount(LiquidationBatchRepositoryAdapterTestData.BATCH_ID_1, 10))
            .verifyComplete();
    }

    @Test
    @DisplayName("Debe incrementar cantidad de participantes y completar")
    void shouldIncrementParticipantCountComplete() {
        when(liquidationBatchDataRepository.incrementParticipantCount(anyLong(), anyInt(), any())).thenReturn(Mono.just(1));
        StepVerifier.create(adapter.incrementParticipantCount(LiquidationBatchRepositoryAdapterTestData.BATCH_ID_1, 2))
            .verifyComplete();
    }

    @Test
    @DisplayName("Debe actualizar totales de participantes y completar")
    void shouldUpdateParticipantTotalsComplete() {
        when(liquidationBatchDataRepository.updateParticipantTotals(anyLong(), anyInt(), anyLong(), anyLong(), anyLong(), any())).thenReturn(Mono.just(1));
        StepVerifier.create(adapter.updateParticipantTotals(LiquidationBatchRepositoryAdapterTestData.BATCH_ID_1, 2, 100L, 10L, 90L))
            .verifyComplete();
    }

    @Test
    @DisplayName("Debe actualizar totales de terceros y completar")
    void shouldUpdateThirdPartyTotalsComplete() {
        when(liquidationBatchDataRepository.updateThirdPartyTotals(anyLong(), anyInt(), anyLong(), anyLong(), anyLong(), any())).thenReturn(Mono.just(1));
        StepVerifier.create(adapter.updateThirdPartyTotals(LiquidationBatchRepositoryAdapterTestData.BATCH_ID_1, 3, 200L, 20L, 180L))
            .verifyComplete();
    }

    @Test
    @DisplayName("Debe recomputar totales del lote y completar")
    void shouldRecomputeBatchTotalsComplete() {
        when(liquidationBatchDataRepository.recomputeBatchTotals(anyLong(), any())).thenReturn(Mono.just(1));
        StepVerifier.create(adapter.recomputeBatchTotals(LiquidationBatchRepositoryAdapterTestData.BATCH_ID_1))
            .verifyComplete();
    }
}
