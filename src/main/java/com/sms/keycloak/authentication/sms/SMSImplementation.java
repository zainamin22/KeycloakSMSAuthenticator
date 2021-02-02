package com.sms.keycloak.authentication.sms;

import com.google.i18n.phonenumbers.Phonenumber;

public class SMSImplementation {

    private static SMSImplementation instance;

    private SMSImplementation() {

    }

    public static SMSImplementation getInstance() {
        if (instance == null) {
            instance = new SMSImplementation();
        }
        return instance;
    }

    public String sendSMS(String smsCode, Phonenumber.PhoneNumber numberProto) throws Exception {
        String message = "Keycloak secret security code: " + smsCode;
        int countryCode = numberProto.getCountryCode();
        long phoneNumber = numberProto.getNationalNumber();

        // Implement SMS Implementation and then return a responseText and throw an exception with error message in case of any problem

        String responseText = "SMS sent successfully";
        return responseText;
    }
}
