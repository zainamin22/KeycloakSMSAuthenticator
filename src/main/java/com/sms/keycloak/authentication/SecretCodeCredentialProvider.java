package com.sms.keycloak.authentication;

import com.sms.keycloak.authentication.credential.SecretCodeCredentialModel;
import org.jboss.logging.Logger;
import org.keycloak.common.util.Time;
import org.keycloak.credential.CredentialInput;
import org.keycloak.credential.CredentialInputValidator;
import org.keycloak.credential.CredentialModel;
import org.keycloak.credential.CredentialProvider;
import org.keycloak.credential.CredentialTypeMetadata;
import org.keycloak.credential.CredentialTypeMetadataContext;
import org.keycloak.credential.UserCredentialStore;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.RealmModel;
import org.keycloak.models.UserCredentialModel;
import org.keycloak.models.UserModel;

import java.util.List;

public class SecretCodeCredentialProvider implements CredentialProvider<SecretCodeCredentialModel>, CredentialInputValidator {
    private static final Logger logger = Logger.getLogger(SecretCodeCredentialProvider.class);

    protected KeycloakSession session;

    public SecretCodeCredentialProvider(KeycloakSession session) {
        this.session = session;
    }

    private UserCredentialStore getCredentialStore() {
        return session.userCredentialManager();
    }


    public List<CredentialModel> getModelByType(RealmModel realm, UserModel user, String credentialType) {
        return getCredentialStore().getStoredCredentialsByType(realm, user, credentialType);
    }

    @Override
    public boolean isValid(RealmModel realm, UserModel user, CredentialInput input) {
        if (!(input instanceof UserCredentialModel)) {
            logger.debug("Expected instance of UserCredentialModel for CredentialInput");
            return false;
        }
        if (!input.getType().equals(getType())) {
            return false;
        }
        String challengeResponse = input.getChallengeResponse();
        if (challengeResponse == null) {
            return false;
        }
        CredentialModel credentialModel = getCredentialStore().getStoredCredentialById(realm, user, input.getCredentialId());
        SecretCodeCredentialModel sqcm = getCredentialFromModel(credentialModel);
        return sqcm.getSecretCodeSecretData().getCode().equals(challengeResponse);
    }

    @Override
    public boolean supportsCredentialType(String credentialType) {
        return getType().equals(credentialType);
    }

    @Override
    public boolean isConfiguredFor(RealmModel realm, UserModel user, String credentialType) {
        if (!supportsCredentialType(credentialType)) return false;
        return true;
    }

    @Override
    public CredentialModel createCredential(RealmModel realm, UserModel user, SecretCodeCredentialModel credentialModel) {
        if (credentialModel.getCreatedDate() == null) {
            credentialModel.setCreatedDate(Time.currentTimeMillis());
        }
        return getCredentialStore().createCredential(realm, user, credentialModel);
    }


    public void updateCredential(RealmModel realm, UserModel user, CredentialModel credentialModel) {
        getCredentialStore().updateCredential(realm, user, credentialModel);
    }

    @Override
    public boolean deleteCredential(RealmModel realm, UserModel user, String credentialId) {
        return getCredentialStore().removeStoredCredential(realm, user, credentialId);
    }

    @Override
    public SecretCodeCredentialModel getCredentialFromModel(CredentialModel model) {
        return SecretCodeCredentialModel.createFromCredentialModel(model);
    }

    @Override
    public CredentialTypeMetadata getCredentialTypeMetadata(CredentialTypeMetadataContext metadataContext) {
        return CredentialTypeMetadata.builder()
                .type(getType())
                .category(CredentialTypeMetadata.Category.TWO_FACTOR)
                .displayName(SecretCodeCredentialProviderFactory.PROVIDER_ID)
                .helpText("Secret SMS Code")
                .createAction(SecretCodeAuthenticatorFactory.PROVIDER_ID)
                .removeable(false)
                .build(session);
    }

    @Override
    public String getType() {
        return SecretCodeCredentialModel.TYPE;
    }

}
