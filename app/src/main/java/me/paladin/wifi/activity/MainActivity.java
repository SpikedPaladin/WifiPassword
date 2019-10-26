package me.paladin.wifi.activity;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.view.menu.MenuBuilder;
import androidx.appcompat.widget.SearchView;
import androidx.preference.PreferenceManager;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import me.paladin.wifi.R;
import me.paladin.wifi.adapter.WifiAdapter;
import me.paladin.wifi.adapter.item.WifiItem;
import me.paladin.wifi.dialog.CodeDialog;
import me.paladin.wifi.dialog.ExitDialog;
import me.paladin.wifi.dialog.WifiDialog;

import static me.paladin.wifi.util.XmlUtil.getNodeByAttribute;
import static me.paladin.wifi.util.XmlUtil.getNodeValueByAttribute;
import static me.paladin.wifi.util.XmlUtil.hasNodeAttribute;

public class MainActivity extends AppCompatActivity implements WifiAdapter.ItemClickListener {
    private SharedPreferences preferences;
    private WifiDialog wifiDialog;
    private CodeDialog codeDialog;
    private ExitDialog exitDialog;
    private WifiAdapter adapter;
    private long backPressTime;
    private boolean adLoaded;
    LinearLayout errorLayout;
    MenuItem searchItem;
    RecyclerView list;
    AdView banner;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        preferences = PreferenceManager.getDefaultSharedPreferences(this);
        banner = findViewById(R.id.mainBanner);
        errorLayout = findViewById(R.id.mainError);
        list = findViewById(R.id.mainList);
        if (preferences != null && !preferences.getBoolean("ad_enabled", true)) {
            banner.setVisibility(View.GONE);
            adLoaded = false;
        } else {
            AdRequest request = new AdRequest.Builder().build();
            banner.loadAd(request);
            adLoaded = true;
        }
        list.setLayoutManager(new LinearLayoutManager(this));
        list.addItemDecoration(new DividerItemDecoration(this, LinearLayoutManager.VERTICAL));
        adapter = new WifiAdapter(this);
        adapter.setClickListener(this);
        list.setAdapter(adapter);
        backPressTime = System.currentTimeMillis() - 2000;
        wifiDialog = new WifiDialog(this);
        codeDialog = new CodeDialog(this);
        exitDialog = new ExitDialog(this);
        if (hasRoot()) {
            loadAdapter();
        } else {
            errorLayout.setVisibility(View.VISIBLE);
            list.setVisibility(View.GONE);
        }
    }
    
    @SuppressWarnings("deprecation")
    private boolean hasRoot() {
        boolean root;
        Process suProcess;
        try {
            suProcess = Runtime.getRuntime().exec("su");
            DataOutputStream os = new DataOutputStream(suProcess.getOutputStream());
            DataInputStream osRes = new DataInputStream(suProcess.getInputStream());
            os.writeBytes("id\n");
            os.flush();
            String currUid = osRes.readLine();
            boolean exitSu = false;
            if (currUid == null) {
                root = false;
            } else if (currUid.contains("uid=0")) {
                root = true;
                exitSu = true;
            } else {
                root = false;
                exitSu = true;
            }
            if (exitSu) {
                os.writeBytes("exit\n");
                os.flush();
            }
        } catch (IOException e) {
            root = false;
        }
        return root;
    }
    
    @SuppressWarnings("IfCanBeSwitch")
    private void loadAdapter() {
        adapter.clear();
        adapter.notifyDataSetChanged();
        // Read xmls since oreo
        try {
            Process process = Runtime.getRuntime().exec("su");
            OutputStream out = process.getOutputStream();
            String cmd = "cat /data/misc/wifi/WifiConfigStore.xml";
            out.write(cmd.getBytes());
            out.flush();
            out.close();
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document doc = builder.parse(process.getInputStream());
            Element element = doc.getDocumentElement();
            element.normalize();
            WifiItem currWifi = null;
            NodeList listNet = doc.getElementsByTagName("Network");
            for (int indexNet = 0; indexNet < listNet.getLength(); indexNet++) {
                Node net = listNet.item(indexNet);
                NodeList listConf = ((Element) net).getElementsByTagName("WifiConfiguration");
                for (int i = 0; i < listConf.getLength(); i++) {
                    Node nodeConf = listConf.item(i);
                    if (nodeConf.getNodeType() == Node.ELEMENT_NODE) {
                        if (hasNodeAttribute(nodeConf, "string", "ConfigKey")) {
                            currWifi = new WifiItem();
                            String name = getNodeValueByAttribute(nodeConf, "string", "SSID");
                            name = name.substring(1, name.length() - 1);
                            currWifi.setSsid(name);
                            if (hasNodeAttribute(nodeConf, "string", "PreSharedKey")) {
                                currWifi.setType(WifiItem.TYPE_WPA);
                                String password = getNodeValueByAttribute(nodeConf, "string", "PreSharedKey");
                                password = password.substring(1, password.length() - 1);
                                currWifi.setPassword(password);
                            } else {
                                Node nodeWep = getNodeByAttribute(nodeConf, "string-array", "WEPKeys");
                                if (nodeWep != null) {
                                    currWifi.setType(WifiItem.TYPE_WEP);
                                    NodeList listWepKeys = ((Element) nodeWep).getElementsByTagName("item");
                                    for (int indexWepKey = 0; indexWepKey < listWepKeys.getLength(); indexWepKey++) {
                                        NamedNodeMap temp = listWepKeys.item(indexWepKey).getAttributes();
                                        for (int tempIndex = 0; tempIndex < temp.getLength(); tempIndex++) {
                                            String key = listWepKeys.item(indexWepKey).getAttributes().item(tempIndex).getNodeValue();
                                            if (key.length() > 2) {
                                                key = key.substring(1, key.length() - 1);
                                                currWifi.setPassword(key);
                                                currWifi.setUser((indexWepKey + 1) + "");
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
                NodeList listEnterprise = ((Element) net).getElementsByTagName("WifiEnterpriseConfiguration");
                for (int i = 0; i < listEnterprise.getLength(); i++) {
                    Node nodeEnterprise = listEnterprise.item(i);
                    if (nodeEnterprise.getNodeType() == Node.ELEMENT_NODE && currWifi != null) {
                        String user = getNodeValueByAttribute(nodeEnterprise, "string", "Identity");
                        user = user.substring(1, user.length() - 1);
                        currWifi.setUser(user);
                        String key = getNodeValueByAttribute(nodeEnterprise, "string", "Password");
                        key = key.substring(1, key.length() - 1);
                        currWifi.setPassword(key);
                        currWifi.setType(WifiItem.TYPE_ENTERPRISE);
                    }
                }
                addNewItem(currWifi);
            }
            process.waitFor();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        // Read config files before oreo
        String[] confFiles = { "/data/misc/wifi/*_supplicant*.conf", "/data/wifi/bcm_supp.conf" };
        for (String confFile : confFiles) {
            try {
                Process process = Runtime.getRuntime().exec("su");
                BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
                OutputStream out = process.getOutputStream();
                String cmd = "cat " + confFile;
                out.write(cmd.getBytes());
                out.flush();
                out.close();
                String line = reader.readLine();
                WifiItem currWifi = null;
                while (line != null) {
                    if (line.trim().startsWith("network={")) {
                        currWifi = new WifiItem();
                    } else if (line.trim().startsWith("}")) {
                        if (currWifi != null) {
                            boolean ignore = false;
                            // Some HTC devices have a fake wifi that should not be printed
                            if (currWifi.getSsid().equalsIgnoreCase("FLAG_FOR_CONFIGURATION_FILE")) {
                                ignore = true;
                            }
                            if (!ignore) {
                                addNewItem(currWifi);
                            }
                        }
                        currWifi = null;
                    } else if (currWifi != null) {
                        String[] keyValue = line.split("=", 2);
                        if (keyValue.length == 2) {
                            String value = keyValue[1].trim();
                            String typeValue = keyValue[0].trim();
                            if (typeValue.equals("ssid")) {
                                value = value.substring(1, value.length() - 1);
                                currWifi.setSsid(value);
                            } else if (typeValue.equals("password")) {
                                currWifi.setType(WifiItem.TYPE_ENTERPRISE);
                                value = value.substring(1, value.length() - 1);
                                currWifi.setPassword(value);
                            } else if (typeValue.equals("psk")) {
                                currWifi.setType(WifiItem.TYPE_WPA);
                                value = value.substring(1, value.length() - 1);
                                currWifi.setPassword(value);
                            } else if (typeValue.equals("identity")) {
                                value = value.substring(1, value.length() - 1);
                                currWifi.setUser(value);
                            } else if (typeValue.equals("wep_key0")) {
                                currWifi.setUser("1");
                                currWifi.setType(WifiItem.TYPE_WEP);
                                value = value.substring(1, value.length() - 1);
                                currWifi.setPassword(value);
                            } else if (typeValue.equals("wep_key1")) {
                                currWifi.setUser("2");
                                currWifi.setType(WifiItem.TYPE_WEP);
                                value = value.substring(1, value.length() - 1);
                                currWifi.setPassword(value);
                            } else if (typeValue.equals("wep_key2")) {
                                currWifi.setUser("3");
                                currWifi.setType(WifiItem.TYPE_WEP);
                                value = value.substring(1, value.length() - 1);
                                currWifi.setPassword(value);
                            } else if (typeValue.equals("wep_key3")) {
                                currWifi.setUser("4");
                                currWifi.setType(WifiItem.TYPE_WEP);
                                value = value.substring(1, value.length() - 1);
                                currWifi.setPassword(value);
                            }
                        }
                    }
                    line = reader.readLine();
                }
                process.waitFor();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }
    
    private void addNewItem(WifiItem item) {
        if (item != null && item.getSsid().length() > 0 && item.getPassword().length() > 0) {
            adapter.addItem(item);
        }
        adapter.notifyDataSetChanged();
    }
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (menu instanceof MenuBuilder) {
            ((MenuBuilder) menu).setOptionalIconsVisible(true);
        }
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
                adapter.getFilter().filter(newText);
                return true;
            }
        });
        return true;
    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.item_main_refresh) {
            if (hasRoot() && list.getVisibility() == View.VISIBLE) {
                loadAdapter();
            } else if (hasRoot() && list.getVisibility() == View.GONE) {
                errorLayout.setVisibility(View.GONE);
                list.setVisibility(View.VISIBLE);
                loadAdapter();
            }
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
            Uri uri = Uri.parse("market://details?id=" + getPackageName());
            Intent intent = new Intent(Intent.ACTION_VIEW, uri);
            int flags = Intent.FLAG_ACTIVITY_NO_HISTORY | Intent.FLAG_ACTIVITY_MULTIPLE_TASK;
            if (Build.VERSION.SDK_INT >= 21)
                flags |= Intent.FLAG_ACTIVITY_NEW_DOCUMENT;
            else
                flags |= Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET;
            intent.addFlags(flags);
            try {
                startActivity(intent);
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
                    searchItem.setVisible(false);
                } else {
                    searchItem.setVisible(true);
                }
            }
            if (data.getBooleanExtra("pass", false)) {
                adapter.notifyDataSetChanged();
            }
        }
    }
    
    @Override
    public void onItemClick(View view, int position) {
        wifiDialog.show(getSupportFragmentManager(), adapter.getItem(position), codeDialog);
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