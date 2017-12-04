package net.rikuwikman.riku.krakenbalance;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Map;

import javax.net.ssl.HttpsURLConnection;

public class KrakenApiRequest {
    private URL url;
    private String signature;
    private String key;
    private StringBuilder postData;
    private boolean isPublic;

    public KrakenApiRequest() throws MalformedURLException {
    }

    public void setSignature(String signature) {
        this.signature = signature;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getUrlPath() {
        return url.getPath();
    }

    private final String PUBLIC_URL = "https://api.kraken.com/0/public/";
    private final String PRIVATE_URL = "https://api.kraken.com/0/private/";

    public enum Method {
        TICKER("Ticker", true),
        BALANCE("Balance", false),
        TRADE_BALANCE("TradeBalance", false);

        public final String name;
        public final boolean isPublic;

        Method(String name, boolean isPublic) {
            this.name = name;
            this.isPublic = isPublic;
        }
    }

    /**
     * Sends the request to the URL and returns its response.
     *
     * @return String response
     * @throws IOException If the connection cannot be sent.
     */
    public JSONObject send() throws IOException, JSONException {
        HttpsURLConnection connection = null;

        try {
            connection = (HttpsURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            connection.addRequestProperty("User-Agent", "KrakenBalance");

            if (!isPublic) {
                if (key == null || signature == null || postData == null) {
                    throw new IllegalStateException("Key, signature and postData are required to make API requests");
                }

                connection.addRequestProperty("API-Key", key.trim());
                connection.addRequestProperty("API-Sign", signature.trim());
            }

            if (postData != null && !postData.toString().isEmpty()) {
                connection.setDoOutput(true);

                try (OutputStreamWriter out = new OutputStreamWriter(connection.getOutputStream())) {
                    out.write(postData.toString());
                }
            }

            try (BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()))) {
                StringBuilder response = new StringBuilder();
                String line;

                while ((line = in.readLine()) != null) {
                    response.append(line);
                }

                //System.out.println(response.toString());

                return parseJSON(response.toString());
            }
        } finally {
            connection.disconnect();
        }
    }

    public String setMethod(Method method) throws MalformedURLException {

        if (method == null) {
            throw new IllegalArgumentException("No method set!");
        }

        isPublic = method.isPublic;
        url = new URL((isPublic ? PUBLIC_URL : PRIVATE_URL) + method.name);
        return url.getPath();
    }

    private JSONObject parseJSON(String jsonStr) throws JSONException {
        JSONObject jsonObject = new JSONObject(jsonStr);

        return jsonObject;
    }

    public String setParameters(Map<String, String> parameters) throws UnsupportedEncodingException {
        if (parameters == null || parameters.isEmpty()) {
            throw new IllegalArgumentException("No parameters set!");
        }

        postData = new StringBuilder();
        for (Map.Entry<String, String> entry : parameters.entrySet()) {
            postData.append(entry.getKey()).append("=").append(KrakenUtils.urlEncode(entry.getValue()).trim()).append("&");
        }

        return postData.toString();
    }
}
