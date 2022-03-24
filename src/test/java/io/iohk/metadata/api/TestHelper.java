package io.iohk.metadata.api;

import io.restassured.internal.UriValidator;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.net.URI;

public class TestHelper {

    public static boolean checkHexString(String value, int expectedLength) {
        try {
            BigInteger convertedValue = new BigInteger(value, 16);
            if ( convertedValue != null && value.length() == expectedLength) {
                return true;
            } else {
                return false;
            }
        } catch (Exception exception) {
            return false;
        }
    }
}
