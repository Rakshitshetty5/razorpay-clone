package com.rakshit.razorpay.common.util;

import java.security.SecureRandom;
import java.util.Base64;

public class RandomizerUtil {

    // Cryptographically secure random number generator.
    // Much stronger than java.util.Random for tokens, IDs, passwords, etc.
    private static final SecureRandom SECURE_RANDOM = new SecureRandom();

    /**
     * Generates a URL-safe Base64 random string.
     *
     * @param bytesLength Number of RANDOM BYTES to generate.
     *                    This is NOT the final string length.
     *
     * @return URL-safe Base64 encoded string without '=' padding.
     */
    public static String randomBase64(int bytesLength) {

        // Create byte array of requested size.
        // Example for bytesLength = 4:
        // [0, 0, 0, 0]
        byte[] randomBytes = new byte[bytesLength];

        // Fill array with random values.
        // Example:
        // [-23, 44, 101, -7]
        SECURE_RANDOM.nextBytes(randomBytes);

        // Convert bytes into URL-safe Base64.
        // URL-safe means:
        // '+' becomes '-'
        // '/' becomes '_'
        //
        // withoutPadding() removes trailing '=' characters.
        return Base64.getUrlEncoder()
                .withoutPadding()
                .encodeToString(randomBytes);
    }
}