package co.com.asulado.usecase.liquidation.batchcreation.steps;

import co.com.asulado.model.liquidation.LiquidationBatch;
import co.com.asulado.model.liquidation.gateways.LiquidationBatchRepository;
import co.com.asulado.usecase.liquidation.batchcreation.util.ProcessingContext;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("Pruebas de UpdateParticipantCountsStep")
class UpdateParticipantCountsStepTest {

    @Mock
    private LiquidationBatchRepository liquidationBatchRepository;

    @Test
    @DisplayName("Debe actualizar el conteo de participantes para lotes creados en el contexto")
    void shouldUpdateCountForCreatedBatches() {
        var step = new UpdateParticipantCountsStep(liquidationBatchRepository);

        var created = List.of(
            LiquidationBatch.builder().batchId(10L).build(),
            LiquidationBatch.builder().batchId(20L).build()
        );

        ProcessingContext ctx = ProcessingContext.builder()
            .createdBatches(created)
            .groupedByPaymentDate(Map.of())
            .liquidationDates(Map.of())
            .currentDate(LocalDate.of(2025, 8, 28))
            .build();

        when(liquidationBatchRepository.findByLiquidationDateGreaterOrEqual(any(LocalDate.class)))
            .thenReturn(Flux.empty());
        when(liquidationBatchRepository.countParticipantsInBatch(10L)).thenReturn(Mono.just(3L));
        when(liquidationBatchRepository.countParticipantsInBatch(20L)).thenReturn(Mono.just(5L));
        when(liquidationBatchRepository.updateParticipantCount(any(Long.class), any(Integer.class)))
            .thenReturn(Mono.empty());

        StepVerifier.create(step.process(ctx))
            .expectNext(ctx)
            .verifyComplete();

        ArgumentCaptor<Long> idCaptor = ArgumentCaptor.forClass(Long.class);
        ArgumentCaptor<Integer> countCaptor = ArgumentCaptor.forClass(Integer.class);
        verify(liquidationBatchRepository, times(2)).updateParticipantCount(idCaptor.capture(), countCaptor.capture());

        List<Long> ids = idCaptor.getAllValues();
        List<Integer> counts = countCaptor.getAllValues();

        Map<Long, Integer> expected = new HashMap<>();
        expected.put(10L, 3);
        expected.put(20L, 5);

        assertEquals(2, ids.size());
        for (int i = 0; i < ids.size(); i++) {
            Long id = ids.get(i);
            Integer c = counts.get(i);
            assertTrue(expected.containsKey(id));
            assertEquals(expected.get(id), c);
        }
    }

    @Test
    @DisplayName("Debe actualizar conteo para todos los lotes con fecha de liquidación >= fecha actual")
    void shouldUpdateCountForAllBatchesWithLiqDateFromCurrentDate() {
        var step = new UpdateParticipantCountsStep(liquidationBatchRepository);

        LocalDate current = LocalDate.of(2025, 8, 28);
        ProcessingContext ctx = ProcessingContext.builder()
            .groupedByPaymentDate(Map.of())
            .liquidationDates(Map.of())
            .currentDate(current)
            .build();

        var batchA = LiquidationBatch.builder().batchId(3L).createdAt(LocalDateTime.of(2025, 8, 28, 9, 0)).build();
        var batchB = LiquidationBatch.builder().batchId(7L).createdAt(LocalDateTime.of(2025, 8, 28, 12, 0)).build();

        when(liquidationBatchRepository.findByLiquidationDateGreaterOrEqual(current))
            .thenReturn(Flux.just(batchA, batchB));
        when(liquidationBatchRepository.countParticipantsInBatch(3L)).thenReturn(Mono.just(4L));
        when(liquidationBatchRepository.countParticipantsInBatch(7L)).thenReturn(Mono.just(6L));
        when(liquidationBatchRepository.updateParticipantCount(any(Long.class), any(Integer.class)))
            .thenReturn(Mono.empty());

        StepVerifier.create(step.process(ctx))
            .expectNext(ctx)
            .verifyComplete();

        verify(liquidationBatchRepository, times(1)).updateParticipantCount(3L, 4);
        verify(liquidationBatchRepository, times(1)).updateParticipantCount(7L, 6);
    }

    @Test
    @DisplayName("No debe actualizar cuando no hay lotes en repositorio ni creados")
    void shouldNotUpdateWhenNoRepoBatchesNorCreatedBatches() {
        var step = new UpdateParticipantCountsStep(liquidationBatchRepository);

        ProcessingContext ctx = ProcessingContext.builder()
            .groupedByPaymentDate(Map.of())
            .liquidationDates(Map.of())
            .currentDate(LocalDate.of(2025, 8, 28))
            .build();

        when(liquidationBatchRepository.findByLiquidationDateGreaterOrEqual(any(LocalDate.class)))
            .thenReturn(Flux.empty());

        StepVerifier.create(step.process(ctx))
            .expectNext(ctx)
            .verifyComplete();

        verify(liquidationBatchRepository, times(1)).findByLiquidationDateGreaterOrEqual(any(LocalDate.class));
        verify(liquidationBatchRepository, never()).updateParticipantCount(any(), any());
    }
}
