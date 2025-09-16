package co.com.asulado.r2dbc.mapper;

import co.com.asulado.model.liquidation.Participant;
import co.com.asulado.r2dbc.entity.liquidation.ParticipantEntity;
import co.com.asulado.r2dbc.testdata.ParticipantEntityMapperTestData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@DisplayName("Pruebas de BatchParticipantDetailEntityMapper")
class ParticipantEntityMapperTest {

    private BatchParticipantDetailEntityMapper mapper;

    @BeforeEach
    void setUp() {
        mapper = Mappers.getMapper(BatchParticipantDetailEntityMapper.class);
    }

    @Test
    @DisplayName("Debe mapear BatchParticipantDetail a BatchParticipantDetailEntity correctamente")
    void shouldMapDomainToEntityCorrectly() {
        // Given
        Participant domain = ParticipantEntityMapperTestData.createDomainParticipant();

        // When
        ParticipantEntity entity = mapper.toEntity(domain);

        // Then
        assertNotNull(entity);
        assertEquals(domain.getParticipantId(), entity.getParticipantId());
        assertEquals(domain.getParticipantPaymentId(), entity.getParticipantPaymentId());
        assertEquals(domain.getLiquidationBatchId(), entity.getLiquidationBatchId());
        assertEquals(domain.getParticipantPaymentStatus(), entity.getParticipantPaymentStatus());
        assertEquals(domain.getGrossAmountCop(), entity.getGrossAmountCop());
        assertEquals(domain.getDeductionAmountCop(), entity.getDeductionAmountCop());
        assertEquals(domain.getNetAmountCop(), entity.getNetAmountCop());
    }

    @Test
    @DisplayName("Debe mapear BatchParticipantDetailEntity a BatchParticipantDetail correctamente")
    void shouldMapEntityToDomainCorrectly() {
        // Given
        ParticipantEntity entity = ParticipantEntityMapperTestData.createEntityParticipant();

        // When
        Participant domain = mapper.toDomain(entity);

        // Then
        assertNotNull(domain);
        assertEquals(entity.getParticipantId(), domain.getParticipantId());
        assertEquals(entity.getParticipantPaymentId(), domain.getParticipantPaymentId());
        assertEquals(entity.getLiquidationBatchId(), domain.getLiquidationBatchId());
        assertEquals(entity.getParticipantPaymentStatus(), domain.getParticipantPaymentStatus());
        assertEquals(entity.getGrossAmountCop(), domain.getGrossAmountCop());
        assertEquals(entity.getDeductionAmountCop(), domain.getDeductionAmountCop());
        assertEquals(entity.getNetAmountCop(), domain.getNetAmountCop());
    }

    @Test
    @DisplayName("Debe manejar valores nulos correctamente")
    void shouldHandleNullValuesGracefully() {
        // Given
        Participant domainWithNulls = ParticipantEntityMapperTestData.createDomainWithNulls();

        // When
        ParticipantEntity entity = mapper.toEntity(domainWithNulls);

        // Then
        assertNotNull(entity);
        assertEquals(domainWithNulls.getParticipantId(), entity.getParticipantId());
        assertEquals(domainWithNulls.getParticipantPaymentId(), entity.getParticipantPaymentId());
        assertEquals(domainWithNulls.getLiquidationBatchId(), entity.getLiquidationBatchId());
        assertEquals(domainWithNulls.getParticipantPaymentStatus(), entity.getParticipantPaymentStatus());
        assertEquals(domainWithNulls.getGrossAmountCop(), entity.getGrossAmountCop());
        assertEquals(domainWithNulls.getDeductionAmountCop(), entity.getDeductionAmountCop());
        assertEquals(domainWithNulls.getNetAmountCop(), entity.getNetAmountCop());
    }

    @Test
    @DisplayName("Debe mantener la integridad de los datos en la conversión de ida y vuelta")
    void shouldMaintainDataIntegrityInRoundTripConversion() {
        // Given
        Participant originalDomain = ParticipantEntityMapperTestData.createOriginalDomain();

        // When
        ParticipantEntity entity = mapper.toEntity(originalDomain);
        Participant resultDomain = mapper.toDomain(entity);

        // Then
        assertNotNull(resultDomain);
        assertEquals(originalDomain.getParticipantId(), resultDomain.getParticipantId());
        assertEquals(originalDomain.getParticipantPaymentId(), resultDomain.getParticipantPaymentId());
        assertEquals(originalDomain.getLiquidationBatchId(), resultDomain.getLiquidationBatchId());
        assertEquals(originalDomain.getParticipantPaymentStatus(), resultDomain.getParticipantPaymentStatus());
        assertEquals(originalDomain.getGrossAmountCop(), resultDomain.getGrossAmountCop());
        assertEquals(originalDomain.getDeductionAmountCop(), resultDomain.getDeductionAmountCop());
        assertEquals(originalDomain.getNetAmountCop(), resultDomain.getNetAmountCop());
    }

    @Test
    @DisplayName("Debe manejar montos cero correctamente")
    void shouldHandleZeroAmountsCorrectly() {
        // Given
        Participant domainWithZeros = ParticipantEntityMapperTestData.createDomainWithZeros();

        // When
        ParticipantEntity entity = mapper.toEntity(domainWithZeros);
        Participant resultDomain = mapper.toDomain(entity);

        // Then
        assertEquals(0L, entity.getGrossAmountCop());
        assertEquals(0L, entity.getDeductionAmountCop());
        assertEquals(0L, entity.getNetAmountCop());
        assertEquals(0L, resultDomain.getGrossAmountCop());
        assertEquals(0L, resultDomain.getDeductionAmountCop());
        assertEquals(0L, resultDomain.getNetAmountCop());
    }
}
