package me.paladin.wifi.data

import com.topjohnwu.superuser.io.SuFile
import me.paladin.wifi.data.loaders.LegacyLoader
import me.paladin.wifi.data.loaders.ModernLoader
import me.paladin.wifi.models.WifiModel

object RootRepository {
    private const val path1 = "/data/misc/wifi/WifiConfigStore.xml"
    private const val path2 = "/data/misc/apexdata/com.android.wifi/WifiConfigStore.xml"

    fun loadWifi(): List<WifiModel> {
        val list = mutableListOf<WifiModel>()

        list += LegacyLoader.load()

        var file = SuFile.open(path1)
        if (file.exists())
            list += ModernLoader.load(path1)

        file = SuFile.open(path2)
        if (file.exists())
            list += ModernLoader.load(path2)

        return list
    }
}