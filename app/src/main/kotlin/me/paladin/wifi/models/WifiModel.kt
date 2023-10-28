package me.paladin.wifi.models

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "wifi")
data class WifiModel(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    var ssid: String = "",
    var password: String = "",
    var type: String = "",
    var user: String? = null
) {
    override fun toString(): String {
        var string = "SSID: $ssid\n"

        if (user != null) {
            string += if (type == "WEP")
                "Key-index: $user\n"
            else
                "User: $user\n"
        }

        string += "Password: $password"

        return string
    }

    fun toQrString(): String {
        var string = "WIFI:T:"

        when (type) {
            TYPE_WPA -> string += "WPA"
            TYPE_WEP -> string += "WEP"
        }

        string += ";S:$ssid;P:$password"

        if (type == TYPE_WPA)
            string += ";I:$user"

        string += ";;"

        return string
    }

    companion object {
        const val TYPE_ENTERPRISE = "WPA_EAP"
        const val TYPE_WEP = "WEP"
        const val TYPE_WPA = "WPA_PSK"
    }
}