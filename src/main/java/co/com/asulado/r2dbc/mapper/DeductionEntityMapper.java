package co.com.asulado.r2dbc.mapper;

import co.com.asulado.model.deduction.Deduction;
import co.com.asulado.r2dbc.entity.liquidation.DeductionEntity;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface DeductionEntityMapper {
    Deduction toDomain(DeductionEntity deductionEntity);
}
