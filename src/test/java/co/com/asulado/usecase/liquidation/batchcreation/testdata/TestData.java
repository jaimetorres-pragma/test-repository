package co.com.asulado.usecase.liquidation.batchcreation.testdata;

import co.com.asulado.model.payments.ParticipantProjection;
import co.com.asulado.usecase.liquidation.batchcreation.util.ProcessingContext;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public final class TestData {
    private TestData() {}

    public static ParticipantProjection participant(String id, LocalDate paymentDate) {
        return ParticipantProjection.builder()
                .participantId(id)
                .personId(1L)
                .productName("Producto")
                .paymentRequestId(UUID.randomUUID().toString())
                .paymentDate(paymentDate)
                .productFrequency("30CADAMES")
                .build();
    }

    public static Map<LocalDate, List<ParticipantProjection>> group(LocalDate paymentDate, int count) {
        List<ParticipantProjection> list = new ArrayList<>(count);
        for (int i = 0; i < count; i++) {
            list.add(participant(UUID.randomUUID().toString(), paymentDate));
        }
        return Map.of(paymentDate, list);
    }

    public static Map<LocalDate, List<ParticipantProjection>> groups(Map<LocalDate, Integer> spec) {
        Map<LocalDate, List<ParticipantProjection>> result = new HashMap<>();
        for (Map.Entry<LocalDate, Integer> e : spec.entrySet()) {
            result.putAll(group(e.getKey(), e.getValue()));
        }
        return result;
    }

    public static ProcessingContext contextWith(Map<LocalDate, List<ParticipantProjection>> grouped,
                                                Map<LocalDate, LocalDate> liquidationDates,
                                                LocalDate currentDate) {
        return ProcessingContext.builder()
                .groupedByPaymentDate(grouped)
                .liquidationDates(liquidationDates)
                .currentDate(currentDate)
                .build();
    }
}

