package co.com.asulado.r2dbc.mapper;

import co.com.asulado.model.liquidation.Participant;
import co.com.asulado.r2dbc.entity.liquidation.ParticipantEntity;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface BatchParticipantDetailEntityMapper {

    ParticipantEntity toEntity(Participant participant);

    Participant toDomain(ParticipantEntity participantEntity);
}
