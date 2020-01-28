package reschikov.geekbrains.androidadvancedlevel.weatherapplication.data.database

import reschikov.geekbrains.androidadvancedlevel.weatherapplication.data.Storable
import reschikov.geekbrains.androidadvancedlevel.weatherapplication.data.database.model.CityTable
import reschikov.geekbrains.androidadvancedlevel.weatherapplication.data.database.model.CurrentTable
import reschikov.geekbrains.androidadvancedlevel.weatherapplication.data.database.model.ForecastTable

class DataBaseProvider(private val appDatabase: AppDatabase) : Storable{

    override suspend fun getLastTimeShow(): Long? = appDatabase.weatherDataDao().getLastShowTime()

    override suspend fun getLastPlace(): CityTable? = appDatabase.weatherDataDao().getLastCity()

    @Throws
    override suspend fun getData(lat: Double, lon: Double): Pair<CurrentTable, List<ForecastTable>> {
        return appDatabase.stateWeatherDao().getStateWeather(lat, lon)
    }

    override suspend fun getListCities() = appDatabase.weatherDataDao().getCities()

    @Throws
    override suspend fun save(cityTable: CityTable, currentTable: CurrentTable, forecasts: List<ForecastTable>){
        appDatabase.saveDao().writeToDB(cityTable, currentTable, forecasts)
    }

    override suspend fun delete(lat: Double, lon: Double): List<CityTable> {
        return appDatabase.deleteDao().removeFromDB(lat, lon)
    }
}