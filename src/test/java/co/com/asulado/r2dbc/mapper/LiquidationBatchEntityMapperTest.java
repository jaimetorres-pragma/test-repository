package co.com.asulado.r2dbc.mapper;

import co.com.asulado.model.liquidation.LiquidationBatch;
import co.com.asulado.r2dbc.entity.liquidation.LiquidationBatchEntity;
import co.com.asulado.r2dbc.testdata.LiquidationBatchEntityMapperTestData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@DisplayName("Pruebas de LiquidationBatchEntityMapper")
class LiquidationBatchEntityMapperTest {

    private LiquidationBatchEntityMapper mapper;

    @BeforeEach
    void setUp() {
        mapper = Mappers.getMapper(LiquidationBatchEntityMapper.class);
    }

    @Test
    @DisplayName("Debe mapear LiquidationBatch a LiquidationBatchEntity correctamente")
    void shouldMapDomainToEntityCorrectly() {
        // Given
        LiquidationBatch domain = LiquidationBatchEntityMapperTestData.createDomainBatch1();

        // When
        LiquidationBatchEntity entity = mapper.toEntity(domain);

        // Then
        assertNotNull(entity);
        assertEquals(domain.getBatchId(), entity.getBatchId());
        assertEquals(domain.getLiquidationDate(), entity.getLiquidationDate());
        assertEquals(domain.getDispersionDate(), entity.getDispersionDate());
        assertEquals(domain.getLiquidationPeriod(), entity.getLiquidationPeriod());
        assertEquals(domain.getStatus(), entity.getStatus());
        assertEquals(domain.getParticipantCount(), entity.getParticipantCount());
        assertEquals(domain.getThirdPartyCount(), entity.getThirdPartyCount());
        assertEquals(domain.getTotalGrossAmountCop(), entity.getTotalGrossAmountCop());
        assertEquals(domain.getTotalDeductionAmountCop(), entity.getTotalDeductionAmountCop());
        assertEquals(domain.getTotalNetAmountCop(), entity.getTotalNetAmountCop());
    }

    @Test
    @DisplayName("Debe mapear LiquidationBatchEntity a LiquidationBatch correctamente")
    void shouldMapEntityToDomainCorrectly() {
        // Given
        LiquidationBatchEntity entity = LiquidationBatchEntityMapperTestData.createEntityBatch2();

        // When
        LiquidationBatch domain = mapper.toDomain(entity);

        // Then
        assertNotNull(domain);
        assertEquals(entity.getBatchId(), domain.getBatchId());
        assertEquals(entity.getLiquidationDate(), domain.getLiquidationDate());
        assertEquals(entity.getDispersionDate(), domain.getDispersionDate());
        assertEquals(entity.getLiquidationPeriod(), domain.getLiquidationPeriod());
        assertEquals(entity.getStatus(), domain.getStatus());
        assertEquals(entity.getParticipantCount(), domain.getParticipantCount());
        assertEquals(entity.getThirdPartyCount(), domain.getThirdPartyCount());
        assertEquals(entity.getTotalGrossAmountCop(), domain.getTotalGrossAmountCop());
        assertEquals(entity.getTotalDeductionAmountCop(), domain.getTotalDeductionAmountCop());
        assertEquals(entity.getTotalNetAmountCop(), domain.getTotalNetAmountCop());
    }

    @Test
    @DisplayName("Debe manejar valores nulos correctamente")
    void shouldHandleNullValuesGracefully() {
        // Given
        LiquidationBatch domainWithNulls = LiquidationBatchEntityMapperTestData.createDomainWithNulls();

        // When
        LiquidationBatchEntity entity = mapper.toEntity(domainWithNulls);

        // Then
        assertNotNull(entity);
        assertEquals(domainWithNulls.getBatchId(), entity.getBatchId());
        assertEquals(domainWithNulls.getLiquidationDate(), entity.getLiquidationDate());
        assertEquals(domainWithNulls.getDispersionDate(), entity.getDispersionDate());
        assertEquals(domainWithNulls.getLiquidationPeriod(), entity.getLiquidationPeriod());
        assertEquals(domainWithNulls.getStatus(), entity.getStatus());
        assertEquals(domainWithNulls.getParticipantCount(), entity.getParticipantCount());
        assertEquals(domainWithNulls.getThirdPartyCount(), entity.getThirdPartyCount());
        assertEquals(domainWithNulls.getTotalGrossAmountCop(), entity.getTotalGrossAmountCop());
        assertEquals(domainWithNulls.getTotalDeductionAmountCop(), entity.getTotalDeductionAmountCop());
        assertEquals(domainWithNulls.getTotalNetAmountCop(), entity.getTotalNetAmountCop());
    }

    @Test
    @DisplayName("Debe mantener integridad de datos en conversión de ida y vuelta")
    void shouldMaintainDataIntegrityInRoundTripConversion() {
        // Given
        LiquidationBatch originalDomain = LiquidationBatchEntityMapperTestData.createOriginalDomainForRoundTrip();

        // When
        LiquidationBatchEntity entity = mapper.toEntity(originalDomain);
        LiquidationBatch resultDomain = mapper.toDomain(entity);

        // Then
        assertNotNull(resultDomain);
        assertEquals(originalDomain.getBatchId(), resultDomain.getBatchId());
        assertEquals(originalDomain.getLiquidationDate(), resultDomain.getLiquidationDate());
        assertEquals(originalDomain.getDispersionDate(), resultDomain.getDispersionDate());
        assertEquals(originalDomain.getLiquidationPeriod(), resultDomain.getLiquidationPeriod());
        assertEquals(originalDomain.getStatus(), resultDomain.getStatus());
        assertEquals(originalDomain.getParticipantCount(), resultDomain.getParticipantCount());
        assertEquals(originalDomain.getThirdPartyCount(), resultDomain.getThirdPartyCount());
        assertEquals(originalDomain.getTotalGrossAmountCop(), resultDomain.getTotalGrossAmountCop());
        assertEquals(originalDomain.getTotalDeductionAmountCop(), resultDomain.getTotalDeductionAmountCop());
        assertEquals(originalDomain.getTotalNetAmountCop(), resultDomain.getTotalNetAmountCop());
    }
}
