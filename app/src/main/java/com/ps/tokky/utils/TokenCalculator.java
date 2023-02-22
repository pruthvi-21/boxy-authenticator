package com.ps.tokky.utils;

import com.ps.tokky.models.HashAlgorithm;
import com.ps.tokky.models.OTPLength;

import java.nio.ByteBuffer;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

public class TokenCalculator {
    private static byte[] generateHash(HashAlgorithm algorithm, byte[] key, byte[] data)
            throws NoSuchAlgorithmException, InvalidKeyException {
        String algo = "Hmac" + algorithm.name();

        Mac mac = Mac.getInstance(algo);
        mac.init(new SecretKeySpec(key, algo));

        return mac.doFinal(data);
    }

    public static int TOTP_RFC6238(byte[] secret, int period, OTPLength otpLength, HashAlgorithm algorithm, int offset) {
        long time = System.currentTimeMillis() / 1000;
        int fullToken = TOTP(secret, period, time, algorithm, offset);
        int div = (int) Math.pow(10, otpLength.getValue());

        return fullToken % div;
    }

    private static int TOTP(byte[] key, int period, long time, HashAlgorithm algorithm, int offset) {
        return HOTP(key, (time / period) + offset, algorithm);
    }

    private static int HOTP(byte[] key, long counter, HashAlgorithm algorithm) {
        int r = 0;

        try {
            byte[] data = ByteBuffer.allocate(8).putLong(counter).array();
            byte[] hash = generateHash(algorithm, key, data);

            int offset = hash[hash.length - 1] & 0xF;

            int binary = (hash[offset] & 0x7F) << 0x18;
            binary |= (hash[offset + 1] & 0xFF) << 0x10;
            binary |= (hash[offset + 2] & 0xFF) << 0x08;
            binary |= (hash[offset + 3] & 0xFF);

            r = binary;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return r;
    }
}
