package reschikov.geekbrains.androidadvancedlevel.weatherapplication.data

import reschikov.geekbrains.androidadvancedlevel.weatherapplication.THREE_HOURS
import reschikov.geekbrains.androidadvancedlevel.weatherapplication.data.database.model.CityTable
import reschikov.geekbrains.androidadvancedlevel.weatherapplication.data.database.model.CurrentTable
import reschikov.geekbrains.androidadvancedlevel.weatherapplication.data.database.model.ForecastTable
import reschikov.geekbrains.androidadvancedlevel.weatherapplication.domain.AppException
import reschikov.geekbrains.androidadvancedlevel.weatherapplication.domain.Place
import reschikov.geekbrains.androidadvancedlevel.weatherapplication.domain.Weather
import reschikov.geekbrains.androidadvancedlevel.weatherapplication.repository.Derivable
import timber.log.Timber

class Repository(private val mapping: Mapping,
                 val storable: Storable,
                 private val requestedWeather: RequestedWeather,
                 private val geocoded: Geocoded,
                 private val issuedCoordinates: IssuedCoordinates) : Derivable{

    /*Добавление по названию города*/
    override suspend fun addPlaceByName(name: String): Place.Places {
        return try {
            updateListPlace(requestedWeather.requestServerByNameAsync(name))
        } catch (e: Throwable) {
            Place.Places(null, e)
        }
    }

    /*Добавление по почтовому индексу*/
    override suspend fun addPlaceByZipCode(postCode: String): Place.Places {
        return try {
            updateListPlace(requestedWeather.requestServerByIndexAsync(postCode))
        } catch (e: Throwable){
            Timber.i(e)
            Place.Places(null, e)
        }
    }

    /*добавление текущего места в список городов*/
    override suspend fun addCurrentPlace(): Place.Places {
        return issuedCoordinates.getCoordinatesCurrentPlace().run {
            coord?.let {
                try {
                    updateListPlace(requestedWeather.requestServerByCoordinatesAsync(it.lat, it.lon))
                } catch (e: Throwable) {
                    Place.Places(null, e)
                }
            } ?: Place.Places(null, error)
        }
    }

    /*Получение погоды текущего места*/
    override suspend fun getStateCurrentPlace(): Weather.Data {
        return issuedCoordinates.getCoordinatesCurrentPlace().run{
            coord?.let {
                try {
                    getWeather(requestedWeather.requestServerByCoordinatesAsync(it.lat, it.lon))
                } catch (e: Throwable) {
                    Weather.Data(null, e)
                }
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
        return try {
            updateListPlace(requestedWeather.requestServerByCoordinatesAsync(lat, lon))
        } catch (e: Exception) {
            Place.Places( null, e)
        }
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

    /*Получение данных погоды из базы, если устаревшие > 3 часов запрос на сервер*/
    override suspend fun getDataWeather(lat: Double, lon: Double): Weather.Data {
        Timber.i("getDataWeather")
        return try {
            val saved = storable.getData(lat, lon)
            takeIf {(System.currentTimeMillis() -  saved.currentTable.dt > THREE_HOURS)}?.let {
                try {
                    getWeather(requestedWeather.requestServerByCoordinatesAsync(lat, lon)).run {
                        weather?.let { return this }
                        error?.let { return Weather.Data(saved, it) }
                    }
                } catch (e: Throwable) {
                    return Weather.Data(saved, e)
                }
            }
            Weather.Data(saved, null)
        } catch (e: Throwable) {
            Weather.Data(null, AppException.Database(e.message))
        }
    }

    /*получение погоды с последнего просмотра*/
    override suspend fun getStateLastPlace(): Weather.Data? {
        Timber.i("getStateLastPlace")
        return  storable.getLastPlace()?.let {
            Timber.i("$it")
            getDataWeather(it.coord.lat, it.coord.lon)
        }
    }

    private suspend fun getWeather(received: Weather.Received): Weather.Data{
        Timber.i("getWeather $received")
        return received.run{
            val currentTable = mapping.createCurrentTable(current)
            val cityTable = mapping.createCityTable(current)
            val forecastsTable = mapping.createListForecastTable(forecastList,current.coord)
            try {
                writeToDatabase(cityTable, currentTable, forecastsTable)
                Weather.Data(Weather.Saved(currentTable, forecastsTable), null)
            } catch (e: Throwable) {
                Weather.Data(null, AppException.Saved(e.message))
            }
        }
    }

    private suspend fun updateListPlace(received: Weather.Received): Place.Places{
        return received.run {
            val lastShowTime = storable.getLastTimeShow()
            val currentTable = mapping.createCurrentTable(current)
            val cityTable = mapping.createCityTable(current, lastShowTime)
            val forecastsTable = mapping.createListForecastTable(forecastList, current.coord)
            try {
                writeToDatabase(cityTable, currentTable, forecastsTable)
                getListCities()
            } catch (e: Throwable) {
                Place.Places(null, AppException.Saved(e.message))
            }
        }
    }

    /*Запись в базу данных*/
    @Throws
    private suspend fun writeToDatabase(cityTable: CityTable, currentTable: CurrentTable, forecastsTable: List<ForecastTable>){
        storable.save(cityTable,currentTable, forecastsTable)
    }
}