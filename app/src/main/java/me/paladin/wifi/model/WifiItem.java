package me.paladin.wifi.model;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

public class WifiItem implements Parcelable {
    private String ssid;
    private String password;
    private String user;
    private String type;
    
    public static String TYPE_ENTERPRISE = "802.1x";
    public static String TYPE_WEP = "WEP";
    public static String TYPE_WPA = "WPA/WPA2";
    
    public WifiItem() {
    
    }
    
    private WifiItem(Parcel in) {
        ssid = in.readString();
        password = in.readString();
        user = in.readString();
        type = in.readString();
    }
    
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
    
    @Override
    public int describeContents() {
        return 0;
    }
    
    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(ssid);
        dest.writeString(password);
        dest.writeString(user);
        dest.writeString(type);
    }
    
    public static final Creator<WifiItem> CREATOR = new Creator<WifiItem>() {
        @Override
        public WifiItem createFromParcel(Parcel in) {
            return new WifiItem(in);
        }
        
        @Override
        public WifiItem[] newArray(int size) {
            return new WifiItem[size];
        }
    };
    
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