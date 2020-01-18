package reschikov.geekbrains.androidadvancedlevel.weatherapplication.data.database

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction
import reschikov.geekbrains.androidadvancedlevel.weatherapplication.data.database.model.CurrentTable
import reschikov.geekbrains.androidadvancedlevel.weatherapplication.data.database.model.ForecastTable
import reschikov.geekbrains.androidadvancedlevel.weatherapplication.domain.Weather

@Dao
abstract class StateWeatherDao {

    @Query("SELECT * FROM currenttable WHERE lon= :lon AND lat= :lat")
    abstract fun getCurrentState(lat: Double, lon: Double): CurrentTable
    @Query("SELECT * FROM forecasttable WHERE lon= :lon AND lat= :lat")
    abstract fun getForecastList(lat: Double, lon: Double): List<ForecastTable>
    @Query("UPDATE citytable SET showTime= :showTime WHERE lon= :lon AND lat= :lat")
    abstract fun setTimeShow(lat: Double, lon: Double, showTime: Long)

    @Transaction
    open fun getStateWeather(lat: Double, lon: Double): Weather.Saved{
        val currentTable = getCurrentState(lat, lon)
        val forecasts = getForecastList(lat, lon)
        setTimeShow(lat, lon, System.currentTimeMillis())
        return Weather.Saved(currentTable, forecasts)
    }
}