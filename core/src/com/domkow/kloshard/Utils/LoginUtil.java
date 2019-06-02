package com.domkow.kloshard.Utils;

import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;

public class LoginUtil {

    public static boolean isValidEmailAddress(String email) {
        boolean result = true;
        try {
            InternetAddress emailAddr = new InternetAddress(email);
            emailAddr.validate();
        } catch (AddressException ex) {
            result = false;
        }
        return result;
    }

    public static boolean isValidPassword(String password) {
        return password.length() >= 6;
    }
}
