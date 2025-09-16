package co.com.asulado.usecase.liquidation.liquidatebatch;

import co.com.asulado.model.constants.Constants;
import co.com.asulado.model.constants.DeductionsConstants;
import co.com.asulado.model.deduction.Income;
import co.com.asulado.model.deduction.ParticipantDeduction;
import co.com.asulado.model.eventmessages.LiquidateBatchMessage;
import co.com.asulado.model.liquidation.gateways.ParameterRepository;
import co.com.asulado.model.liquidation.parameter.GeneralParameter;
import co.com.asulado.model.liquidation.parameter.LiquidationParameter;
import co.com.asulado.model.payments.gateways.ParticipantProjectionRepository;
import co.com.asulado.usecase.liquidation.deductions.DeductionUseCase;
import co.com.asulado.usecase.liquidation.liquidatebatch.util.gateway.LiquidationHelper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.math.BigInteger;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyMap;
import static org.mockito.Mockito.RETURNS_DEEP_STUBS;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.anyDouble;
import static org.mockito.Mockito.anyList;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.argThat;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


@ExtendWith(MockitoExtension.class)
class LiquidateBatchUseCaseTest {

    private DeductionUseCase deductionUseCase;
    private ParameterRepository parameterRepository;
    private ParticipantProjectionRepository participantProjectionRepository;
    private LiquidationHelper liquidationHelper;

    private LiquidateBatchUseCase useCase;

    @Captor private ArgumentCaptor<Double> taxableBaseCaptor;
    @Captor private ArgumentCaptor<Double> minSalaryCaptor;

    @BeforeEach
    void setUp() {
        deductionUseCase = mock(DeductionUseCase.class);
        parameterRepository = mock(ParameterRepository.class);
        participantProjectionRepository = mock(ParticipantProjectionRepository.class);
        liquidationHelper = mock(LiquidationHelper.class);

        useCase = new LiquidateBatchUseCase(
                deductionUseCase, parameterRepository, participantProjectionRepository, liquidationHelper);
    }

    @Test
    void liquidateBatch_happyPath_typeVALUE_usesLocalValueAsTaxableBase_updatesAll() {
        String traceId = "t-123";
        BigInteger batchId = BigInteger.valueOf(1L);

        LiquidateBatchMessage msg = LiquidateBatchMessage.builder()
                .batchId(batchId)
                .build();

        LiquidationParameter liqParam = mock(LiquidationParameter.class, RETURNS_DEEP_STUBS);
        when(liqParam.getTaxableBase().getFormula())
                .thenReturn("originValue * exchangeRate / numberOfPayments");

        GeneralParameter genParam = GeneralParameter.builder()
                .minimumSalary(1300)
                .build();

        when(parameterRepository.getParameters(Constants.LIQUIDATION_PARAMETER_KEY, traceId))
                .thenReturn(Mono.just(liqParam));
        when(parameterRepository.getParameters(Constants.GENERAL_PARAMETER_KEY, traceId))
                .thenReturn(Mono.just(genParam));

        Income incomeMesa = mock(Income.class);
        when(incomeMesa.getParticipationType()).thenReturn(DeductionsConstants.TYPE_VALUE);
        when(incomeMesa.getGrossParticipationValue()).thenReturn(1000L);
        when(incomeMesa.getBaseParticipationValue()).thenReturn(2000L);
        when(incomeMesa.getExchangeRate()).thenReturn(3.7);
        when(incomeMesa.getNumberOfPayments()).thenReturn(2);
        when(incomeMesa.getType()).thenReturn(DeductionsConstants.MESADA_PENSIONAL);

        AtomicReference<Double> localRef = new AtomicReference<>(0.0);
        doAnswer(inv -> { localRef.set(inv.getArgument(0)); return null; })
                .when(incomeMesa).setLocalValue(anyDouble());
        when(incomeMesa.getLocalValue()).thenAnswer(inv -> localRef.get());

        when(liquidationHelper.evaluateFormula(anyString(), anyMap()))
                .thenReturn(1850.0);

        ParticipantDeduction pd = mock(ParticipantDeduction.class);
        when(pd.getIncomes()).thenReturn(List.of(incomeMesa));

        when(participantProjectionRepository.findAllParticipantsDeductions(batchId.toString()))
                .thenReturn(Flux.just(pd));

        when(participantProjectionRepository.updateAllParticipantsDeductions(anyList()))
                .thenReturn(Mono.empty());

        Mono<Void> mono = useCase.liquidateBatch(msg, traceId);

        StepVerifier.create(mono).verifyComplete();

        verify(incomeMesa).setOriginValue(1000L);
        verify(incomeMesa).setParticipationPercentage(1000.0 / 2000.0);

        verify(liquidationHelper).evaluateFormula(anyString(), anyMap());
        assertEquals(1850.0, incomeMesa.getLocalValue(), 1e-9);

        verify(deductionUseCase).getTotalDeductions(
                eq(pd), eq(liqParam), taxableBaseCaptor.capture(), minSalaryCaptor.capture(),eq(traceId));

        assertEquals(1850.0, taxableBaseCaptor.getValue(), 1e-9);
        assertEquals(1300.0, minSalaryCaptor.getValue(), 1e-9);

        verify(participantProjectionRepository).updateAllParticipantsDeductions(argThat(list -> list.size() == 1 && list.get(0) == pd));
    }

    @Test
    void liquidateBatch_updatesPercentageBranch_typePERCENTAGE() {
        String traceId = "t-456";
        BigInteger batchId = BigInteger.valueOf(1L);

        LiquidateBatchMessage msg = LiquidateBatchMessage.builder()
                .batchId(batchId)
                .build();

        LiquidationParameter liqParam = mock(LiquidationParameter.class, RETURNS_DEEP_STUBS);
        when(liqParam.getTaxableBase().getFormula()).thenReturn("any");

        GeneralParameter genParam = GeneralParameter.builder()
                .minimumSalary(1300)
                .build();

        when(parameterRepository.getParameters(Constants.LIQUIDATION_PARAMETER_KEY, traceId))
                .thenReturn(Mono.just(liqParam));
        when(parameterRepository.getParameters(Constants.GENERAL_PARAMETER_KEY, traceId))
                .thenReturn(Mono.just(genParam));

        Income incomeMesa = mock(Income.class);
        when(incomeMesa.getParticipationType()).thenReturn(DeductionsConstants.TYPE_PERCENTAGE);

        when(incomeMesa.getBaseParticipationValue()).thenReturn(2000L);
        when(incomeMesa.getParticipationPercentage()).thenReturn(50.0);
        when(incomeMesa.getType()).thenReturn(DeductionsConstants.MESADA_PENSIONAL);

        AtomicReference<Double> originRef = new AtomicReference<>(0.0);
        doAnswer(inv -> { originRef.set(inv.getArgument(0)); return null; })
                .when(incomeMesa).setOriginValue(anyDouble());
        when(incomeMesa.getOriginValue()).thenAnswer(inv -> originRef.get());

        AtomicReference<Double> localRef = new AtomicReference<>(0.0);
        doAnswer(inv -> { localRef.set(inv.getArgument(0)); return null; })
                .when(incomeMesa).setLocalValue(anyDouble());
        when(incomeMesa.getLocalValue()).thenAnswer(inv -> localRef.get());

        when(liquidationHelper.evaluateFormula(anyString(), anyMap())).thenReturn(1000.0);

        ParticipantDeduction pd = mock(ParticipantDeduction.class);
        when(pd.getIncomes()).thenReturn(List.of(incomeMesa));

        when(participantProjectionRepository.findAllParticipantsDeductions(batchId.toString()))
                .thenReturn(Flux.just(pd));
        when(participantProjectionRepository.updateAllParticipantsDeductions(anyList()))
                .thenReturn(Mono.empty());

        StepVerifier.create(useCase.liquidateBatch(msg, traceId)).verifyComplete();

        verify(incomeMesa).setOriginValue(1000.0);
        verify(incomeMesa).setGrossParticipationValue(1000L);

        verify(deductionUseCase).getTotalDeductions(eq(pd), eq(liqParam),
                taxableBaseCaptor.capture(), minSalaryCaptor.capture(),eq(traceId));
        assertEquals(1000.0, taxableBaseCaptor.getValue(), 1e-9);
        assertEquals(1300.0, minSalaryCaptor.getValue(), 1e-9);
    }


    @Test
    void liquidateBatch_whenNoMesadaPensional_taxableBaseIsZero() {
        String traceId = "t-789";
        BigInteger batchId = BigInteger.valueOf(1L);

        LiquidateBatchMessage msg = LiquidateBatchMessage.builder()
                .batchId(batchId)
                .build();

        LiquidationParameter liqParam = mock(LiquidationParameter.class, RETURNS_DEEP_STUBS);
        when(liqParam.getTaxableBase().getFormula()).thenReturn("any");

        GeneralParameter genParam = GeneralParameter.builder()
                .minimumSalary(1100)
                .build();

        when(parameterRepository.getParameters(Constants.LIQUIDATION_PARAMETER_KEY, traceId))
                .thenReturn(Mono.just(liqParam));
        when(parameterRepository.getParameters(Constants.GENERAL_PARAMETER_KEY, traceId))
                .thenReturn(Mono.just(genParam));

        Income otherIncome = mock(Income.class);
        when(otherIncome.getParticipationType()).thenReturn(DeductionsConstants.TYPE_VALUE);
        when(otherIncome.getGrossParticipationValue()).thenReturn(10L);
        when(otherIncome.getBaseParticipationValue()).thenReturn(100L);
        when(otherIncome.getType()).thenReturn("OTRO_TIPO");

        when(liquidationHelper.evaluateFormula(anyString(), any(Map.class))).thenReturn(5.0);

        ParticipantDeduction pd = mock(ParticipantDeduction.class);
        when(pd.getIncomes()).thenReturn(List.of(otherIncome));

        when(participantProjectionRepository.findAllParticipantsDeductions(batchId.toString()))
                .thenReturn(Flux.just(pd));
        when(participantProjectionRepository.updateAllParticipantsDeductions(anyList()))
                .thenReturn(Mono.empty());

        StepVerifier.create(useCase.liquidateBatch(msg, traceId)).verifyComplete();

        verify(deductionUseCase).getTotalDeductions(eq(pd), eq(liqParam), taxableBaseCaptor.capture(), minSalaryCaptor.capture(),eq(traceId));
        assertEquals(0.0, taxableBaseCaptor.getValue(), 1e-9);
        assertEquals(1100.0, minSalaryCaptor.getValue(), 1e-9);
    }
}