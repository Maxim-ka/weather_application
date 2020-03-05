package reschikov.geekbrains.androidadvancedlevel.weatherapplication.data

import reschikov.geekbrains.androidadvancedlevel.weatherapplication.data.database.model.CityTable
import reschikov.geekbrains.androidadvancedlevel.weatherapplication.data.database.model.CurrentTable
import reschikov.geekbrains.androidadvancedlevel.weatherapplication.data.database.model.ForecastTable
import reschikov.geekbrains.androidadvancedlevel.weatherapplication.data.network.model.data.openweather.current.Coord

interface Storable {

    suspend fun getLastTimeShow(): Long?
    suspend fun getLastPlace(): CityTable?
    suspend fun getCoordinatePlace(name: String): Coord
    suspend fun getData(lat: Double, lon: Double): Pair<CurrentTable, List<ForecastTable>>
    suspend fun getListCities(): List<CityTable>
    suspend fun save(cityTable: CityTable, currentTable: CurrentTable, forecasts: List<ForecastTable>)
    suspend fun delete(lat: Double, lon: Double): List<CityTable>
}