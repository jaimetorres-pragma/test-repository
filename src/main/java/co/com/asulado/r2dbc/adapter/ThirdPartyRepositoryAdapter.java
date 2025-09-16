package co.com.asulado.r2dbc.adapter;

import co.com.asulado.model.liquidation.ThirdParty;
import co.com.asulado.model.liquidation.gateways.ThirdPartyRepository;
import co.com.asulado.r2dbc.mapper.ThirdPartyEntityMapper;
import co.com.asulado.r2dbc.repository.ThirdPartyDataRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;

@Repository
@RequiredArgsConstructor
public class ThirdPartyRepositoryAdapter implements ThirdPartyRepository {

    private final ThirdPartyDataRepository repository;
    private final ThirdPartyEntityMapper mapper;

    @Override
    public Flux<ThirdParty> findAllByBatchId(Long batchId) {
        return repository.findAllByLiquidationBatchId(batchId)
                .map(mapper::toDomain);
    }
}

