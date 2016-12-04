package com.github.anthony_o.swingifts.util;

import java.util.Base64;

public class Base64Utils {

    public static byte[] convertFromBase64RFC4648ToBytes(String key) {
        return Base64.getDecoder().decode(key.replaceAll("-", "+").replaceAll("_", "/")); // to follow spec RFC 4648
    }

    public static String convertFromBytesToBase64RFC4648(byte[] key) {
        return Base64.getEncoder().encodeToString(key).replaceAll("/", "_").replaceAll("\\+", "-"); // to follow spec RFC 4648
    }
}
