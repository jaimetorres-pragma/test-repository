package co.com.asulado.r2dbc.adapter;

import co.com.asulado.model.constants.DatabaseConstants;
import co.com.asulado.model.deduction.ParticipantDeduction;
import co.com.asulado.model.payments.ParticipantProjection;
import co.com.asulado.r2dbc.entity.row.ParticipantDeductionRow;
import co.com.asulado.r2dbc.mapper.ParticipantsProjectionsMapper;
import co.com.asulado.r2dbc.providers.ParticipantProjectionSQLProvider;
import co.com.asulado.r2dbc.testdata.ParticipantProjectionRepositoryAdapterTestData;
import io.r2dbc.spi.Connection;
import io.r2dbc.spi.Row;
import io.r2dbc.spi.RowMetadata;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.reactivestreams.Publisher;
import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.r2dbc.core.FetchSpec;
import org.springframework.r2dbc.core.RowsFetchSpec;
import org.springframework.transaction.reactive.TransactionalOperator;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.lang.reflect.Method;
import java.time.LocalDate;
import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Function;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@SuppressWarnings("unchecked")
@ExtendWith(MockitoExtension.class)
@DisplayName("Pruebas de ParticipantProjectionRepositoryAdapter")
class ParticipantProjectionRepositoryAdapterTest {

    @Mock
    private DatabaseClient databaseClient;

    @Mock
    private DatabaseClient.GenericExecuteSpec executeSpec;

    @Mock
    private ParticipantProjectionSQLProvider sqlProvider;

    @Mock
    private ParticipantsProjectionsMapper mapper;

    @Mock
    private TransactionalOperator transactionalOperator;

    private ParticipantProjectionRepositoryAdapter adapter;

    @BeforeEach
    void setUp() throws Exception {
        adapter = new ParticipantProjectionRepositoryAdapter(databaseClient, sqlProvider, mapper, transactionalOperator);
        setChunk(adapter);
    }

    @Test
    @DisplayName("Debe encontrar proyecciones de participantes excluyendo las ya liquidadas")
    void shouldFindAllParticipantProjectionsExcludingExistingInLiquidationBatch() {
        // Given
        ParticipantProjection projection1 = createParticipantProjection(
                ParticipantProjectionRepositoryAdapterTestData.PARTICIPANT_ID_1,
                ParticipantProjectionRepositoryAdapterTestData.PAYMENT_DATE_1,
                ParticipantProjectionRepositoryAdapterTestData.FREQ_20
        );
        ParticipantProjection projection2 = createParticipantProjection(
                ParticipantProjectionRepositoryAdapterTestData.PARTICIPANT_ID_2,
                null,
                ParticipantProjectionRepositoryAdapterTestData.FREQ_15
        );

        FetchSpec<ParticipantProjection> mockRowsFetchSpec = (FetchSpec<ParticipantProjection>) mock(FetchSpec.class);

        when(sqlProvider.getParticipantProjectionExcludingLiquidatedQuery()).thenReturn(ParticipantProjectionRepositoryAdapterTestData.SQL);
        when(databaseClient.sql(ParticipantProjectionRepositoryAdapterTestData.SQL)).thenReturn(executeSpec);
        when(executeSpec.map(any(BiFunction.class))).thenReturn(mockRowsFetchSpec);
        when(mockRowsFetchSpec.all()).thenReturn(Flux.just(projection1, projection2));

        // When & Then
        StepVerifier.create(adapter.findAllParticipantProjectionsExcludingExistingInLiquidationBatch())
                .assertNext(projection -> {
                    assertNotNull(projection);
                    assertEquals(ParticipantProjectionRepositoryAdapterTestData.PARTICIPANT_ID_1, projection.getParticipantId());
                    assertEquals(ParticipantProjectionRepositoryAdapterTestData.PAYMENT_DATE_1, projection.getPaymentDate());
                    assertEquals(ParticipantProjectionRepositoryAdapterTestData.FREQ_20, projection.getProductFrequency());
                })
                .assertNext(projection -> {
                    assertNotNull(projection);
                    assertEquals(ParticipantProjectionRepositoryAdapterTestData.PARTICIPANT_ID_2, projection.getParticipantId());
                    assertNull(projection.getPaymentDate());
                    assertEquals(ParticipantProjectionRepositoryAdapterTestData.FREQ_15, projection.getProductFrequency());
                })
                .verifyComplete();
    }

    @Test
    @DisplayName("Debe manejar resultado vacío desde base de datos")
    void shouldHandleEmptyResultFromDatabase() {
        // Given
        FetchSpec<ParticipantProjection> mockRowsFetchSpec = (FetchSpec<ParticipantProjection>) mock(FetchSpec.class);
        when(sqlProvider.getParticipantProjectionExcludingLiquidatedQuery()).thenReturn(ParticipantProjectionRepositoryAdapterTestData.SQL);
        when(databaseClient.sql(anyString())).thenReturn(executeSpec);
        when(executeSpec.map(any(BiFunction.class))).thenReturn(mockRowsFetchSpec);
        when(mockRowsFetchSpec.all()).thenReturn(Flux.empty());

        // When & Then
        StepVerifier.create(adapter.findAllParticipantProjectionsExcludingExistingInLiquidationBatch())
                .verifyComplete();
    }

    @Test
    @DisplayName("Debe manejar error de base de datos")
    void shouldHandleDatabaseError() {
        // Given
        FetchSpec<ParticipantProjection> mockRowsFetchSpec = (FetchSpec<ParticipantProjection>) mock(FetchSpec.class);
        when(sqlProvider.getParticipantProjectionExcludingLiquidatedQuery()).thenReturn(ParticipantProjectionRepositoryAdapterTestData.SQL);
        when(databaseClient.sql(anyString())).thenReturn(executeSpec);
        when(executeSpec.map(any(BiFunction.class))).thenReturn(mockRowsFetchSpec);
        when(mockRowsFetchSpec.all()).thenReturn(Flux.error(new RuntimeException("Database connection error")));

        // When & Then
        StepVerifier.create(adapter.findAllParticipantProjectionsExcludingExistingInLiquidationBatch())
                .expectError(RuntimeException.class)
                .verify();
    }

    @Test
    @DisplayName("Debe mapear fila a ParticipantProjection usando reflexión del método privado")
    void shouldMapRowToParticipantProjectionUsingPrivateMethod() throws Exception {
        Row row = mock(Row.class);
        RowMetadata metadata = mock(RowMetadata.class);

        when(row.get(DatabaseConstants.COLUMN_PARTICIPANT_ID, String.class))
                .thenReturn(ParticipantProjectionRepositoryAdapterTestData.PARTICIPANT_ID_3);
        when(row.get(DatabaseConstants.COLUMN_PAYMENT_DATE, LocalDate.class))
                .thenReturn(ParticipantProjectionRepositoryAdapterTestData.PAYMENT_DATE_2);
        when(row.get(DatabaseConstants.COLUMN_PRODUCT_FREQUENCY, String.class))
                .thenReturn(ParticipantProjectionRepositoryAdapterTestData.FREQ_21);
        when(row.get(DatabaseConstants.COLUMN_PAYMENT_REQUEST_ID, String.class))
                .thenReturn(ParticipantProjectionRepositoryAdapterTestData.PAYMENT_REQUEST_ID);
        when(row.get(DatabaseConstants.COLUMN_PERSON_ID, Long.class))
                .thenReturn(ParticipantProjectionRepositoryAdapterTestData.PERSON_ID);
        when(row.get(DatabaseConstants.COLUMN_PRODUCT_NAME, String.class))
                .thenReturn(ParticipantProjectionRepositoryAdapterTestData.PRODUCT_PENSION);

        Method method = ParticipantProjectionRepositoryAdapter.class
                .getDeclaredMethod("mapRowToParticipantProjection", Row.class, RowMetadata.class);
        method.setAccessible(true);

        ParticipantProjection result = (ParticipantProjection) method.invoke(adapter, row, metadata);

        assertNotNull(result);
        assertEquals(ParticipantProjectionRepositoryAdapterTestData.PARTICIPANT_ID_3, result.getParticipantId());
        assertEquals(ParticipantProjectionRepositoryAdapterTestData.PAYMENT_DATE_2, result.getPaymentDate());
        assertEquals(ParticipantProjectionRepositoryAdapterTestData.FREQ_21, result.getProductFrequency());
        assertEquals(ParticipantProjectionRepositoryAdapterTestData.PAYMENT_REQUEST_ID, result.getPaymentRequestId());
        assertEquals(ParticipantProjectionRepositoryAdapterTestData.PERSON_ID, result.getPersonId());
        assertEquals(ParticipantProjectionRepositoryAdapterTestData.PRODUCT_PENSION, result.getProductName());
    }

    private ParticipantProjection createParticipantProjection(String participantId, LocalDate paymentDate, String frequency) {
        return ParticipantProjection.builder()
                .participantId(participantId)
                .paymentDate(paymentDate)
                .productFrequency(frequency)
                .build();
    }

    @Test
    @DisplayName("findAllParticipantsDeductions: agrupa por participantId y mapea a dominio")
    void shouldGroupByParticipantAndMapToDomain() {
        String ids = "P1,P2";
        String sql = "SELECT ...";
        when(sqlProvider.getParticipantsDeductionAndIncomeQuery(ids)).thenReturn(sql);
        when(databaseClient.sql(sql)).thenReturn(executeSpec);

        @SuppressWarnings("unchecked")
        RowsFetchSpec<ParticipantDeductionRow> rowsSpec = (RowsFetchSpec<ParticipantDeductionRow>) mock(RowsFetchSpec.class);
        when(executeSpec.map(any(BiFunction.class))).thenReturn(rowsSpec);

        ParticipantDeductionRow r1 = ParticipantDeductionRow.builder()
                .participantId(1L)
                .build();

        when(rowsSpec.all()).thenReturn(Flux.just(r1));

        ParticipantDeduction dP1 = mock(ParticipantDeduction.class);
        when(mapper.toDomain(argThat(list -> list.size() == 1))).thenReturn(dP1);

        StepVerifier.create(adapter.findAllParticipantsDeductions(ids).collectList())
                .assertNext(list -> {
                    assertEquals(1, list.size());
                    assertTrue(list.contains(dP1));
                })
                .verifyComplete();
    }

    @Test
    @DisplayName("findAllParticipantsDeductions: resultado vacío")
    void shouldCompleteOnEmptyResult() {
        when(sqlProvider.getParticipantsDeductionAndIncomeQuery(anyString())).thenReturn("SQL");
        when(databaseClient.sql("SQL")).thenReturn(executeSpec);

        @SuppressWarnings("unchecked")
        RowsFetchSpec<ParticipantDeductionRow> rowsSpec = (RowsFetchSpec<ParticipantDeductionRow>) mock(RowsFetchSpec.class);
        when(executeSpec.map(any(BiFunction.class))).thenReturn(rowsSpec);
        when(rowsSpec.all()).thenReturn(Flux.empty());

        StepVerifier.create(adapter.findAllParticipantsDeductions("ANY"))
                .verifyComplete();
    }

    @Test
    @DisplayName("findAllParticipantsDeductions: error en DB se propaga")
    void shouldPropagateDatabaseError() {
        when(sqlProvider.getParticipantsDeductionAndIncomeQuery(anyString())).thenReturn("SQL");
        when(databaseClient.sql("SQL")).thenReturn(executeSpec);

        @SuppressWarnings("unchecked")
        RowsFetchSpec<ParticipantDeductionRow> rowsSpec = (RowsFetchSpec<ParticipantDeductionRow>) mock(RowsFetchSpec.class);
        when(executeSpec.map(any(BiFunction.class))).thenReturn(rowsSpec);
        when(rowsSpec.all()).thenReturn(Flux.error(new RuntimeException("DB error")));

        StepVerifier.create(adapter.findAllParticipantsDeductions("ANY"))
                .expectErrorMessage("DB error")
                .verify();
    }

    @Test
    @DisplayName("updateAllParticipantsDeductions: actualiza (1 lote) y completa")
    void shouldUpdateAllParticipantsAndComplete() {
        ParticipantDeduction p1 = mock(ParticipantDeduction.class);
        List<ParticipantDeduction> input = List.of(p1);

        when(sqlProvider.updateIncomeParticipant()).thenReturn("UPDATE ...");


        when(transactionalOperator.transactional(any(Mono.class)))
                .thenAnswer(inv -> inv.getArgument(0));

        when(databaseClient.inConnectionMany(any())).thenAnswer(inv -> {
            @SuppressWarnings("unchecked")
            Function<Connection, Publisher<Long>> fn =
                    (Function<io.r2dbc.spi.Connection, Publisher<Long>>) inv.getArgument(0);
            return fn.apply(mock(io.r2dbc.spi.Connection.class));
        });

        when(mapper.executeParticipantDeduction(anyList(), any(), anyString()))
                .thenReturn(Flux.just(1L));

        StepVerifier.create(adapter.updateAllParticipantsDeductions(input))
                .verifyComplete();
    }

    @Test
    @DisplayName("updateAllParticipantsDeductions: lista vacía completa sin llamar a DB")
    void shouldCompleteWhenListIsEmpty() {
        StepVerifier.create(adapter.updateAllParticipantsDeductions(List.of()))
                .verifyComplete();

        verifyNoInteractions(databaseClient, mapper, sqlProvider, transactionalOperator);
    }

    @Test
    @DisplayName("updateAllParticipantsDeductions: error del mapper/DB se propaga")
    void shouldPropagateErrorWhenDbFails() {
        ParticipantDeduction p1 = mock(ParticipantDeduction.class);
        when(sqlProvider.updateIncomeParticipant()).thenReturn("UPDATE ...");
        when(transactionalOperator.transactional(any(Mono.class)))
                .thenAnswer(inv -> inv.getArgument(0));
        when(databaseClient.inConnectionMany(any())).thenAnswer(inv -> {
            @SuppressWarnings("unchecked")
            Function<io.r2dbc.spi.Connection, Publisher<Long>> fn =
                    (Function<io.r2dbc.spi.Connection, Publisher<Long>>) inv.getArgument(0);
            return fn.apply(mock(io.r2dbc.spi.Connection.class));
        });
        when(mapper.executeParticipantDeduction(anyList(), any(), anyString()))
                .thenReturn(Flux.error(new IllegalStateException("boom")));

        StepVerifier.create(adapter.updateAllParticipantsDeductions(List.of(p1)))
                .expectErrorMessage("boom")
                .verify();
    }

    private static void setChunk(Object target) throws Exception {
        var f = target.getClass().getDeclaredField("chunk");
        f.setAccessible(true);
        f.setInt(target, 10);
    }

}
