package co.com.asulado.r2dbc.adapter;


import co.com.asulado.model.constants.DatabaseConstants;
import co.com.asulado.model.deduction.Income;
import co.com.asulado.model.deduction.ParticipantDeduction;
import co.com.asulado.model.payments.ParticipantProjection;
import co.com.asulado.model.payments.gateways.ParticipantProjectionRepository;
import co.com.asulado.r2dbc.entity.row.ParticipantDeductionRow;
import co.com.asulado.r2dbc.mapper.ParticipantsProjectionsMapper;
import co.com.asulado.r2dbc.providers.ParticipantProjectionSQLProvider;
import io.r2dbc.spi.Row;
import io.r2dbc.spi.RowMetadata;
import io.r2dbc.spi.Statement;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.reactive.TransactionalOperator;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Repository
@RequiredArgsConstructor
@Slf4j
public class ParticipantProjectionRepositoryAdapter implements ParticipantProjectionRepository {

    private final DatabaseClient databaseClient;
    private final ParticipantProjectionSQLProvider sqlProvider;
    private final ParticipantsProjectionsMapper mapper;
    private final TransactionalOperator tx;
    @Value("${adapters.r2dbc.chunks}") private int chunk;

    @Override
    public Flux<ParticipantProjection> findAllParticipantProjectionsExcludingExistingInLiquidationBatch() {
        String sql = sqlProvider.getParticipantProjectionExcludingLiquidatedQuery();
        return databaseClient.sql(sql)
                .map(this::mapRowToParticipantProjection)
                .all();
    }
    private ParticipantProjection mapRowToParticipantProjection(Row row, RowMetadata rowMetadata) {
        return ParticipantProjection.builder()
                .participantId(row.get(DatabaseConstants.COLUMN_PARTICIPANT_ID, String.class))
                .paymentDate(row.get(DatabaseConstants.COLUMN_PAYMENT_DATE, LocalDate.class))
                .productFrequency(row.get(DatabaseConstants.COLUMN_PRODUCT_FREQUENCY, String.class))
                .paymentRequestId(row.get(DatabaseConstants.COLUMN_PAYMENT_REQUEST_ID, String.class))
                .personId(row.get(DatabaseConstants.COLUMN_PERSON_ID, Long.class))
                .productName(row.get(DatabaseConstants.COLUMN_PRODUCT_NAME, String.class))
                .build();
    }

    @Override
     public Flux<ParticipantDeduction> findAllParticipantsDeductions(String participantsIds) {
        String sql = sqlProvider.getParticipantsDeductionAndIncomeQuery(participantsIds);
         return databaseClient.sql(sql)
                 .map(mapper::mapRowToParticipantDeductionProjection)
                 .all()
                 .groupBy(ParticipantDeductionRow::getParticipantId)
                 .flatMap(group -> group
                         .collectList()
                         .map(mapper::toDomain)
                 );
    }

    @Override
    public Mono<Void> updateAllParticipantsDeductions(List<ParticipantDeduction> participantDeductions) {
        return Flux.fromIterable(participantDeductions)
                .buffer(chunk)
                .concatMap(batch ->
                        databaseClient.inConnectionMany(conn ->
                                        mapper.executeParticipantDeduction(batch, conn,
                                                sqlProvider.updateIncomeParticipant()))
                                .reduce(0L, Long::sum)
                                .as(tx::transactional)
                )
                .reduce(0L, Long::sum)
                .doOnSuccess(totalUpdated -> log.info("Total rows updated: {}", totalUpdated))
                .then();
    }
}
