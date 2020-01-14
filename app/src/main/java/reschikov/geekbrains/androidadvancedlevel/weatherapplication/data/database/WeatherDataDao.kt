package reschikov.geekbrains.androidadvancedlevel.weatherapplication.data.database

import androidx.room.Dao
import androidx.room.Query
import reschikov.geekbrains.androidadvancedlevel.weatherapplication.data.database.model.CityTable

@Dao
interface WeatherDataDao {

    @Query("SELECT * FROM citytable WHERE showTime = (SELECT MAX(showTime) FROM citytable)")
    suspend fun getLastCity(): CityTable?

    @Query("SELECT * FROM citytable ORDER BY showTime DESC")
    suspend fun getCities(): List<CityTable>

    @Query("SELECT MAX(showTime) FROM citytable")
    suspend fun getLastShowTime(): Long?
}