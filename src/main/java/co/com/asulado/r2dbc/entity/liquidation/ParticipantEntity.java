package co.com.asulado.r2dbc.entity.liquidation;

import co.com.asulado.model.constants.DatabaseConstants;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
@Table(DatabaseConstants.TABLE_TLIQ_PARTICIPANT)
public class ParticipantEntity {

    @Id
    @Column("LIQ_ID_PARTICIPANTE_PK")
    private Long participantId;

    @Column("LIQ_ID_PARTICIPANTE_PAGO")
    private UUID participantPaymentId;

    @Column("LIQ_ID_SOLICITUD_PAGO")
    private UUID paymentRequestId;

    @Column("LIQ_DS_PRODUCTO")
    private String product;

    @Column("LIQ_ID_PERSONA_FK")
    private Long personId;

    @Column("LIQ_ID_LOTE_LIQUIDACION_FK")
    private Long liquidationBatchId;

    @Column("LIQ_DS_ESTADO_PARTICIPANTE")
    private String participantPaymentStatus;

    @Column("LIQ_DS_ESTADO_LIQUIDACION")
    private String liquidationStatus;

    @Column("LIQ_NM_VALOR_BRUTO_LOCAL")
    private Long grossAmountCop;

    @Column("LIQ_NM_VALOR_DEDUCCIONES_LOCAL")
    private Long deductionAmountCop;

    @Column("LIQ_NM_VALOR_NETO_LOCAL")
    private Long netAmountCop;

}
