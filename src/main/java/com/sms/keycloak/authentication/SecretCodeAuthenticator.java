package com.sms.keycloak.authentication;

import com.google.i18n.phonenumbers.NumberParseException;
import com.google.i18n.phonenumbers.PhoneNumberUtil;
import com.google.i18n.phonenumbers.Phonenumber;
import com.sms.keycloak.authentication.credential.SecretCodeCredentialModel;
import com.sms.keycloak.authentication.sms.SMSImplementation;
import org.jboss.logging.Logger;
import org.keycloak.authentication.AuthenticationFlowContext;
import org.keycloak.authentication.AuthenticationFlowError;
import org.keycloak.authentication.Authenticator;
import org.keycloak.authentication.CredentialValidator;
import org.keycloak.credential.CredentialModel;
import org.keycloak.credential.CredentialProvider;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.RealmModel;
import org.keycloak.models.UserCredentialModel;
import org.keycloak.models.UserModel;
import twitter4j.JSONObject;

import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;
import java.util.List;

public class SecretCodeAuthenticator implements Authenticator, CredentialValidator<SecretCodeCredentialProvider> {
    private static final Logger logger = Logger.getLogger(SecretCodeAuthenticator.class);

    @Override
    public void authenticate(AuthenticationFlowContext context) {
        String phoneNumber = context.getUser().getUsername();
        PhoneNumberUtil phoneUtil = PhoneNumberUtil.getInstance();
        Phonenumber.PhoneNumber numberProto = null;

        try {
            numberProto = phoneUtil.parse(phoneNumber, Phonenumber.PhoneNumber.CountryCodeSource.UNSPECIFIED.name());
            logger.info(numberProto.getCountryCode() + " " + numberProto.getNationalNumber() + " ");
        } catch (NumberParseException e) {
            logger.error("NumberParseException was thrown: " + e.toString());
        }

        if (numberProto == null) {
            Response challenge = context.form()
                    .setError("Username not in a correct phone number format")
                    .createForm("error-page.ftl");
            context.failureChallenge(AuthenticationFlowError.INVALID_CREDENTIALS, challenge);
            return;
        }

        String smsCode = CommonUtil.getSmsCode(7);
        boolean success = false;
        try {
            String response = SMSImplementation.getInstance().sendSMS(smsCode, numberProto);
            logger.info("SMS sending response: " + response);
            success = true;
        } catch (Exception e) {
            logger.error("Exception was thrown: " + e.toString());
        }

        if (!success) {
            Response challenge = context.form()
                    .setError("Failed to send SMS to the username. Please check your server logs")
                    .createForm("error-page.ftl");
            context.failureChallenge(AuthenticationFlowError.INVALID_CREDENTIALS, challenge);
            return;
        }

        SecretCodeCredentialProvider sqcp = (SecretCodeCredentialProvider) context.getSession().getProvider(CredentialProvider.class, "secret-code");
        List<CredentialModel> credentialModels = sqcp.getModelByType(context.getRealm(), context.getUser(), SecretCodeCredentialModel.TYPE);

        if (!credentialModels.isEmpty()) {
            CredentialModel mod = credentialModels.get(0);
            JSONObject obj = new JSONObject();
            obj.put("code", smsCode);
            mod.setSecretData(obj.toString());
            sqcp.updateCredential(context.getRealm(), context.getUser(), mod);
        } else {
            sqcp.createCredential(context.getRealm(), context.getUser(), SecretCodeCredentialModel.createSecretCode("Security Code", smsCode));
        }

        Response challenge = context.form()
                .createForm("secret-code.ftl");
        context.challenge(challenge);
    }

    @Override
    public void action(AuthenticationFlowContext context) {
        boolean validated = validateCode(context);
        if (!validated) {
            Response challenge = context.form()
                    .setError("Invalid SMS Code")
                    .createForm("secret-code.ftl");
            context.failureChallenge(AuthenticationFlowError.INVALID_CREDENTIALS, challenge);
            return;
        }
        context.success();
    }


    protected boolean validateCode(AuthenticationFlowContext context) {
        MultivaluedMap<String, String> formData = context.getHttpRequest().getDecodedFormParameters();
        String secret = formData.getFirst("secret_code");
        String credentialId = formData.getFirst("credentialId");
        if (credentialId == null || credentialId.isEmpty()) {
            credentialId = getCredentialProvider(context.getSession())
                    .getDefaultCredential(context.getSession(), context.getRealm(), context.getUser()).getId();
        }

        UserCredentialModel input = new UserCredentialModel(credentialId, getType(context.getSession()), secret);
        return getCredentialProvider(context.getSession()).isValid(context.getRealm(), context.getUser(), input);
    }

    @Override
    public boolean requiresUser() {
        return true;
    }

    @Override
    public boolean configuredFor(KeycloakSession session, RealmModel realm, UserModel user) {
        return getCredentialProvider(session).isConfiguredFor(realm, user, getType(session));
    }

    @Override
    public void setRequiredActions(KeycloakSession session, RealmModel realm, UserModel user) {

    }

    @Override
    public void close() {

    }

    @Override
    public SecretCodeCredentialProvider getCredentialProvider(KeycloakSession session) {
        return (SecretCodeCredentialProvider) session.getProvider(CredentialProvider.class, SecretCodeCredentialProviderFactory.PROVIDER_ID);
    }
}
