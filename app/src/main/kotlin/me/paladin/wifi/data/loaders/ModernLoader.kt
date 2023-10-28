package me.paladin.wifi.data.loaders

import com.topjohnwu.superuser.Shell
import me.paladin.wifi.models.WifiModel
import org.jsoup.Jsoup
import org.jsoup.parser.Parser

object ModernLoader {

    fun load(path: String): List<WifiModel> {
        val list = mutableListOf<WifiModel>()
        val content = mutableListOf<String>()
        Shell.cmd("cat $path").to(content).exec()

        val doc = Jsoup.parse(content.joinToString(separator = ""), Parser.xmlParser())
        var currWifi: WifiModel? = null

        doc.getElementsByTag("Network").forEach { networkElement ->
            networkElement.getElementsByTag("WifiConfiguration").forEach { wifiElement ->
                if (wifiElement.getElementsByAttributeValue("name", "ConfigKey")[0] != null) {
                    var name = wifiElement.getElementsByAttributeValue("name", "SSID")[0].text()
                    name = name.trim('"')

                    currWifi = WifiModel(ssid = name)

                    val preSharedKey = wifiElement.getElementsByAttributeValue("name", "PreSharedKey")[0]

                    if (preSharedKey.tagName() == "string") {
                        var password = preSharedKey.text()
                        password = password.trim('"')

                        currWifi!!.password = password
                        currWifi!!.type = WifiModel.TYPE_WPA
                    } else {
                        val wep = wifiElement.getElementsByAttributeValue("name", "WEPKeys")[0]
                        if (wep.tagName() == "string-array") {
                            val keyIndex = wifiElement.getElementsByAttributeValue("name", "WEPTxKeyIndex").attr("value").toInt()
                            var password = wep.getElementsByTag("item")[keyIndex].attr("value")
                            password = password.trim('"')

                            currWifi!!.password = password
                            currWifi!!.user = "${keyIndex + 1}"
                            currWifi!!.type = WifiModel.TYPE_WEP
                        }
                    }
                }
            }
            networkElement.getElementsByTag("WifiEnterpriseConfiguration").forEach { enterpriseElement ->
                if (currWifi != null) {
                    var user = enterpriseElement.getElementsByAttributeValue("name", "Identity").text()
                    user = user.trim('"')

                    var password = enterpriseElement.getElementsByAttributeValue("name", "Password").text()
                    password = password.trim('"')

                    currWifi!!.password = password
                    currWifi!!.user = user
                    currWifi!!.type = WifiModel.TYPE_ENTERPRISE
                }
            }

            if (currWifi != null)
                list += currWifi!!
        }

        return list
    }
}