package me.paladin.wifi.activity;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import me.paladin.wifi.BuildConfig;
import me.paladin.wifi.R;

public class AboutActivity extends AppCompatActivity {
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        ((TextView) findViewById(R.id.about_version)).setText(getString(R.string.app_version, BuildConfig.VERSION_NAME));
    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }
    
    public void onClick(View view) {
        int id = view.getId();
        if (id == R.id.about_github) {
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://github.com/SpikedPaladin/WifiPassword")));
        } else if (id == R.id.about_telegram) {
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://t.me/PaladinDev")));
        }
    }
}
