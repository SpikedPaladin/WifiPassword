package me.paladin.wifi.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.ArrayList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import me.paladin.wifi.R;
import me.paladin.wifi.adapter.WifiAdapter;
import me.paladin.wifi.dialog.WifiDialog;
import me.paladin.wifi.model.WifiItem;

import static me.paladin.wifi.util.XmlUtil.getNodeByAttribute;
import static me.paladin.wifi.util.XmlUtil.getNodeValueByAttribute;
import static me.paladin.wifi.util.XmlUtil.hasNodeAttribute;

public class SystemFragment extends Fragment implements WifiAdapter.ItemClickListener {
    private ArrayList<WifiItem> items = new ArrayList<>();
    private WifiDialog wifiDialog;
    private LinearLayout error;
    private RecyclerView list;
    private boolean rooted;
    
    public WifiAdapter adapter;
    
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_system, container, false);
    }
    
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        list = view.findViewById(android.R.id.list);
        error = view.findViewById(R.id.mainError);
        list.setLayoutManager(new LinearLayoutManager(getContext()));
        if (getContext() != null) list.addItemDecoration(new DividerItemDecoration(getContext(), LinearLayoutManager.VERTICAL));
        adapter = new WifiAdapter(getContext());
        adapter.setClickListener(this);
        adapter.setItems(items);
        list.setAdapter(adapter);
        wifiDialog = new WifiDialog();
        setHasOptionsMenu(true);
        if (savedInstanceState == null) {
            rooted = isRooted();
            if (rooted) {
                loadAdapter();
            } else {
                list.setVisibility(View.GONE);
                error.setVisibility(View.VISIBLE);
            }
        } else {
            rooted = savedInstanceState.getBoolean("rooted");
            if (rooted) {
                ArrayList<WifiItem> savedItems = savedInstanceState.getParcelableArrayList("items");
                if (savedItems != null) {
                    items.addAll(savedItems);
                    adapter.notifyDataSetChanged();
                }
            } else {
                list.setVisibility(View.GONE);
                error.setVisibility(View.VISIBLE);
            }
        }
    }
    
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.item_main_refresh) {
            if (isRooted() && list.getVisibility() == View.VISIBLE) {
                loadAdapter();
            } else if (isRooted() && list.getVisibility() == View.GONE) {
                rooted = true;
                list.setVisibility(View.VISIBLE);
                error.setVisibility(View.GONE);
                loadAdapter();
            }
        }
        return super.onOptionsItemSelected(item);
    }
    
    /**
     * Check if device is rooted
     * @return true if device rooted
     */
    private boolean isRooted() {
        boolean root;
        Process suProcess;
        try {
            suProcess = Runtime.getRuntime().exec("su");
            DataOutputStream os = new DataOutputStream(suProcess.getOutputStream());
            BufferedReader osRes = new BufferedReader(new InputStreamReader(suProcess.getInputStream()));
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
                            switch (typeValue) {
                                case "ssid":
                                    value = value.substring(1, value.length() - 1);
                                    currWifi.setSsid(value);
                                    break;
                                case "password":
                                    currWifi.setType(WifiItem.TYPE_ENTERPRISE);
                                    value = value.substring(1, value.length() - 1);
                                    currWifi.setPassword(value);
                                    break;
                                case "psk":
                                    currWifi.setType(WifiItem.TYPE_WPA);
                                    value = value.substring(1, value.length() - 1);
                                    currWifi.setPassword(value);
                                    break;
                                case "identity":
                                    value = value.substring(1, value.length() - 1);
                                    currWifi.setUser(value);
                                    break;
                                case "wep_key0":
                                    currWifi.setUser("1");
                                    currWifi.setType(WifiItem.TYPE_WEP);
                                    value = value.substring(1, value.length() - 1);
                                    currWifi.setPassword(value);
                                    break;
                                case "wep_key1":
                                    currWifi.setUser("2");
                                    currWifi.setType(WifiItem.TYPE_WEP);
                                    value = value.substring(1, value.length() - 1);
                                    currWifi.setPassword(value);
                                    break;
                                case "wep_key2":
                                    currWifi.setUser("3");
                                    currWifi.setType(WifiItem.TYPE_WEP);
                                    value = value.substring(1, value.length() - 1);
                                    currWifi.setPassword(value);
                                    break;
                                case "wep_key3":
                                    currWifi.setUser("4");
                                    currWifi.setType(WifiItem.TYPE_WEP);
                                    value = value.substring(1, value.length() - 1);
                                    currWifi.setPassword(value);
                                    break;
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
    
    /**
     * Add new item to adapter and call {@link RecyclerView.Adapter#notifyDataSetChanged()}
     * @param item item to add
     */
    private void addNewItem(WifiItem item) {
        if (item != null && item.getSsid().length() > 0 && item.getPassword().length() > 0) {
            adapter.addItem(item);
        }
        adapter.notifyDataSetChanged();
    }
    
    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean("rooted", rooted);
        outState.putParcelableArrayList("items", items);
    }
    
    @Override
    public void onItemClick(View view, int position) {
        wifiDialog.show(getFragmentManager(), adapter.getItem(position));
    }
}
