package net.rikuwikman.riku.krakenbalance;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;

public class KrakenApi {
    private static final String OTP = "otp";
    private static final String NONCE = "nonce";
    private static final String MICRO_SECONDS = "000";

    private String key;
    private String secret;

    public void setKey(String key) {
        this.key = key;
    }

    public void setSecret(String secret) {
        this.secret = secret;
    }

    public JSONObject getBalance(String otp) throws IOException, NoSuchAlgorithmException, InvalidKeyException, JSONException {
        KrakenApiRequest request = new KrakenApiRequest();
        request.setKey(key);

        HashMap<String, String> parameters = new HashMap<>();

        if (otp != null) {
            parameters.put(OTP, otp);
        }

        String nonce = String.valueOf(System.currentTimeMillis()) + MICRO_SECONDS;
        parameters.put(NONCE, nonce);

        String postData = request.setParameters(parameters);

        request.setMethod(KrakenApiRequest.Method.BALANCE);

        byte[] sha256 = KrakenUtils.sha256(nonce + postData);
        byte[] path = KrakenUtils.stringToBytes(request.getUrlPath());
        byte[] hmacKey = KrakenUtils.base64Decode(secret);
        byte[] hmacMessage = KrakenUtils.concatArrays(path, sha256);

        String hmacDigest = KrakenUtils.base64Encode(KrakenUtils.hmacSha512(hmacKey, hmacMessage));
        request.setSignature(hmacDigest);

        return request.send();
    }

    public JSONObject getTradeBalance(String otp, String asset) throws IOException, NoSuchAlgorithmException, InvalidKeyException, JSONException {
        KrakenApiRequest request = new KrakenApiRequest();
        request.setKey(key);

        HashMap<String, String> parameters = new HashMap<>();

        if (otp != null) {
            parameters.put(OTP, otp);
        }

        String nonce = String.valueOf(System.currentTimeMillis()) + MICRO_SECONDS;
        parameters.put(NONCE, nonce);
        parameters.put("asset", asset);

        String postData = request.setParameters(parameters);

        request.setMethod(KrakenApiRequest.Method.TRADE_BALANCE);

        byte[] sha256 = KrakenUtils.sha256(nonce + postData);
        byte[] path = KrakenUtils.stringToBytes(request.getUrlPath());
        byte[] hmacKey = KrakenUtils.base64Decode(secret);
        byte[] hmacMessage = KrakenUtils.concatArrays(path, sha256);

        String hmacDigest = KrakenUtils.base64Encode(KrakenUtils.hmacSha512(hmacKey, hmacMessage));
        request.setSignature(hmacDigest);

        return request.send();
    }

    public JSONObject getTicker(String pair) throws IOException, JSONException {
        KrakenApiRequest request = new KrakenApiRequest();
        request.setMethod(KrakenApiRequest.Method.TICKER);

        HashMap<String, String> parameters = new HashMap<>();
        parameters.put("pair", pair);
        request.setParameters(parameters);

        return request.send();
    }
}
