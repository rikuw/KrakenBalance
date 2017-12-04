package net.rikuwikman.riku.krakenbalance;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import android.util.Base64;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

final class KrakenUtils {
    private static final String UTF8 = "UTF-8";

    private static final String SHA256 = "SHA-256";
    private static final String HMAC_SHA512 = "HmacSHA512";

    public static byte[] base64Decode(String input) {
        return Base64.decode(input.getBytes(), Base64.NO_WRAP);
    }

    public static String base64Encode(byte[] data) {
        return Base64.encodeToString(data, Base64.NO_WRAP);
    }

    public static byte[] concatArrays(byte[] a, byte[] b) {

        if (a == null || b == null) {
            throw new IllegalArgumentException("Array cannot be null!");
        }

        byte[] concat = new byte[a.length + b.length];

        for (int i = 0; i < concat.length; i++) {
            concat[i] = i < a.length ? a[i] : b[i - a.length];
        }

        return concat;
    }

    public static byte[] hmacSha512(byte[] key, byte[] message) throws NoSuchAlgorithmException, InvalidKeyException {
        Mac mac = Mac.getInstance(HMAC_SHA512);
        mac.init(new SecretKeySpec(key, HMAC_SHA512));
        return mac.doFinal(message);
    }

    public static byte[] sha256(String message) throws NoSuchAlgorithmException {
        MessageDigest md = MessageDigest.getInstance(SHA256);
        return md.digest(stringToBytes(message));
    }

    public static byte[] stringToBytes(String input) {

        if (input == null) {
            throw new IllegalArgumentException("Input cannot be null!");
        }

        return input.getBytes(Charset.forName(UTF8));
    }

    public static String urlEncode(String input) throws UnsupportedEncodingException {
        return URLEncoder.encode(input, UTF8);
    }

    private KrakenUtils() {}
}
