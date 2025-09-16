package co.com.asulado.r2dbc.entity.liquidation;

import co.com.asulado.model.constants.DatabaseConstants;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
@Table(DatabaseConstants.TABLE_TLIQ_BATCH_LIQUIDATION)
public class LiquidationBatchEntity {

    @Id
    @Column("LIQ_ID_LOTE_LIQUIDACION_PK")
    private Long batchId;

    @Column("LIQ_FE_LIQUIDACION")
    private LocalDate liquidationDate;

    @Column("LIQ_FE_DISPERSION")
    private LocalDate dispersionDate;

    @Column("LIQ_DS_PERIODO_LIQUIDACION")
    private String liquidationPeriod;

    @Column("LIQ_DS_ESTADO")
    private String status;

    @Column("LIQ_NM_CANTIDAD_PARTICIPANTES")
    private Integer participantCount;

    @Column("LIQ_NM_CANTIDAD_TERCEROS")
    private Integer thirdPartyCount;

    @Column("LIQ_NM_CANTIDAD_PAGOS")
    private Integer paymentsCount;

    @Column("LIQ_NM_VALOR_BRUTO_TOTAL_LOCAL")
    private Long totalGrossAmountCop;

    @Column("LIQ_NM_VALOR_DEDUCCIONES_TOTAL_LOCAL")
    private Long totalDeductionAmountCop;

    @Column("LIQ_NM_VALOR_NETO_TOTAL_LOCAL")
    private Long totalNetAmountCop;

    @Column("LIQ_NM_VALOR_BRUTO_TOTAL_TERCEROS_LOCAL")
    private Long totalThirdPartyGrossAmountCop;

    @Column("LIQ_NM_VALOR_DEDUCCIONES_TOTAL_TERCEROS_LOCAL")
    private Long totalThirdPartyDeductionAmountCop;

    @Column("LIQ_NM_VALOR_NETO_TOTAL_TERCEROS_LOCAL")
    private Long totalThirdPartyNetAmountCop;

    @Column("LIQ_DS_RAZON_LOTE")
    private String batchReason;

    @Column("LIQ_FE_CREACION_LOTE")
    private LocalDateTime createdAt;

    @Column("LIQ_FE_ACTUALIZACION_LOTE")
    private LocalDateTime updatedAt;
}
