package reschikov.geekbrains.androidadvancedlevel.weatherapplication.data

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.withContext
import reschikov.geekbrains.androidadvancedlevel.weatherapplication.THREE_HOURS
import reschikov.geekbrains.androidadvancedlevel.weatherapplication.data.database.model.CityTable
import reschikov.geekbrains.androidadvancedlevel.weatherapplication.data.database.model.CurrentTable
import reschikov.geekbrains.androidadvancedlevel.weatherapplication.data.database.model.ForecastTable
import reschikov.geekbrains.androidadvancedlevel.weatherapplication.domain.BaseException
import reschikov.geekbrains.androidadvancedlevel.weatherapplication.domain.Place
import reschikov.geekbrains.androidadvancedlevel.weatherapplication.domain.Weather
import reschikov.geekbrains.androidadvancedlevel.weatherapplication.repository.Derivable
import timber.log.Timber

class Repository(private val mapping: Mapping,
                 val storable: Storable,
                 private val requestedWeather: RequestedWeather,
                 private val geocoded: Geocoded,
                 private val issuedCoordinates: IssuedCoordinates) : Derivable{

    /*добавление текущего места в список городов*/
    override suspend fun addCurrentPlace(): Place.Places {
        return issuedCoordinates.getCoordinatesCurrentPlace().run {
            coord?.let {
                updateListPlace(requestedWeather.requestServerAsync(it.lat, it.lon))
            } ?: Place.Places(null, error)
        }
    }

    /*Получение погоды текущего места*/
    override suspend fun getStateCurrentPlace(): Weather.Data {
        return issuedCoordinates.getCoordinatesCurrentPlace().run{
            coord?.let {
                getWeather(requestedWeather.requestServerAsync(it.lat, it.lon))
            } ?: Weather.Data(null, error)
        }
    }

    /*добавление места в список городов через запрос прямого геокодирования*/
    override suspend fun determineLocationCoordinates(place: String, code: String): Place.Places  {
        return try {
            Place.Places(mapping.createListPlaceResult(geocoded.requestDirectGeocoding(place, code)), null)
        } catch (e: Throwable) {
            Place.Places( null, e)
        }
    }

    /*Получение данных с сервиса погоды по выбранному варианту из геокодирования*/
    override suspend fun addSelectedPlace(lat: Double, lon: Double): Place.Places {
        return updateListPlace(requestedWeather.requestServerAsync(lat, lon))
    }

    /*Получение списка городов из базы*/
    override suspend fun getListCities(): Place.Places {
        return try {
            Place.Places(mapping.createListPlaceCity(storable.getListCities()), null)
        } catch (e: Throwable){
            Place.Places(null, e)
        }
    }

    /*удаление города из базы данных и возврат обновленного списка*/
    override suspend fun deletePlace(lat: Double, lon: Double): Place.Places {
        return try {
            Place.Places(mapping.createListPlaceCity(storable.delete(lat, lon)), null)
        } catch (e: Throwable){
            Place.Places(null, e)
        }
    }

    /*Загрузка последнего просмотренного города*/
    override suspend fun loadLastPlace(): CityTable? = storable.getLastPlace()

    /*Получение данных погоды из базы, если устаревшие > 3 часов запрос на сервер*/
    override suspend fun getDataWeather(lat: Double, lon: Double): Weather.Data {
        return storable.getData(lat, lon).apply {
            weather?.let {saved ->
                takeIf {saved is Weather.Saved && (System.currentTimeMillis() -  saved.currentTable.dt > THREE_HOURS)
                }?.let { requestedWeather.requestServerAsync(lat, lon).also {received ->
                        received.weather?.let { getWeather(received) }
                        received.error?.let { error = it }
                    }
                }
            }
        }
    }

    /*получение погоды с последнего просмотра*/
    override suspend fun getStateLastPlace(): Weather.Data? {
        return loadLastPlace()?.let {
            Timber.i("LastPlace $it")
            getDataWeather(it.coord.lat, it.coord.lon)
        }
    }

    private suspend fun getWeather(data: Weather.Data): Weather.Data{
        return data.apply{
            weather?.let {
                it as Weather.Received
                val currentTable = mapping.createCurrentTable(it.current)
                val cityTable = mapping.createCityTable(it.current)
                val forecastsTable = mapping.createListForecastTable(it.forecastList,it.current.coord)
                writeToDatabase(cityTable, currentTable, forecastsTable)?.let { baseException ->
                    weather = null
                    error = baseException
                } ?: apply { weather = Weather.Saved(currentTable, forecastsTable) }
            }
        }
    }

    private suspend fun updateListPlace(data: Weather.Data): Place.Places{
        return data.run {
            weather?.let {
                it as Weather.Received
                val lastShowTime = storable.getLastTimeShow()
                val currentTable = mapping.createCurrentTable(it.current)
                val cityTable = mapping.createCityTable(it.current, lastShowTime)
                val forecastsTable = mapping.createListForecastTable(it.forecastList, it.current.coord)
                writeToDatabase(cityTable, currentTable, forecastsTable)?.let {baseException ->
                    Place.Places(null, baseException)
                } ?: getListCities()
            } ?: Place.Places(null, error)
        }
    }

    /*Запись в базу данных*/
    private suspend fun writeToDatabase(cityTable: CityTable, currentTable: CurrentTable, forecastsTable: List<ForecastTable>): BaseException?{
        return withContext(Dispatchers.IO + Job()) {
                    storable.save(cityTable,currentTable, forecastsTable)
                }
    }
}