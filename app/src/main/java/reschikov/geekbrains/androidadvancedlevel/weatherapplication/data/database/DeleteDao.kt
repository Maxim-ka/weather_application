package reschikov.geekbrains.androidadvancedlevel.weatherapplication.data.database

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction
import reschikov.geekbrains.androidadvancedlevel.weatherapplication.data.database.model.CityTable

@Dao
abstract class DeleteDao {

    @Query("DELETE FROM citytable WHERE lon= :lon AND lat= :lat")
    abstract fun deleteCity(lat: Double, lon: Double)
    @Query("SELECT * FROM citytable ORDER BY showTime DESC")
    abstract fun getCities(): List<CityTable>

    @Transaction
    open fun removeFromDB(lat: Double, lon: Double): List<CityTable>{
        deleteCity(lat, lon)
        return getCities()
    }
}