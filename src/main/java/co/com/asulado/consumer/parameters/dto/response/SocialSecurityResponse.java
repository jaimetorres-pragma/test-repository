package co.com.asulado.consumer.parameters.dto.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public record SocialSecurityResponse(
        @JsonProperty("reglas")
        SecurityRulesResponse rules,
        @JsonProperty("active")
        boolean active
) {}
