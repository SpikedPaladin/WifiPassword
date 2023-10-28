package me.paladin.wifi.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow
import me.paladin.wifi.models.WifiModel

@Dao
interface WifiDao {
    @Query("select * from wifi")
    fun getAll(): Flow<List<WifiModel>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun post(todo: WifiModel)

    @Delete
    fun delete(todo: WifiModel)
}