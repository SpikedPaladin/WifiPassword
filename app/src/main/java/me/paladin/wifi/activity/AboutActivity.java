package me.paladin.wifi.activity;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

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
        if (id == R.id.aboutGithub) {
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://github.com/SpikedPaladin")));
        } else if (id == R.id.aboutVk) {
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://vk.com/paladingames")));
        } else if (id == R.id.aboutDiscord) {
            ClipboardManager clipboardManager = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
            ClipData clipData = ClipData.newPlainText("Tag", "Paladin#3107");
            clipboardManager.setPrimaryClip(clipData);
            Toast.makeText(this, R.string.message_copied_tag, Toast.LENGTH_SHORT).show();
        }
    }
}
