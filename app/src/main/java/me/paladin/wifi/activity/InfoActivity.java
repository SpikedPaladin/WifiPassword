package me.paladin.wifi.activity;

import androidx.annotation.StringRes;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import me.paladin.wifi.R;
import me.paladin.wifi.model.WifiItem;

public class InfoActivity extends AppCompatActivity {
    private WifiItem item;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        item = getIntent().getParcelableExtra("item");
        TextView ssidText, passwordText;
        ssidText = findViewById(R.id.infoSSID);
        passwordText = findViewById(R.id.infoPassword);
        ssidText.setText(item.getSsid());
        passwordText.setText(item.getPassword());
    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }
    
    public void onClick(View view) {
        int id = view.getId();
        @StringRes int message = -1;
        String value = "";
        if (id == R.id.infoSSIDCopy) {
            message = R.string.message_copied_ssid;
            value = item.getSsid();
        } else if (id == R.id.infoPasswordCopy) {
            message = R.string.message_copied_password;
            value = item.getPassword();
        }
        ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData clip = ClipData.newPlainText("Info", value);
        clipboard.setPrimaryClip(clip);
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
}
