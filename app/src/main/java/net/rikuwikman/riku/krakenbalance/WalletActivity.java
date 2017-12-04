package net.rikuwikman.riku.krakenbalance;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.concurrent.CopyOnWriteArrayList;

public class WalletActivity extends AppCompatActivity {

    CopyOnWriteArrayList<HashMap<String, String>> balances;
    ListView walletList;
    TextView totalBalance;

    private static WalletListAdapter adapter;
    private KrakenApi api = new KrakenApi();

    private String totalAmount;

    public enum Currency {
        XXBT("XXBT", "XBT", "Bitcoin", "BTC"),
        BCH("BCH", "BCH", "Bitcoin Cash", "BCH"),
        ZEUR("ZEUR", "EUR", "Euro", "€");

        public final String name;
        public final String pairName;
        public final String longName;
        public final String symbol;

        Currency(String name, String pairName, String longName, String symbol) {
            this.name = name;
            this.pairName = pairName;
            this.longName = longName;
            this.symbol = symbol;
        }
    }

    private Currency defaultCurrency = Currency.ZEUR;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wallet);

        walletList = findViewById(R.id.walletList);
        totalBalance = findViewById(R.id.totalBalance);

        Intent intent = getIntent();
        api.setKey(intent.getStringExtra("key"));
        api.setSecret(intent.getStringExtra("secret"));

        new RetrieveBalanceTask().execute();
        new RetrieveTradeBalanceTask().execute();

        balances = new CopyOnWriteArrayList<>();

        adapter = new WalletListAdapter(balances, getApplicationContext());

        walletList.setAdapter(adapter);
    }

    class RetrieveBalanceTask extends AsyncTask<String, Void, JSONObject> {
        protected JSONObject doInBackground(String... urls) {
            JSONObject balanceJson = null;

            try {
                balanceJson = api.getBalance(null);
            } catch (NoSuchAlgorithmException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (InvalidKeyException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }

            return balanceJson;
        }

        protected void onPostExecute(JSONObject balanceJson) {
            try {
                JSONArray errors = balanceJson.getJSONArray("error");

                if (errors.length() != 0) {
                    // TODO: Handle errors
                }

                JSONObject result = balanceJson.getJSONObject("result");
                Iterator<String> iterator = result.keys();
                while (iterator.hasNext()) {
                    String currency = iterator.next();
                    String value = result.getString(currency);

                    addCurrencyBalance(currency, value);
                    findViewById(R.id.loadingPanel).setVisibility(View.GONE);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    class RetrieveTradeBalanceTask extends AsyncTask<String, Void, JSONObject> {
        protected JSONObject doInBackground(String... urls) {
            JSONObject balanceJson = null;

            try {
                balanceJson = api.getTradeBalance(null, "ZEUR");
            } catch (NoSuchAlgorithmException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (InvalidKeyException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }

            return balanceJson;
        }

        protected void onPostExecute(JSONObject balanceJson) {
            try {
                JSONArray errors = balanceJson.getJSONArray("error");

                if (errors.length() != 0) {
                    // TODO: Handle errors
                }

                JSONObject result = balanceJson.getJSONObject("result");
                updateTotalBalance(result.getString("eb"));
                findViewById(R.id.loadingPanel2).setVisibility(View.GONE);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    class RetrieveTickerTask extends AsyncTask<String, Void, JSONObject> {
        private String currency;
        private String pair;

        public RetrieveTickerTask(String currency, String pair) {
            super();
            this.currency = currency;
            this.pair = pair;
        }

        protected JSONObject doInBackground(String... pairs) {
            JSONObject tickerJson = null;

            try {
                tickerJson = api.getTicker(pair);
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }

            return tickerJson;
        }

        protected void onPostExecute(JSONObject tickerJson) {
            try {
                JSONArray errors = tickerJson.getJSONArray("error");

                if (errors.length() != 0) {
                    // TODO: Handle errors
                }

                JSONObject result = tickerJson.getJSONObject("result");

                Iterator<String> iterator = result.keys();
                while (iterator.hasNext()) {
                    String key = iterator.next();
                    JSONObject rate = result.getJSONObject(key);
                    JSONArray rateC = rate.getJSONArray("c");
                    addCurrencyConverted(currency, rateC);
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    private void updateTotalBalance(String total) {
        if (total != null) {
            totalAmount = total;
        }

        double totalDouble = Double.parseDouble(totalAmount);
        DecimalFormat decimalFormat = new DecimalFormat();
        decimalFormat.setMaximumFractionDigits(2);

        totalBalance.setText("Total balance: " + decimalFormat.format(totalDouble) + " " + defaultCurrency.symbol);
    }

    private void addCurrencyBalance(String currency, String value) throws IOException, JSONException {
        HashMap<String, String> currencyBalance = new HashMap<>();
        currencyBalance.put("currency", currency);
        currencyBalance.put("amount", value);
        currencyBalance.put("amount_converted", "...");

        String pair = Currency.valueOf(currency).pairName;
        pair += defaultCurrency.pairName;

        balances.add(currencyBalance);

        new RetrieveTickerTask(currency, pair).execute();

        adapter.notifyDataSetChanged();
    }

    private void addCurrencyConverted(String currency, JSONArray rate) throws JSONException {
        for (HashMap<String, String> currencyBalance : balances) {
            if (currencyBalance.get("currency") == currency) {
                balances.remove(currencyBalance);

                String convertRate = rate.getString(0);

                double balance = Double.parseDouble(currencyBalance.get("amount"));
                double convertRateDouble = Double.parseDouble(convertRate);
                double convertedBalance = balance * convertRateDouble;
                DecimalFormat decimalFormat = new DecimalFormat();
                decimalFormat.setMaximumFractionDigits(2);

                currencyBalance.put("amount_converted", decimalFormat.format(convertedBalance));

                balances.add(currencyBalance);
                adapter.notifyDataSetChanged();
            }
        }
    }
}
