package co.com.asulado.model.error;

import lombok.Getter;

import java.util.Collections;
import java.util.List;

@Getter
public class BusinessException extends RuntimeException {
    private final ErrorCode errorCode;
    private final String traceId;
    private final List<String> params;

    public BusinessException(ErrorCode errorCode, String traceId) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
        this.traceId = traceId;
        this.params = Collections.emptyList();
    }
    public BusinessException(ErrorCode errorCode, String param, String traceId) {
        super(formatMessage(errorCode.getMessage(), param));
        this.errorCode = errorCode;
        this.traceId = traceId;
        this.params = Collections.singletonList(param);
    }

    private static String formatMessage(String pattern, Object param) {
        try {
            return String.format(pattern, param);
        } catch (Exception e) {
            return pattern;
        }
    }

}