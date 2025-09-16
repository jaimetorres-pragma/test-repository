package co.com.asulado.r2dbc.mapper;

import co.com.asulado.model.liquidation.ThirdParty;
import co.com.asulado.r2dbc.entity.liquidation.ThirdPartyEntity;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface ThirdPartyEntityMapper {
    ThirdParty toDomain(ThirdPartyEntity entity);
}

