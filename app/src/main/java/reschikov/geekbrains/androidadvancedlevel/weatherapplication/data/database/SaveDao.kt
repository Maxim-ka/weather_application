package reschikov.geekbrains.androidadvancedlevel.weatherapplication.data.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Transaction
import reschikov.geekbrains.androidadvancedlevel.weatherapplication.data.database.model.CityTable
import reschikov.geekbrains.androidadvancedlevel.weatherapplication.data.database.model.CurrentTable
import reschikov.geekbrains.androidadvancedlevel.weatherapplication.data.database.model.ForecastTable

@Dao
abstract class SaveDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract fun insertCity(cityTable: CityTable)
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract fun insertCurrent(currentTable: CurrentTable)
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract fun insertForecastList(list: List<ForecastTable>)

    @Transaction
    open fun writeToDB(cityTable: CityTable, currentTable: CurrentTable, list: List<ForecastTable>){
        insertCity(cityTable)
        insertCurrent(currentTable)
        insertForecastList(list)
    }
}