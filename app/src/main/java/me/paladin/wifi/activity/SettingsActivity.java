package me.paladin.wifi.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.ListPreference;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.PreferenceManager;

import me.paladin.wifi.R;
import me.paladin.wifi.util.ThemeUtil;

public class SettingsActivity extends AppCompatActivity {
    private boolean startAds, startSearch, startPass;
    private SharedPreferences preferences;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings_activity);
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.settings, new SettingsFragment())
                .commit();
        preferences = PreferenceManager.getDefaultSharedPreferences(this);
        startAds = preferences.getBoolean("ad_enabled", true);
        startSearch = preferences.getBoolean("search_enabled", true);
        startPass = preferences.getBoolean("show_passwords", true);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }
    
    @Override
    public void onBackPressed() {
        Intent intent = new Intent();
        if (startAds != preferences.getBoolean("ad_enabled", true)) {
            intent.putExtra("ads", true);
        }
        if (startSearch != preferences.getBoolean("search_enabled", true)) {
            intent.putExtra("search", true);
        }
        if (startPass != preferences.getBoolean("show_passwords", true)) {
            intent.putExtra("pass", true);
        }
        if (intent.getExtras() != null) {
            setResult(RESULT_OK, intent);
        }
        super.onBackPressed();
    }
    
    public static class SettingsFragment extends PreferenceFragmentCompat {
        @Override
        public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
            setPreferencesFromResource(R.xml.preferences, rootKey);
    
            ListPreference themeList = findPreference("application_theme");
            if (themeList != null) {
                themeList.setOnPreferenceChangeListener(
                        new Preference.OnPreferenceChangeListener() {
                            @Override
                            public boolean onPreferenceChange(Preference preference, Object newValue) {
                                ThemeUtil.applyTheme((String) newValue);
                                return true;
                            }
                        }
                );
            }
        }
    }
}