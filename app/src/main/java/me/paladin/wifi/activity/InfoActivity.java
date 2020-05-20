package me.paladin.wifi.activity;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import me.paladin.wifi.R;
import me.paladin.wifi.dialog.CodeDialog;
import me.paladin.wifi.model.WifiModel;

public class InfoActivity extends AppCompatActivity implements View.OnClickListener {
    private WifiModel item;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        item = getIntent().getParcelableExtra("item");
        TextView name = findViewById(R.id.info_name);
        TextView password = findViewById(R.id.info_password);
        LinearLayout code = findViewById(R.id.info_action_get_code);
        LinearLayout copy_all = findViewById(R.id.info_action_copy_all);
        ImageView copy_password = findViewById(R.id.info_action_copy_password);
        name.setText(item.getSsid());
        password.setText(item.getPassword());
        code.setOnClickListener(this);
        copy_all.setOnClickListener(this);
        copy_password.setOnClickListener(this);
    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }
    
    @Override
    public void onClick(View view) {
        String text = null;
        switch (view.getId()) {
            case R.id.info_action_get_code:
                new CodeDialog().show(getSupportFragmentManager(), item);
                break;
            case R.id.info_action_copy_all:
                Toast.makeText(this, R.string.message_copied, Toast.LENGTH_SHORT).show();
                if (item.getUser().length() > 0) {
                    text = item.getSsid() + "\n" + item.getType() + "\n" + item.getUser() + "\n" + item.getPassword();
                } else {
                    text = item.getSsid() + "\n" + item.getType() + "\n" + item.getPassword();
                }
                break;
            case R.id.info_action_copy_password:
                Toast.makeText(this, R.string.message_copied_password, Toast.LENGTH_SHORT).show();
                text = item.getPassword();
                break;
        }
        if (text != null) {
            ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
            ClipData clip = ClipData.newPlainText("Info", text);
            clipboard.setPrimaryClip(clip);
        }
    }
}
