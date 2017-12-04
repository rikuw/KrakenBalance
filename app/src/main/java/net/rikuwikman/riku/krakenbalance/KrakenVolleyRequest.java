package net.rikuwikman.riku.krakenbalance;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

public class KrakenVolleyRequest {
    private URL url;
    private String signature;
    private String key;
    private StringBuilder postData;
    private boolean isPublic;
    RequestQueue queue;

    public KrakenVolleyRequest(RequestQueue queue) throws MalformedURLException {
        this.queue = queue;
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

    private Map<String, String> getHeaders() {
        Map<String, String> headers = new HashMap<String, String>();
        headers.put("User-Agent", "KrakenBalance");
        headers.put("API-Key", key.trim());
        headers.put("API-Sign", signature.trim());

        return headers;
    }

    public void send() throws IOException, JSONException {
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, getUrlPath(), null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });

        queue.add(request);
    }
}
