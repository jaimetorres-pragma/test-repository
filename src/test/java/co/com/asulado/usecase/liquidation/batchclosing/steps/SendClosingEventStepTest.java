package co.com.asulado.usecase.liquidation.batchclosing.steps;

import co.com.asulado.usecase.liquidation.batchclosing.util.ClosingProcessingContext;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import reactor.test.StepVerifier;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DisplayName("Pruebas de SendClosingEventStep")
class SendClosingEventStepTest {

    @Test
    @DisplayName("Debe retornar el mismo contexto luego de enviar evento")
    void shouldReturnSameContextAfterSendingEvent() {
        var step = new SendClosingEventStep();
        var context = ClosingProcessingContext.builder().currentDate(LocalDate.now()).build();
        StepVerifier.create(step.process(context))
                .assertNext(result -> assertEquals(context, result))
                .verifyComplete();
    }
}

