package reschikov.geekbrains.androidadvancedlevel.weatherapplication.data.database

import androidx.room.Database
import androidx.room.RoomDatabase
import reschikov.geekbrains.androidadvancedlevel.weatherapplication.data.database.model.CityTable
import reschikov.geekbrains.androidadvancedlevel.weatherapplication.data.database.model.CurrentTable
import reschikov.geekbrains.androidadvancedlevel.weatherapplication.data.database.model.ForecastTable

@Database(entities = [CurrentTable::class, ForecastTable::class, CityTable::class], version = 2,
        exportSchema = false)
abstract class AppDatabase : RoomDatabase() {

    abstract fun weatherDataDao(): WeatherDataDao

    abstract fun saveDao(): SaveDao

    abstract fun stateWeatherDao(): StateWeatherDao

    abstract fun deleteDao(): DeleteDao
}