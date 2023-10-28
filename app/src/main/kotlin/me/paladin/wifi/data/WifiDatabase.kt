package me.paladin.wifi.data

import androidx.room.Database
import androidx.room.RoomDatabase
import me.paladin.wifi.data.dao.WifiDao
import me.paladin.wifi.models.WifiModel

@Database(entities = [WifiModel::class], version = 1, exportSchema = false)
abstract class WifiDatabase : RoomDatabase() {
    abstract fun wifiDao(): WifiDao

    companion object {
        const val NAME = "wifi"
    }
}