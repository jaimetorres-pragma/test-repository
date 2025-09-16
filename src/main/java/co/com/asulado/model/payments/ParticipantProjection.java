package co.com.asulado.model.payments;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@AllArgsConstructor
@Data
@Builder
@NoArgsConstructor
public class ParticipantProjection {
    private String participantId;
    private Long personId;
    private String productName;
    private String paymentRequestId;
    private LocalDate paymentDate;
    private String productFrequency;
}
