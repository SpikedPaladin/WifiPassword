package me.paladin.wifi.adapter.item;

import androidx.annotation.NonNull;

import java.io.Serializable;

public class WifiItem implements Serializable {
    private String ssid;
    private String password;
    private String user;
    private String type;
    
    public static String TYPE_ENTERPRISE = "802.1x";
    public static String TYPE_WEP = "WEP";
    public static String TYPE_WPA = "WPA/WPA2";
    
    public String getSsid() {
        if (ssid == null) {
            return "";
        }
        return ssid;
    }
    
    public void setSsid(String ssid) {
        this.ssid = ssid;
    }
    
    public String getPassword() {
        if (password == null) {
            return "";
        }
        return password;
    }
    
    public String getProtectedPassword() {
        if (password == null) {
            return "";
        }
        StringBuilder password = new StringBuilder();
        for (int i = 0; i < getPassword().length(); i++) {
            password.append("*");
        }
        return password.toString();
    }
    
    public void setPassword(String password) {
        this.password = password;
    }
    
    public String getUser() {
        if (user == null) {
            return "";
        }
        return user;
    }
    
    public void setUser(String user) {
        this.user = user;
    }
    
    public String getType() {
        if (type == null) {
            return "";
        }
        return type;
    }
    
    public void setType(String type) {
        this.type = type;
    }
    
    @NonNull
    @Override
    public String toString() {
        String separator = System.getProperty("line.separator");
        String result = "";
        result += "SSID: " + getSsid() + separator;
        if (getUser().length() > 0) {
            if (getType().equals(TYPE_ENTERPRISE)) {
                result += "User: ";
            } else if (getType().equals(TYPE_WEP)) {
                result += "Keyindex: ";
            }
            result += getUser() + separator;
        }
        result += "Password: " + getPassword();
        return result;
    }
}