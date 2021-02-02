package com.sms.keycloak.authentication;

import org.keycloak.credential.CredentialProviderFactory;
import org.keycloak.models.KeycloakSession;

public class SecretCodeCredentialProviderFactory implements CredentialProviderFactory<SecretCodeCredentialProvider> {

    public static final String PROVIDER_ID =  "secret-code";

    @Override
    public String getId() {
        return PROVIDER_ID;
    }

    @Override
    public SecretCodeCredentialProvider create(KeycloakSession session) {
        return new SecretCodeCredentialProvider(session);
    }
}
