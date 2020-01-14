package reschikov.geekbrains.androidadvancedlevel.weatherapplication.data

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.withContext
import reschikov.geekbrains.androidadvancedlevel.weatherapplication.THREE_HOURS
import reschikov.geekbrains.androidadvancedlevel.weatherapplication.data.database.model.CityTable
import reschikov.geekbrains.androidadvancedlevel.weatherapplication.data.database.model.CurrentTable
import reschikov.geekbrains.androidadvancedlevel.weatherapplication.data.database.model.ForecastTable
import reschikov.geekbrains.androidadvancedlevel.weatherapplication.domain.BaseException
import reschikov.geekbrains.androidadvancedlevel.weatherapplication.domain.DataWeather
import reschikov.geekbrains.androidadvancedlevel.weatherapplication.domain.Place
import reschikov.geekbrains.androidadvancedlevel.weatherapplication.repository.Derivable

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
    override suspend fun getStateCurrentPlace(): DataWeather {
        return issuedCoordinates.getCoordinatesCurrentPlace().run{
            coord?.let {
                getWeather(requestedWeather.requestServerAsync(it.lat, it.lon))
            } ?: DataWeather.Error(error!!)
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
    override suspend fun getDataWeather(lat: Double, lon: Double): DataWeather {
        return storable.getData(lat, lon).run {
            takeIf { it is DataWeather.Data &&
                System.currentTimeMillis() - it.currentTable.dt > THREE_HOURS }?.let {
                requestedWeather.requestServerAsync(lat, lon).takeIf {
                    it is DataWeather.ServerResponse
                }?.let { getWeather(it)}
            } ?: this
        }
    }

    /*получение погоды с последнего просмотра*/
    override suspend fun getStateLastPlace(): DataWeather? {
        return loadLastPlace()?.let {
            getDataWeather(it.coord.lat, it.coord.lon)
        }
    }

    private suspend fun getWeather(dataWeather: DataWeather): DataWeather{
        return dataWeather.run{
            takeIf { it is  DataWeather.ServerResponse}?.let {
                it as DataWeather.ServerResponse
                val currentTable = mapping.createCurrentTable(it.current)
                val cityTable = mapping.createCityTable(it.current)
                val forecastsTable = mapping.createListForecastTable(it.forecastList,it.current.coord)
                writeToDatabase(cityTable, currentTable, forecastsTable)?.let { baseException ->
                    DataWeather.Error(baseException)
                } ?: DataWeather.Data(currentTable, forecastsTable)
            } ?: this
        }
    }

    private suspend fun updateListPlace(dataWeather: DataWeather): Place.Places{
        return when(dataWeather){
            is DataWeather.ServerResponse ->  dataWeather.run{
                val lastShowTime = storable.getLastTimeShow()
                val currentTable = mapping.createCurrentTable(current)
                val cityTable = mapping.createCityTable(current, lastShowTime)
                val forecastsTable = mapping.createListForecastTable(forecastList, current.coord)
                writeToDatabase(cityTable, currentTable, forecastsTable)?.let {
                    Place.Places(null, it)
                } ?: getListCities()
            }
            else -> Place.Places(null, (dataWeather as DataWeather.Error).error)
        }
    }

    /*Запись в базу данных*/
    private suspend fun writeToDatabase(cityTable: CityTable, currentTable: CurrentTable, forecastsTable: List<ForecastTable>): BaseException?{
        return withContext(Dispatchers.IO + Job()) {
                    storable.save(cityTable,currentTable, forecastsTable)
                }
    }
}