package com.github.anthonyo.swingifts.service.util;

import org.apache.commons.lang3.RandomStringUtils;

import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Base64;

/**
 * Utility class for generating random Strings.
 */
public final class RandomUtil {

    private static final int DEF_COUNT = 20;

    private static final SecureRandom SECURE_RANDOM;

//    private static final SecureRandom STRONG_SECURE_RANDOM;

    private static final int EVENT_PUBLIC_KEY_RANDOM_BYTES_COUNT = 24;

    static {
        SECURE_RANDOM = new SecureRandom();
        SECURE_RANDOM.nextBytes(new byte[64]);

//        try {
//            STRONG_SECURE_RANDOM = SecureRandom.getInstanceStrong(); // Generate strong random bytes thanks to https://stackoverflow.com/a/34912596/535203 // not used because it is too long to generate random
//        } catch (NoSuchAlgorithmException e) {
//            throw new IllegalStateException("Can't initialize a strong SecureRandom", e);
//        }
    }

    private RandomUtil() {
    }

    private static String generateRandomAlphanumericString() {
        return RandomStringUtils.random(DEF_COUNT, 0, 0, true, true, null, SECURE_RANDOM);
    }

    /**
     * Generate a password.
     *
     * @return the generated password.
     */
    public static String generatePassword() {
        return generateRandomAlphanumericString();
    }

    /**
     * Generate an activation key.
     *
     * @return the generated activation key.
     */
    public static String generateActivationKey() {
        return generateRandomAlphanumericString();
    }

    /**
     * Generate a reset key.
     *
     * @return the generated reset key.
     */
    public static String generateResetKey() {
        return generateRandomAlphanumericString();
    }

    public static String generateEventPublicKey() {
        byte[] bytes = new byte[EVENT_PUBLIC_KEY_RANDOM_BYTES_COUNT];
        SECURE_RANDOM.nextBytes(bytes);
        return Base64.getUrlEncoder().encodeToString(bytes);
    }
}
