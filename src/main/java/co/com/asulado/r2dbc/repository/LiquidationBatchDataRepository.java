package co.com.asulado.r2dbc.repository;

import co.com.asulado.r2dbc.entity.liquidation.LiquidationBatchEntity;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Repository
public interface LiquidationBatchDataRepository extends ReactiveCrudRepository<LiquidationBatchEntity, Long> {


    Flux<LiquidationBatchEntity> findAllByLiquidationDate(LocalDate liquidationDate);

    Flux<LiquidationBatchEntity> findAllByLiquidationDateGreaterThanEqual(LocalDate fromDate);

    @Query("UPDATE TLIQ_LOTE_LIQUIDACION SET LIQ_NM_CANTIDAD_PARTICIPANTES = $2, LIQ_FE_ACTUALIZACION_LOTE = $3 WHERE LIQ_ID_LOTE_LIQUIDACION_PK = $1")
    Mono<Integer> updateParticipantCount(Long batchId, Integer count, LocalDateTime updatedAt);

    @Query("UPDATE TLIQ_LOTE_LIQUIDACION SET LIQ_NM_CANTIDAD_PARTICIPANTES = COALESCE(LIQ_NM_CANTIDAD_PARTICIPANTES, 0) + $2, LIQ_FE_ACTUALIZACION_LOTE = $3 WHERE LIQ_ID_LOTE_LIQUIDACION_PK = $1")
    Mono<Integer> incrementParticipantCount(Long batchId, Integer delta, LocalDateTime updatedAt);

    @Query("UPDATE TLIQ_LOTE_LIQUIDACION SET LIQ_NM_CANTIDAD_PARTICIPANTES = $2, LIQ_NM_VALOR_BRUTO_PARTICIPANTES_LOCAL = $3, LIQ_NM_VALOR_DEDUCCIONES_PARTICIPANTES_LOCAL = $4, LIQ_NM_VALOR_NETO_PARTICIPANTES_LOCAL = $5, LIQ_NM_CANTIDAD_PAGOS = COALESCE(LIQ_NM_CANTIDAD_TERCEROS, 0) + COALESCE($2, 0), LIQ_FE_ACTUALIZACION_LOTE = $6 WHERE LIQ_ID_LOTE_LIQUIDACION_PK = $1")
    Mono<Integer> updateParticipantTotals(Long batchId, Integer count, Long gross, Long deductions, Long net, LocalDateTime updatedAt);

    @Query("UPDATE TLIQ_LOTE_LIQUIDACION SET LIQ_NM_CANTIDAD_TERCEROS = $2, LIQ_NM_VALOR_BRUTO_TOTAL_TERCEROS_LOCAL = $3, LIQ_NM_VALOR_DEDUCCIONES_TOTAL_TERCEROS_LOCAL = $4, LIQ_NM_VALOR_NETO_TOTAL_TERCEROS_LOCAL = $5, LIQ_NM_CANTIDAD_PAGOS = COALESCE($2, 0) + COALESCE(LIQ_NM_CANTIDAD_PARTICIPANTES, 0), LIQ_FE_ACTUALIZACION_LOTE = $6 WHERE LIQ_ID_LOTE_LIQUIDACION_PK = $1")
    Mono<Integer> updateThirdPartyTotals(Long batchId, Integer count, Long gross, Long deductions, Long net, LocalDateTime updatedAt);

    @Query("UPDATE TLIQ_LOTE_LIQUIDACION SET LIQ_NM_VALOR_BRUTO_TOTAL_LOCAL = COALESCE(LIQ_NM_VALOR_BRUTO_PARTICIPANTES_LOCAL, 0) + COALESCE(LIQ_NM_VALOR_BRUTO_TOTAL_TERCEROS_LOCAL, 0), LIQ_NM_VALOR_DEDUCCIONES_TOTAL_LOCAL = COALESCE(LIQ_NM_VALOR_DEDUCCIONES_PARTICIPANTES_LOCAL, 0) + COALESCE(LIQ_NM_VALOR_DEDUCCIONES_TOTAL_TERCEROS_LOCAL, 0), LIQ_NM_VALOR_NETO_TOTAL_LOCAL = (COALESCE(LIQ_NM_VALOR_BRUTO_PARTICIPANTES_LOCAL, 0) + COALESCE(LIQ_NM_VALOR_BRUTO_TOTAL_TERCEROS_LOCAL, 0)) - (COALESCE(LIQ_NM_VALOR_DEDUCCIONES_PARTICIPANTES_LOCAL, 0) + COALESCE(LIQ_NM_VALOR_DEDUCCIONES_TOTAL_TERCEROS_LOCAL, 0)), LIQ_FE_ACTUALIZACION_LOTE = $2 WHERE LIQ_ID_LOTE_LIQUIDACION_PK = $1")
    Mono<Integer> recomputeBatchTotals(Long batchId, LocalDateTime updatedAt);
}
