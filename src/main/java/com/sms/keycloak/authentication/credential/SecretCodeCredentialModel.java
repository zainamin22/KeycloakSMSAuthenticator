package com.sms.keycloak.authentication.credential;

import com.sms.keycloak.authentication.credential.dto.SecretCodeCredentialData;
import com.sms.keycloak.authentication.credential.dto.SecretCodeSecretData;
import org.keycloak.common.util.Time;
import org.keycloak.credential.CredentialModel;
import org.keycloak.util.JsonSerialization;

import java.io.IOException;


public class SecretCodeCredentialModel extends CredentialModel {
    public static final String TYPE = "SECRET_CODE";

    private final SecretCodeCredentialData credentialData;
    private final SecretCodeSecretData secretData;

    private SecretCodeCredentialModel(SecretCodeCredentialData credentialData, SecretCodeSecretData secretData) {
        this.credentialData = credentialData;
        this.secretData = secretData;
    }

    private SecretCodeCredentialModel(String label, String code) {
        credentialData = new SecretCodeCredentialData(label);
        secretData = new SecretCodeSecretData(code);
    }

    public static SecretCodeCredentialModel createSecretCode(String label, String code) {
        SecretCodeCredentialModel credentialModel = new SecretCodeCredentialModel(label, code);
        credentialModel.fillCredentialModelFields();
        return credentialModel;
    }

    public static SecretCodeCredentialModel createFromCredentialModel(CredentialModel credentialModel){
        try {
            SecretCodeCredentialData credentialData = JsonSerialization.readValue(credentialModel.getCredentialData(), SecretCodeCredentialData.class);
            SecretCodeSecretData secretData = JsonSerialization.readValue(credentialModel.getSecretData(), SecretCodeSecretData.class);

            SecretCodeCredentialModel secretCodeCredentialModel = new SecretCodeCredentialModel(credentialData, secretData);
            secretCodeCredentialModel.setUserLabel(credentialModel.getUserLabel());
            secretCodeCredentialModel.setCreatedDate(credentialModel.getCreatedDate());
            secretCodeCredentialModel.setType(TYPE);
            secretCodeCredentialModel.setId(credentialModel.getId());
            secretCodeCredentialModel.setSecretData(credentialModel.getSecretData());
            secretCodeCredentialModel.setCredentialData(credentialModel.getCredentialData());
            return secretCodeCredentialModel;
        } catch (IOException e){
            throw new RuntimeException(e);
        }
    }

    public SecretCodeSecretData getSecretCodeSecretData() {
        return secretData;
    }

    private void fillCredentialModelFields(){
        try {
            setCredentialData(JsonSerialization.writeValueAsString(credentialData));
            setSecretData(JsonSerialization.writeValueAsString(secretData));
            setType(TYPE);
            setCreatedDate(Time.currentTimeMillis());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}
