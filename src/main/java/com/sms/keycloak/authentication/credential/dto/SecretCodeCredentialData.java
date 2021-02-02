package com.sms.keycloak.authentication.credential.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class SecretCodeCredentialData {

    private final String label;

    @JsonCreator
    public SecretCodeCredentialData(@JsonProperty("label") String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }
}
