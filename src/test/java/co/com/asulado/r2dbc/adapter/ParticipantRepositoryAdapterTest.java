package co.com.asulado.r2dbc.adapter;

import co.com.asulado.model.liquidation.Participant;
import co.com.asulado.r2dbc.entity.liquidation.ParticipantEntity;
import co.com.asulado.r2dbc.mapper.BatchParticipantDetailEntityMapper;
import co.com.asulado.r2dbc.repository.BatchParticipantDetailDataRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("Pruebas de ParticipantRepositoryAdapter")
class ParticipantRepositoryAdapterTest {

    @Mock
    private BatchParticipantDetailDataRepository repository;

    @Mock
    private BatchParticipantDetailEntityMapper mapper;

    private ParticipantRepositoryAdapter adapter;

    @BeforeEach
    void setUp() {
        adapter = new ParticipantRepositoryAdapter(repository, mapper);
    }

    @Test
    @DisplayName("Debe obtener participantes por lote y mapear a dominio")
    void shouldFindAllByBatchIdAndMapToDomain() {
        Long batchId = 100L;
        ParticipantEntity e1 = ParticipantEntity.builder().participantId(1L).liquidationBatchId(batchId).grossAmountCop(1000L).build();
        ParticipantEntity e2 = ParticipantEntity.builder().participantId(2L).liquidationBatchId(batchId).grossAmountCop(2000L).build();
        Participant d1 = Participant.builder().participantId(1L).liquidationBatchId(batchId).grossAmountCop(1000L).build();
        Participant d2 = Participant.builder().participantId(2L).liquidationBatchId(batchId).grossAmountCop(2000L).build();

        when(repository.findAllByLiquidationBatchId(batchId)).thenReturn(Flux.just(e1, e2));
        when(mapper.toDomain(e1)).thenReturn(d1);
        when(mapper.toDomain(e2)).thenReturn(d2);

        StepVerifier.create(adapter.findAllByBatchId(batchId))
                .assertNext(p -> {
                    assertNotNull(p);
                    assertEquals(d1.getParticipantId(), p.getParticipantId());
                    assertEquals(d1.getGrossAmountCop(), p.getGrossAmountCop());
                })
                .assertNext(p -> {
                    assertNotNull(p);
                    assertEquals(d2.getParticipantId(), p.getParticipantId());
                    assertEquals(d2.getGrossAmountCop(), p.getGrossAmountCop());
                })
                .verifyComplete();
    }

    @Test
    @DisplayName("Debe retornar vacío cuando no existen participantes")
    void shouldReturnEmptyWhenNoParticipantsFound() {
        Long batchId = 200L;
        when(repository.findAllByLiquidationBatchId(batchId)).thenReturn(Flux.empty());

        StepVerifier.create(adapter.findAllByBatchId(batchId))
                .verifyComplete();
    }

    @Test
    @DisplayName("Debe propagar errores del repositorio")
    void shouldPropagateRepositoryErrors() {
        Long batchId = 300L;
        when(repository.findAllByLiquidationBatchId(batchId))
                .thenReturn(Flux.error(new RuntimeException("DB error")));

        StepVerifier.create(adapter.findAllByBatchId(batchId))
                .expectErrorMatches(t -> t instanceof RuntimeException && t.getMessage().equals("DB error"))
                .verify();
    }
}

