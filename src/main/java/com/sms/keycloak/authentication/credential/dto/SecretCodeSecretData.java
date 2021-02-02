package com.sms.keycloak.authentication.credential.dto;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class SecretCodeSecretData {

     private final String code;

    @JsonCreator
     public SecretCodeSecretData(@JsonProperty("code") String code) {
         this.code = code;
     }

    public String getCode() {
        return code;
    }
}
