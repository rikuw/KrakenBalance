package net.rikuwikman.riku.krakenbalance;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    Button saveCredentialsBtn;
    TextView keyTextView;
    TextView secretTextView;

    static final int WALLET_ACTIVITY_REQUEST = 100;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        keyTextView = findViewById(R.id.keyText);
        secretTextView = findViewById(R.id.secretText);

        saveCredentialsBtn = findViewById(R.id.saveApiCredentialsBtn);
        saveCredentialsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String key = keyTextView.getText().toString();
                String secret = secretTextView.getText().toString();

                if (validApiCredentials(key, secret)) {
                    Intent intent = new Intent(MainActivity.this, WalletActivity.class);
                    intent.putExtra("key", key);
                    intent.putExtra("secret", secret);
                    startActivityForResult(intent, WALLET_ACTIVITY_REQUEST);
                    // TODO: Save credentials
                }
            }
        });
    }

    /**
     * Validate that the key and secret are valid API credentials.
     *
     * @param key    API key
     * @param secret API secret
     * @return boolean
     */
    private boolean validApiCredentials(String key, String secret) {
        if (!key.isEmpty() && !secret.isEmpty()) {
            return true;
        } else {
            return false;
        }
    }
}
