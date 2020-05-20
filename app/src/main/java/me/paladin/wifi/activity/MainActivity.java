package me.paladin.wifi.activity;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.FragmentManager;
import androidx.preference.PreferenceManager;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;


import me.paladin.wifi.R;
import me.paladin.wifi.fragment.SystemFragment;
import me.paladin.wifi.dialog.ExitDialog;

public class MainActivity extends AppCompatActivity {
    private SharedPreferences preferences;
    private SystemFragment systemFragment;
    private ExitDialog exitDialog;
    private long backPressTime;
    private boolean adLoaded;
    MenuItem searchItem;
    AdView banner;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        preferences = PreferenceManager.getDefaultSharedPreferences(this);
        banner = findViewById(R.id.main_banner);
        if (preferences != null && !preferences.getBoolean("ad_enabled", true)) {
            banner.setVisibility(View.GONE);
            adLoaded = false;
        } else {
            AdRequest request = new AdRequest.Builder().build();
            banner.loadAd(request);
            adLoaded = true;
        }
        backPressTime = System.currentTimeMillis() - 2000;
        exitDialog = new ExitDialog(this);
        FragmentManager manager = getSupportFragmentManager();
        systemFragment = (SystemFragment) manager.findFragmentByTag("FRAGMENT_SYSTEM");
        if (systemFragment == null) {
            systemFragment = new SystemFragment();
            manager.beginTransaction().add(R.id.main_container, systemFragment, "FRAGMENT_SYSTEM").commit();
        }
    }
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main_toolbar, menu);
        searchItem = menu.findItem(R.id.item_main_search);
        if (preferences != null && !preferences.getBoolean("search_enabled", true)) {
            searchItem.setVisible(false);
            return true;
        }
        SearchView searchView = (SearchView) searchItem.getActionView();
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            public boolean onQueryTextSubmit(String query) {
                return false;
            }
            
            public boolean onQueryTextChange(String newText) {
                systemFragment.adapter.getFilter().filter(newText);
                return true;
            }
        });
        return true;
    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.item_main_refresh) {
            return false;
        } else if (id == R.id.item_main_settings) {
            startActivityForResult(new Intent(this, SettingsActivity.class), 1);
        } else if (id == R.id.item_main_share) {
            Intent intent = new Intent(Intent.ACTION_SEND);
            intent.setType("text/plain");
            intent.putExtra(Intent.EXTRA_SUBJECT, R.string.app_name);
            intent.putExtra(Intent.EXTRA_TEXT, "http://play.google.com/store/apps/details?id=" + getPackageName());
            startActivity(Intent.createChooser(intent, getString(R.string.action_share)));
        } else if (id == R.id.item_main_about) {
            startActivity(new Intent(this, AboutActivity.class));
        } else if (id == R.id.item_main_rate) {
            try {
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + getPackageName())));
            } catch (ActivityNotFoundException ex) {
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("http://play.google.com/store/apps/details?id=" + getPackageName())));
            }
        } else if (id == R.id.item_main_exit) {
            exitDialog.show(getSupportFragmentManager());
        }
        return super.onOptionsItemSelected(item);
    }
    
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == RESULT_OK && data != null) {
            if (data.getBooleanExtra("ads", false)) {
                if (banner.getVisibility() == View.VISIBLE) {
                    banner.pause();
                    banner.setVisibility(View.GONE);
                } else if (banner.getVisibility() == View.GONE && adLoaded) {
                    banner.resume();
                    banner.setVisibility(View.VISIBLE);
                } else if (banner.getVisibility() == View.GONE && !adLoaded) {
                    banner.resume();
                    banner.setVisibility(View.VISIBLE);
                    AdRequest request = new AdRequest.Builder().build();
                    banner.loadAd(request);
                    adLoaded = true;
                }
            }
            if (data.getBooleanExtra("search", false)) {
                if (searchItem.isVisible()) {
                    searchItem.collapseActionView();
                    searchItem.setVisible(false);
                } else {
                    searchItem.setVisible(true);
                }
            }
            if (data.getBooleanExtra("pass", false)) {
                systemFragment.adapter.notifyDataSetChanged();
            }
        }
    }
    
    @Override
    public void onBackPressed() {
        if (preferences != null && preferences.getBoolean("back_pressed_enabled", true)) {
            String action = preferences.getString("back_pressed_action", "dialog");
            if (action != null && action.equals("dialog")) {
                exitDialog.show(getSupportFragmentManager());
                return;
            } else if (backPressTime < System.currentTimeMillis() - 2000) {
                Toast.makeText(this, R.string.message_press_back, Toast.LENGTH_SHORT).show();
                backPressTime = System.currentTimeMillis();
                return;
            }
        }
        super.onBackPressed();
    }
    
    @Override
    protected void onPause() {
        if (banner != null) {
            banner.pause();
        }
        super.onPause();
    }
    
    @Override
    protected void onResume() {
        super.onResume();
        if (banner != null) {
            banner.resume();
        }
    }
    
    @Override
    protected void onDestroy() {
        if (banner != null) {
            banner.destroy();
        }
        super.onDestroy();
    }
}