package co.com.asulado.model.deduction.gateways;

import co.com.asulado.model.deduction.Deduction;

import reactor.core.publisher.Flux;

public interface DeductionRepository {
    Flux<Deduction> saveAll(Iterable<Deduction> deductions);
}
