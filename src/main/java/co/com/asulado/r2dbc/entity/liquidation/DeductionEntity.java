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

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
@Table(DatabaseConstants.TABLE_TLIQ_DEDUCTION)
public class DeductionEntity {

    @Id
    @Column("LIQ_ID_DEDUCCION_PK")
    private Long deductionId;

    @Column("LIQ_ID_PARTICIPANTE_FK")
    private Long participantId;

    @Column("LIQ_SN_ES_ADICIONAL")
    private Boolean isAdditional;

    @Column("LIQ_DS_TIPO")
    private String type;

    @Column("LIQ_DS_TIPO_DEDUCCION")
    private String participantType;

    @Column("LIQ_NM_PORCENTAJE")
    private Double percentage;

    @Column("LIQ_NM_VALOR")
    private Long value;

    @Column("LIQ_NM_VALOR_ORIGEN")
    private Long originValue;

    @Column("LIQ_NM_TASA_CAMBIO")
    private Double exchangeRate;

    @Column("LIQ_NM_VALOR_LOCAL")
    private Long localValue;

    @Column("LIQ_NM_CANTIDAD_PAGOS")
    private Integer numberOfPayments;

    @Column("LIQ_DS_ESTADO_DEDUCCION")
    private String status;

    @Column("LIQ_FE_INICIO")
    private LocalDate startDate;

    @Column("LIQ_FE_FIN")
    private LocalDate endDate;
}
