package co.com.asulado.usecase.liquidation.batchcreation.steps;

import co.com.asulado.usecase.liquidation.testdata.UseCaseTestData;
import lombok.experimental.UtilityClass;

@UtilityClass
public class FetchParticipantsStepTestData {

    public static final int PARTICIPANTS_COUNT_4 = 4;
    public static final int PARTICIPANTS_COUNT_0 = 0;

    public static RuntimeException createConnectionException() {
        return UseCaseTestData.createConnectionException();
    }
}
