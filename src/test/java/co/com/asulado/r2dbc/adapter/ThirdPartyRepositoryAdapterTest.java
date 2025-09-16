package co.com.asulado.r2dbc.adapter;

import co.com.asulado.model.liquidation.ThirdParty;
import co.com.asulado.r2dbc.entity.liquidation.ThirdPartyEntity;
import co.com.asulado.r2dbc.mapper.ThirdPartyEntityMapper;
import co.com.asulado.r2dbc.repository.ThirdPartyDataRepository;
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
@DisplayName("Pruebas de ThirdPartyRepositoryAdapter")
class ThirdPartyRepositoryAdapterTest {

    @Mock
    private ThirdPartyDataRepository repository;

    @Mock
    private ThirdPartyEntityMapper mapper;

    private ThirdPartyRepositoryAdapter adapter;

    @BeforeEach
    void setUp() {
        adapter = new ThirdPartyRepositoryAdapter(repository, mapper);
    }

    @Test
    @DisplayName("Debe obtener terceros por lote y mapear a dominio")
    void shouldFindAllByBatchIdAndMapToDomain() {
        Long batchId = 500L;
        ThirdPartyEntity e1 = ThirdPartyEntity.builder().thirdPartyId(1L).liquidationBatchId(batchId).grossAmountCop(700L).build();
        ThirdPartyEntity e2 = ThirdPartyEntity.builder().thirdPartyId(2L).liquidationBatchId(batchId).grossAmountCop(900L).build();
        ThirdParty d1 = ThirdParty.builder().thirdPartyId(1L).liquidationBatchId(batchId).grossAmountCop(700L).build();
        ThirdParty d2 = ThirdParty.builder().thirdPartyId(2L).liquidationBatchId(batchId).grossAmountCop(900L).build();

        when(repository.findAllByLiquidationBatchId(batchId)).thenReturn(Flux.just(e1, e2));
        when(mapper.toDomain(e1)).thenReturn(d1);
        when(mapper.toDomain(e2)).thenReturn(d2);

        StepVerifier.create(adapter.findAllByBatchId(batchId))
                .assertNext(t -> {
                    assertNotNull(t);
                    assertEquals(d1.getThirdPartyId(), t.getThirdPartyId());
                    assertEquals(d1.getGrossAmountCop(), t.getGrossAmountCop());
                })
                .assertNext(t -> {
                    assertNotNull(t);
                    assertEquals(d2.getThirdPartyId(), t.getThirdPartyId());
                    assertEquals(d2.getGrossAmountCop(), t.getGrossAmountCop());
                })
                .verifyComplete();
    }

    @Test
    @DisplayName("Debe retornar vacío cuando no existen terceros")
    void shouldReturnEmptyWhenNoThirdPartiesFound() {
        Long batchId = 600L;
        when(repository.findAllByLiquidationBatchId(batchId)).thenReturn(Flux.empty());

        StepVerifier.create(adapter.findAllByBatchId(batchId))
                .verifyComplete();
    }

    @Test
    @DisplayName("Debe propagar errores del repositorio")
    void shouldPropagateRepositoryErrors() {
        Long batchId = 700L;
        when(repository.findAllByLiquidationBatchId(batchId))
                .thenReturn(Flux.error(new RuntimeException("DB error")));

        StepVerifier.create(adapter.findAllByBatchId(batchId))
                .expectErrorMatches(t -> t instanceof RuntimeException && t.getMessage().equals("DB error"))
                .verify();
    }
}

