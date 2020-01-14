package reschikov.geekbrains.androidadvancedlevel.weatherapplication.data

import reschikov.geekbrains.androidadvancedlevel.weatherapplication.data.database.model.CityTable
import reschikov.geekbrains.androidadvancedlevel.weatherapplication.data.database.model.CurrentTable
import reschikov.geekbrains.androidadvancedlevel.weatherapplication.data.database.model.ForecastTable
import reschikov.geekbrains.androidadvancedlevel.weatherapplication.domain.BaseException
import reschikov.geekbrains.androidadvancedlevel.weatherapplication.domain.DataWeather

interface Storable {

    suspend fun getLastTimeShow(): Long?
    suspend fun getLastPlace(): CityTable?
    suspend fun getData(lat: Double, lon: Double): DataWeather
    suspend fun getListCities(): List<CityTable>
    suspend fun save(cityTable: CityTable, currentTable: CurrentTable, forecasts: List<ForecastTable>): BaseException?
    suspend fun delete(lat: Double, lon: Double): List<CityTable>
}