package co.com.asulado.r2dbc.providers;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ParticipantProjectionSQLProviderTest {

    @Test
    @DisplayName("Debe devolver la consulta completa cuando las propiedades de esquema están definidas")
    void shouldReturnCalendarListWhenApiResponseIsSuccessful() {
        ParticipantProjectionSQLProvider provider = new ParticipantProjectionSQLProvider();
        ReflectionTestUtils.setField(provider, "schemaPayments", "payments_schema");
        ReflectionTestUtils.setField(provider, "schemaLiquidation", "liquidation_schema");

        String expected = "SELECT\n" +
                "  pp.PAG_ID_PARTICIPANTE_PAGO_PK as participant_id,\n" +
                "  pp.PAG_FE_INICIO as payment_date,\n" +
                "  pp.PAG_ID_SOLICITUD_PAGO_FK as payment_request_id,\n" +
                "  pp.PAG_ID_PARTICIPANTE_FK as person_id,\n" +
                "  pr.PAG_DS_NOMBRE as product_name,\n" +
                "  pr.PAG_DS_FRECUENCIA as product_frecuency\n" +
                "FROM " + "payments_schema" + ".TPAG_PARTICIPANTE_PAGO pp\n" +
                "INNER JOIN " + "payments_schema" + ".TPAG_SOLICITUD_PAGO sp\n" +
                "  ON pp.PAG_ID_SOLICITUD_PAGO_FK = sp.PAG_ID_SOLICITUD_PAGO_PK\n" +
                "INNER JOIN " + "payments_schema" + ".TPAG_PRODUCTO pr\n" +
                "  ON sp.PAG_ID_PRODUCTO_FK = pr.PAG_ID_PRODUCTO_PK\n" +
                "WHERE NOT EXISTS (\n" +
                "  SELECT 1\n" +
                "  FROM " + "liquidation_schema" + ".TLIQ_PARTICIPANTE lp\n" +
                "  WHERE lp.LIQ_ID_PARTICIPANTE_PAGO = pp.PAG_ID_PARTICIPANTE_PAGO_PK\n" +
                ")";

        String actual = provider.getParticipantProjectionExcludingLiquidatedQuery();

        assertEquals(expected, actual);
    }

    @Test
    void shouldReturnGetParticipantDeductionsSuccessful() {
        ParticipantProjectionSQLProvider provider = new ParticipantProjectionSQLProvider();
        ReflectionTestUtils.setField(provider, "schemaLiquidation", "liquidation_schema");

        String expected = """
                SELECT
                  participant.liq_id_participante_pk AS participant_id,
                  participant.liq_ds_estado_participante AS participant_status,
                  participant.liq_ds_producto AS product_description,
                  income.liq_id_ingreso_pk AS income_id,
                  income.liq_ds_tipo AS income_type,
                  income.liq_ds_frecuencia AS income_frequency,
                  income.liq_nm_cantidad_pagos AS number_of_payments,
                  income.liq_nm_numero_pago AS payment_number,
                  income.liq_ds_tipo_participacion AS participation_type,
                  income.liq_nm_porcentaje_participacion AS participation_percentage,
                  income.liq_nm_valor_bruto_participacion AS gross_participation_value,
                  income.liq_ds_moneda AS currency,
                  income.liq_nm_tasa_cambio AS exchange_rate,
                  income.liq_nm_valor_base_participacion AS base_participation_value,
                  person.liq_id_persona_pk AS person_id,
                  person.liq_ds_tipo_documento AS document_type,
                  person.liq_nm_numero_documento AS document_number,
                  person.liq_fe_nacimiento AS birth_date,
                  person.liq_sn_residente_exterior AS is_foreign_resident,
                  person.liq_sn_es_activo AS is_person_active,
                  deduction.liq_id_deduccion_pk AS deduction_id,
                  deduction.liq_sn_es_adicional AS deduction_is_additional,
                  deduction.liq_fe_inicio AS deduction_start_date,
                  deduction.liq_fe_fin AS deduction_end_date,
                  deduction.liq_ds_estado_deduccion AS deduction_status,
                  deduction.liq_ds_tipo AS deduction_type,
                  deduction.liq_ds_tipo_deduccion AS deduction_participant_type,
                  deduction.liq_nm_porcentaje AS deduction_percentage,
                  deduction.liq_nm_valor AS deduction_value,
                  deduction.liq_nm_valor_origen AS deduction_origin_value,
                  deduction.liq_ds_moneda AS deduction_currency,
                  deduction.liq_nm_tasa_cambio AS deduction_exchange_rate,
                  deduction.liq_nm_cantidad_pagos AS deduction_number_of_payments
                FROM liquidation_schema.tliq_participante participant
                INNER JOIN liquidation_schema.tliq_persona person
                  ON participant.liq_id_persona_fk = person.liq_id_persona_pk
                LEFT JOIN liquidation_schema.tliq_ingreso income
                  ON participant.liq_id_participante_pk = income.liq_id_participante_fk
                LEFT JOIN liquidation_schema.tliq_deduccion deduction
                  ON participant.liq_id_participante_pk = deduction.liq_id_participante_fk
                WHERE participant.liq_id_lote_liquidacion_fk IN (1)""";

        String actual = provider.getParticipantsDeductionAndIncomeQuery("1");

        assertEquals(expected, actual);
    }


    @Test
    @DisplayName("Debe devolver la consulta completa cuando las propiedades de esquema están definidas")
    void shouldReturnUpdateParticipantDeductionsSuccessful() {
        ParticipantProjectionSQLProvider provider = new ParticipantProjectionSQLProvider();
        ReflectionTestUtils.setField(provider, "schemaLiquidation", "liquidation_schema");

        String expected = """
            UPDATE liquidation_schema.tliq_ingreso
               SET liq_nm_porcentaje_participacion   = $1,
                   liq_nm_valor_bruto_participacion  = $2,
                   liq_nm_valor_origen               = $3,
                   liq_nm_valor_local                = $4
             WHERE liq_id_ingreso_pk = $5
          """;

        String actual = provider.updateIncomeParticipant();

        assertEquals(expected, actual);
    }




}

