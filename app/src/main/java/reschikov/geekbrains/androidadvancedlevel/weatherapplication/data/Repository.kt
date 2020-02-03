package reschikov.geekbrains.androidadvancedlevel.weatherapplication.data

import reschikov.geekbrains.androidadvancedlevel.weatherapplication.THREE_HOURS
import reschikov.geekbrains.androidadvancedlevel.weatherapplication.data.database.model.CityTable
import reschikov.geekbrains.androidadvancedlevel.weatherapplication.data.database.model.CurrentTable
import reschikov.geekbrains.androidadvancedlevel.weatherapplication.data.database.model.ForecastTable
import reschikov.geekbrains.androidadvancedlevel.weatherapplication.data.network.model.data.openweather.current.Current
import reschikov.geekbrains.androidadvancedlevel.weatherapplication.data.network.model.data.openweather.forecast.ForecastList
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
    override suspend fun addPlaceByName(name: String): Pair<List<Place>?, Throwable?> {
        return try {
            updateListPlace(requestedWeather.requestServerByNameAsync(name))
        } catch (e: Throwable) {
            Pair(null, e)
        }
    }

    /*Добавление по почтовому индексу*/
    override suspend fun addPlaceByZipCode(postCode: String): Pair<List<Place>?, Throwable?> {
        return try {
            updateListPlace(requestedWeather.requestServerByIndexAsync(postCode))
        } catch (e: Throwable){
            Pair(null, e)
        }
    }

    /*добавление текущего места в список городов*/
    override suspend fun addCurrentPlace(): Pair<List<Place>?, Throwable?> {
        return issuedCoordinates.getCoordinatesCurrentPlace().run {
            first?.let {
                try {
                    updateListPlace(requestedWeather.requestServerByCoordinatesAsync(it.latitude, it.longitude))
                } catch (e: Throwable) {
                    Pair(null, e)
                }
            } ?: Pair(null, second)
        }
    }

    /*Получение погоды текущего места*/
    override suspend fun getStateCurrentPlace(): Weather {
        return issuedCoordinates.getCoordinatesCurrentPlace().run{
            first?.let {
                try {
                    getWeather(requestedWeather.requestServerByCoordinatesAsync(it.latitude, it.longitude))
                } catch (e: Throwable) {
                    Weather(null, e)
                }
            } ?: Weather(null, second)
        }
    }

    /*добавление места в список городов через запрос прямого геокодирования*/
    override suspend fun determineLocationCoordinates(place: String, code: String): Pair<List<Place>?, Throwable?> {
        return try {
            Pair(mapping.createListPlaceResult(geocoded.requestDirectGeocoding(place, code)), null)
        } catch (e: Throwable) {
            Pair( null, e)
        }
    }

    /*Получение данных с сервиса погоды по выбранному варианту из геокодирования*/
    override suspend fun addSelectedPlace(lat: Double, lon: Double): Pair<List<Place>?, Throwable?> {
        return try {
            updateListPlace(requestedWeather.requestServerByCoordinatesAsync(lat, lon))
        } catch (e: Exception) {
            Pair( null, e)
        }
    }

    /*Получение списка городов из базы*/
    override suspend fun getListCities(): Pair<List<Place>?, Throwable?> {
        return try {
            Pair(mapping.createListPlaceCity(storable.getListCities()), null)
        } catch (e: Throwable){
            Pair(null, e)
        }
    }

    /*удаление города из базы данных и возврат обновленного списка*/
    override suspend fun deletePlace(lat: Double, lon: Double): Pair<List<Place>?, Throwable?> {
        return try {
            Pair(mapping.createListPlaceCity(storable.delete(lat, lon)), null)
        } catch (e: Throwable){
            Pair(null, e)
        }
    }

    /*Получение данных погоды из базы, если устаревшие > 3 часов запрос на сервер*/
    override suspend fun getDataWeather(lat: Double, lon: Double): Weather {
        return try {
            val saved = storable.getData(lat, lon)
            takeIf {(System.currentTimeMillis() -  saved.first.dt > THREE_HOURS)}?.let {
                try {
                    getWeather(requestedWeather.requestServerByCoordinatesAsync(lat, lon)).run {
                        state?.let { this } ?: Weather(saved, error)
                    }
                } catch (e: Throwable) {
                    Weather(saved, e)
                }
            } ?: Weather(saved, null)
        } catch (e: Throwable) {
            Weather(null, AppException.Database(e.message))
        }
    }

    /*получение погоды с последнего просмотра*/
    override suspend fun getStateLastPlace(): Weather? {
        return  storable.getLastPlace()?.let {
            Timber.i("$it")
            getDataWeather(it.coord.lat, it.coord.lon)
        }
    }

    private suspend fun getWeather(received: Pair<Current, ForecastList>): Weather{
        return transform(received, null).run{
            try {
                writeToDatabase(first, second, third)
                Weather(Pair(second, third), null)
            } catch (e: Throwable) {
                Weather(null, AppException.Saved(e.message))
            }
        }
    }

    private suspend fun updateListPlace(received: Pair<Current, ForecastList>): Pair<List<Place>?, Throwable?>{
        return transform(received, storable.getLastTimeShow()).run {
                try {
                    writeToDatabase(first, second, third)
                    getListCities()
                } catch (e: Throwable) {
                    Pair(null, AppException.Saved(e.message))
                }
            }
    }

    private fun transform(received: Pair<Current, ForecastList>, lastShowTime: Long?): Triple<CityTable, CurrentTable, List<ForecastTable>>{
        return received.run {
            Triple(
                mapping.createCityTable(first, lastShowTime),
                mapping.createCurrentTable(first),
                mapping.createListForecastTable(second, first)
            )
        }
    }

    /*Запись в базу данных*/
    @Throws
    private suspend fun writeToDatabase(cityTable: CityTable, currentTable: CurrentTable, forecastsTable: List<ForecastTable>){
        storable.save(cityTable,currentTable, forecastsTable)
    }
}