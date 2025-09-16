package co.com.asulado.r2dbc.mapper;

import co.com.asulado.model.liquidation.LiquidationBatch;
import co.com.asulado.r2dbc.entity.liquidation.LiquidationBatchEntity;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface LiquidationBatchEntityMapper {

    LiquidationBatchEntity toEntity(LiquidationBatch liquidationBatch);

    LiquidationBatch toDomain(LiquidationBatchEntity liquidationBatchEntity);
}
