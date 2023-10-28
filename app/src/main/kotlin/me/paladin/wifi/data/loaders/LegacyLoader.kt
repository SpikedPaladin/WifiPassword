package me.paladin.wifi.data.loaders

import com.topjohnwu.superuser.Shell
import me.paladin.wifi.models.WifiModel

object LegacyLoader {

    fun load(): List<WifiModel> {
        val list = mutableListOf<WifiModel>()
        val content = mutableListOf<String>()
        var currWifi: WifiModel? = null
        val confFiles = listOf("/data/misc/wifi/*_supplicant*.conf", "/data/wifi/bcm_supp.conf")

        for (file in confFiles) {
            Shell.cmd("cat $file").to(content).exec()

            if (content.size > 0)
                break
        }

        for (line in content) {
            if (line.trim().startsWith("network={")) {
                currWifi = WifiModel()
            } else if (line.trim().startsWith("}")) {
                if (currWifi != null) {
                    var ignore = false

                    // Some HTC devices have a fake wifi that should not be printed
                    if (currWifi.ssid.equals("FLAG_FOR_CONFIGURATION_FILE", true))
                        ignore = true

                    if (!ignore)
                        list += currWifi
                }
                currWifi = null
            } else if (currWifi != null) {
                val keyValue = line.split('=', limit = 2)

                if (keyValue.size == 2) {
                    var value = keyValue[1].trim()

                    when (keyValue[0].trim()) {
                        "ssid" -> {
                            value = value.trim('"')
                            currWifi.ssid = value
                        }
                        "password" -> {
                            value = value.trim('"')
                            currWifi.type = WifiModel.TYPE_ENTERPRISE
                            currWifi.password = value
                        }
                        "psk" -> {
                            value = value.trim('"')
                            currWifi.type = WifiModel.TYPE_WPA
                            currWifi.password = value
                        }
                        "identity" -> {
                            value = value.trim('"')
                            currWifi.user = value
                        }
                        "wep_key0" -> {
                            value = value.trim('"')
                            currWifi.user = "1"
                            currWifi.type = WifiModel.TYPE_WEP
                            currWifi.password = value
                        }
                        "wep_key1" -> {
                            value = value.trim('"')
                            currWifi.user = "2"
                            currWifi.type = WifiModel.TYPE_WEP
                            currWifi.password = value
                        }
                        "wep_key2" -> {
                            value = value.trim('"')
                            currWifi.user = "3"
                            currWifi.type = WifiModel.TYPE_WEP
                            currWifi.password = value
                        }
                        "wep_key3" -> {
                            value = value.trim('"')
                            currWifi.user = "4"
                            currWifi.type = WifiModel.TYPE_WEP
                            currWifi.password = value
                        }
                    }
                }
            }
        }

        return list
    }
}