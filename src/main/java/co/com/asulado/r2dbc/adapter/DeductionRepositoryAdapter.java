package co.com.asulado.r2dbc.adapter;

import co.com.asulado.model.deduction.Deduction;
import co.com.asulado.model.deduction.gateways.DeductionRepository;
import co.com.asulado.r2dbc.mapper.DeductionEntityMapper;
import co.com.asulado.r2dbc.repository.DeductionDataRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository
@RequiredArgsConstructor
public class DeductionRepositoryAdapter implements DeductionRepository {

    private final DeductionDataRepository deductionRepository;
    private final DeductionEntityMapper mapper;

    @Override
    public Flux<Deduction> saveAll(Iterable<Deduction> deductions) {
        return Flux.fromIterable(deductions)
                .flatMap(d -> updateDeductionFields(
                        d.getDeductionId(),
                        d.getPercentage(),
                        d.getLocalValue(),
                        d.getOriginValue(),
                        d.getValue()
                ));
    }

    private Mono<Deduction> updateDeductionFields(Long deductionId, Double percentage, Long valueInLocalPerPayment, Long valueInOriginPerPayment, Long valueInOrigin) {
        return deductionRepository.findById(deductionId)
                .flatMap(entity -> {
                    entity.setPercentage(percentage);
                    entity.setLocalValue(valueInLocalPerPayment);
                    entity.setValue(valueInOrigin);
                    entity.setOriginValue(valueInOriginPerPayment);
                    return deductionRepository.save(entity);
                })
                .map(mapper::toDomain);
    }
}
