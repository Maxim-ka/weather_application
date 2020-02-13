package reschikov.geekbrains.androidadvancedlevel.weatherapplication.data

import org.koin.core.KoinComponent
import org.koin.core.qualifier.named
import reschikov.geekbrains.androidadvancedlevel.weatherapplication.SCOPE_GEO
import reschikov.geekbrains.androidadvancedlevel.weatherapplication.SCOPE_LOCAL
import reschikov.geekbrains.androidadvancedlevel.weatherapplication.SCOPE_WEATHER
import reschikov.geekbrains.androidadvancedlevel.weatherapplication.THREE_HOURS
import reschikov.geekbrains.androidadvancedlevel.weatherapplication.data.database.model.CityTable
import reschikov.geekbrains.androidadvancedlevel.weatherapplication.data.database.model.CurrentTable
import reschikov.geekbrains.androidadvancedlevel.weatherapplication.data.database.model.ForecastTable
import reschikov.geekbrains.androidadvancedlevel.weatherapplication.data.network.Requested
import reschikov.geekbrains.androidadvancedlevel.weatherapplication.data.network.Closable
import reschikov.geekbrains.androidadvancedlevel.weatherapplication.data.network.model.data.openweather.current.Current
import reschikov.geekbrains.androidadvancedlevel.weatherapplication.data.network.model.data.openweather.forecast.ForecastList
import reschikov.geekbrains.androidadvancedlevel.weatherapplication.data.network.request.command.GetByCoordinates
import reschikov.geekbrains.androidadvancedlevel.weatherapplication.domain.AppException
import reschikov.geekbrains.androidadvancedlevel.weatherapplication.domain.Place
import reschikov.geekbrains.androidadvancedlevel.weatherapplication.domain.Weather
import reschikov.geekbrains.androidadvancedlevel.weatherapplication.repository.Derivable
import timber.log.Timber

class Repository(private val storable: Storable,
                 private val mapping: Mapping,
                 private var requestedWeather: RequestedWeather? = null,
                 private var geocoded: Geocoded? = null,
                 private var issuedCoordinates: IssuedCoordinates? = null)
    : Derivable, KoinComponent{
     
    
    /*Добавление города*/
    override suspend fun addPlace(requested: Requested): Pair<List<Place>?, Throwable?> {
        return try {
            updateListPlace(getRequestedWeather().requestServer(requested))
        } catch (e: Throwable) {
            Pair(null, e)
        }
    }

    private fun getRequestedWeather() : RequestedWeather{
        return requestedWeather?.let { it } ?: run{
            requestedWeather = getKoin().getOrCreateScope(SCOPE_WEATHER, named(SCOPE_WEATHER)).get()
            requestedWeather as RequestedWeather
        }
    }

    /*добавление текущего места в список городов*/
    override suspend fun addCurrentPlace(): Pair<List<Place>?, Throwable?> {
        return getIssuedCoordinates().getCoordinatesCurrentPlace().run {
            first?.let {
                try {
                    updateListPlace(getRequestedWeather().requestServer(it))
                } catch (e: Throwable) {
                    Pair(null, e)
                }
            } ?: Pair(null, second)
        }
    }

    /*Получение погоды текущего места*/
    override suspend fun getStateCurrentPlace(): Weather {
        return getIssuedCoordinates().getCoordinatesCurrentPlace().run{
            first?.let {
                try {
                    getWeather(getRequestedWeather().requestServer(it))
                } catch (e: Throwable) {
                    Weather(null, e)
                }
            } ?: Weather(null, second)
        }
    }

    private fun getIssuedCoordinates() : IssuedCoordinates{
        return issuedCoordinates?.let { it } ?: run {
            issuedCoordinates = getKoin().getOrCreateScope(SCOPE_LOCAL, named(SCOPE_LOCAL)).get()
            issuedCoordinates as IssuedCoordinates
        }
    }

    /*получение списка вариантов мест через запрос прямого геокодирования*/
    override suspend fun determineLocationCoordinates(place: String, code: String): Pair<List<Place>?, Throwable?> {
        return try {
            Pair(mapping.createListPlaceResult(getGeocoded().requestDirectGeocoding(place, code)), null)
        } catch (e: Throwable) {
            Pair( null, e)
        }
    }

    private fun getGeocoded() : Geocoded{
        return geocoded?.let { it  } ?: run {
            geocoded = getKoin().getOrCreateScope(SCOPE_GEO, named(SCOPE_GEO)).get()
            geocoded as Geocoded
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
            Pair(null, AppException.Database(e.message))
        }
    }

    /*Получение данных погоды из базы, если устаревшие > 3 часов запрос на сервер*/
    override suspend fun getDataWeather(lat: Double, lon: Double): Weather {
        return try {
            val saved = storable.getData(lat, lon)
            takeIf {(System.currentTimeMillis() -  saved.first.dt > THREE_HOURS)}?.let {
                try {
                    getWeather(getRequestedWeather().requestServer(GetByCoordinates(lat, lon))).run {
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
        closeModules()
    }

    private fun closeModules(){
        issuedCoordinates?.let {
            (it as Closable).toClose()
            issuedCoordinates = null
        }
        geocoded = null
    }
}