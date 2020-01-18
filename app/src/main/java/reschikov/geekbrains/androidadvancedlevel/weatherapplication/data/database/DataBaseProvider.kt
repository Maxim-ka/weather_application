package reschikov.geekbrains.androidadvancedlevel.weatherapplication.data.database

import reschikov.geekbrains.androidadvancedlevel.weatherapplication.data.Storable
import reschikov.geekbrains.androidadvancedlevel.weatherapplication.data.database.model.CityTable
import reschikov.geekbrains.androidadvancedlevel.weatherapplication.data.database.model.CurrentTable
import reschikov.geekbrains.androidadvancedlevel.weatherapplication.data.database.model.ForecastTable
import reschikov.geekbrains.androidadvancedlevel.weatherapplication.domain.BaseException
import reschikov.geekbrains.androidadvancedlevel.weatherapplication.domain.Weather

class DataBaseProvider(private val appDatabase: AppDatabase) : Storable {

    override suspend fun getLastTimeShow(): Long? = appDatabase.weatherDataDao().getLastShowTime()

    override suspend fun getLastPlace(): CityTable? = appDatabase.weatherDataDao().getLastCity()

    override suspend fun getData(lat: Double, lon: Double): Weather.Data {
        return try {
                    Weather.Data(appDatabase.stateWeatherDao().getStateWeather(lat, lon), null)
                } catch (e: Throwable){
                    Weather.Data(null, BaseException.Database(e.message))
                }
    }

    override suspend fun getListCities() = appDatabase.weatherDataDao().getCities()

    override suspend fun save(cityTable: CityTable, currentTable: CurrentTable, forecasts: List<ForecastTable>): BaseException? {
        var baseException: BaseException? = null
        try {
            appDatabase.saveDao().writeToDB(cityTable, currentTable, forecasts)
        } catch (e: Throwable) {
            baseException = BaseException.Saved(e.message)
        }finally {
            return baseException
        }
    }

    override suspend fun delete(lat: Double, lon: Double): List<CityTable> {
        return appDatabase.deleteDao().removeFromDB(lat, lon)
    }
}