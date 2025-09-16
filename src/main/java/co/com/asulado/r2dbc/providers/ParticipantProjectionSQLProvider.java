package co.com.asulado.r2dbc.providers;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class ParticipantProjectionSQLProvider {

    private static final String INNER_JOIN = "INNER JOIN ";
    private static final String LEFT_JOIN = "LEFT JOIN ";

    @Value("${adapters.r2dbc.other-schemas.payments}")
    private String schemaPayments;

    @Value("${adapters.r2dbc.schema}")
    private String schemaLiquidation;

    public String getParticipantProjectionExcludingLiquidatedQuery() {
        StringBuilder sb = new StringBuilder();
        sb.append("SELECT\n");
        sb.append("  pp.PAG_ID_PARTICIPANTE_PAGO_PK as participant_id,\n");
        sb.append("  pp.PAG_FE_INICIO as payment_date,\n");
        sb.append("  pp.PAG_ID_SOLICITUD_PAGO_FK as payment_request_id,\n");
        sb.append("  pp.PAG_ID_PARTICIPANTE_FK as person_id,\n");
        sb.append("  pr.PAG_DS_NOMBRE as product_name,\n");
        sb.append("  pr.PAG_DS_FRECUENCIA as product_frecuency\n");
        sb.append("FROM ").append(schemaPayments).append(".TPAG_PARTICIPANTE_PAGO pp\n");
        sb.append(INNER_JOIN).append(schemaPayments).append(".TPAG_SOLICITUD_PAGO sp\n");
        sb.append("  ON pp.PAG_ID_SOLICITUD_PAGO_FK = sp.PAG_ID_SOLICITUD_PAGO_PK\n");
        sb.append(INNER_JOIN).append(schemaPayments).append(".TPAG_PRODUCTO pr\n");
        sb.append("  ON sp.PAG_ID_PRODUCTO_FK = pr.PAG_ID_PRODUCTO_PK\n");
        sb.append("WHERE NOT EXISTS (\n");
        sb.append("  SELECT 1\n");
        sb.append("  FROM ").append(schemaLiquidation).append(".TLIQ_PARTICIPANTE lp\n");
        sb.append("  WHERE lp.LIQ_ID_PARTICIPANTE_PAGO = pp.PAG_ID_PARTICIPANTE_PAGO_PK\n");
        sb.append(")");
        return sb.toString();
    }

    public String getParticipantsDeductionAndIncomeQuery(String batchIds) {
        StringBuilder sb = new StringBuilder();
        sb.append("SELECT\n");
        sb.append("  participant.liq_id_participante_pk AS participant_id,\n");
        sb.append("  participant.liq_ds_estado_participante AS participant_status,\n");
        sb.append("  participant.liq_ds_producto AS product_description,\n");
        sb.append("  income.liq_id_ingreso_pk AS income_id,\n");
        sb.append("  income.liq_ds_tipo AS income_type,\n");
        sb.append("  income.liq_ds_frecuencia AS income_frequency,\n");
        sb.append("  income.liq_nm_cantidad_pagos AS number_of_payments,\n");
        sb.append("  income.liq_nm_numero_pago AS payment_number,\n");
        sb.append("  income.liq_ds_tipo_participacion AS participation_type,\n");
        sb.append("  income.liq_nm_porcentaje_participacion AS participation_percentage,\n");
        sb.append("  income.liq_nm_valor_bruto_participacion AS gross_participation_value,\n");
        sb.append("  income.liq_ds_moneda AS currency,\n");
        sb.append("  income.liq_nm_tasa_cambio AS exchange_rate,\n");
        sb.append("  income.liq_nm_valor_base_participacion AS base_participation_value,\n");
        sb.append("  person.liq_id_persona_pk AS person_id,\n");
        sb.append("  person.liq_ds_tipo_documento AS document_type,\n");
        sb.append("  person.liq_nm_numero_documento AS document_number,\n");
        sb.append("  person.liq_fe_nacimiento AS birth_date,\n");
        sb.append("  person.liq_sn_residente_exterior AS is_foreign_resident,\n");
        sb.append("  person.liq_sn_es_activo AS is_person_active,\n");
        sb.append("  deduction.liq_id_deduccion_pk AS deduction_id,\n");
        sb.append("  deduction.liq_sn_es_adicional AS deduction_is_additional,\n");
        sb.append("  deduction.liq_fe_inicio AS deduction_start_date,\n");
        sb.append("  deduction.liq_fe_fin AS deduction_end_date,\n");
        sb.append("  deduction.liq_ds_estado_deduccion AS deduction_status,\n");
        sb.append("  deduction.liq_ds_tipo AS deduction_type,\n");
        sb.append("  deduction.liq_ds_tipo_deduccion AS deduction_participant_type,\n");
        sb.append("  deduction.liq_nm_porcentaje AS deduction_percentage,\n");
        sb.append("  deduction.liq_nm_valor AS deduction_value,\n");
        sb.append("  deduction.liq_nm_valor_origen AS deduction_origin_value,\n");
        sb.append("  deduction.liq_ds_moneda AS deduction_currency,\n");
        sb.append("  deduction.liq_nm_tasa_cambio AS deduction_exchange_rate,\n");
        sb.append("  deduction.liq_nm_cantidad_pagos AS deduction_number_of_payments\n");
        sb.append("FROM ").append(schemaLiquidation).append(".tliq_participante participant\n");
        sb.append(INNER_JOIN).append(schemaLiquidation).append(".tliq_persona person\n");
        sb.append("  ON participant.liq_id_persona_fk = person.liq_id_persona_pk\n");
        sb.append(LEFT_JOIN).append(schemaLiquidation).append(".tliq_ingreso income\n");
        sb.append("  ON participant.liq_id_participante_pk = income.liq_id_participante_fk\n");
        sb.append(LEFT_JOIN).append(schemaLiquidation).append(".tliq_deduccion deduction\n");
        sb.append("  ON participant.liq_id_participante_pk = deduction.liq_id_participante_fk\n");
        sb.append("WHERE participant.liq_id_lote_liquidacion_fk IN (").append(batchIds).append(")");
        return sb.toString();
    }

    public String updateIncomeParticipant() {
        return """
            UPDATE %s.tliq_ingreso
               SET liq_nm_porcentaje_participacion   = $1,
                   liq_nm_valor_bruto_participacion  = $2,
                   liq_nm_valor_origen               = $3,
                   liq_nm_valor_local                = $4
             WHERE liq_id_ingreso_pk = $5
          """.formatted(schemaLiquidation);
    }
}
