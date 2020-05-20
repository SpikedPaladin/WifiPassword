package me.paladin.wifi;

import android.app.Application;
import android.content.SharedPreferences;

import androidx.preference.PreferenceManager;

import com.google.android.gms.ads.MobileAds;

import me.paladin.wifi.util.ThemeUtil;

public class App extends Application {
    
    @Override
    public void onCreate() {
        super.onCreate();
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        String theme = preferences.getString("application_theme", ThemeUtil.THEME_SYSTEM);
        if (theme != null) ThemeUtil.applyTheme(theme);
        MobileAds.initialize(this);
    }
}
