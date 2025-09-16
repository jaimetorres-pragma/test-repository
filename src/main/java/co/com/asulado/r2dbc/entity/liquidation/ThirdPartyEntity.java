package co.com.asulado.r2dbc.entity.liquidation;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
@Table("TLIQ_TERCERO")
public class ThirdPartyEntity {

    @Id
    @Column("LIQ_TERCERO_PK")
    private Long thirdPartyId;

    @Column("LIQ_ID_PERSONA_FK")
    private Long personId;

    @Column("LIQ_ID_LOTE_LIQUIDACION_FK")
    private Long liquidationBatchId;

    @Column("LIQ_DS_TIPO_PAGO")
    private String paymentType;

    @Column("LIQ_DS_ESTADO_TERCERO")
    private String thirdPartyStatus;

    @Column("LIQ_DS_ESTADO_LIQUIDACION")
    private String liquidationStatus;

    @Column("LIQ_NM_VALOR_BRUTO_LOCAL")
    private Long grossAmountCop;

    @Column("LIQ_NM_VALOR_DEDUCCIONES_LOCAL")
    private Long deductionAmountCop;

    @Column("LIQ_NM_VALOR_NETO_LOCAL")
    private Long netAmountCop;
}

