package reschikov.geekbrains.androidadvancedlevel.weatherapplication.data.network

import android.content.Context
import android.content.Context.MODE_PRIVATE
import android.content.SharedPreferences
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import reschikov.geekbrains.androidadvancedlevel.weatherapplication.BuildConfig
import reschikov.geekbrains.androidadvancedlevel.weatherapplication.KEY_WEATHER
import reschikov.geekbrains.androidadvancedlevel.weatherapplication.PREFERENCE_OPEN_WEATHER
import reschikov.geekbrains.androidadvancedlevel.weatherapplication.data.RequestedWeather
import reschikov.geekbrains.androidadvancedlevel.weatherapplication.data.network.model.data.openweather.current.Current
import reschikov.geekbrains.androidadvancedlevel.weatherapplication.data.network.model.data.openweather.forecast.ForecastList
import reschikov.geekbrains.androidadvancedlevel.weatherapplication.data.network.request.OpenweathermapCurrent
import reschikov.geekbrains.androidadvancedlevel.weatherapplication.data.network.request.OpenweathermapForecast
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import kotlin.coroutines.Continuation
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

private const val URL_OPEN_WEATHER_MAP = "https://api.openweathermap.org/data/2.5/"
private const val UNITS_METRIC = "metric"
/*FiXME применить шаблон команда*/
class WeatherServiceProvider(context: Context) : RequestBaseProvider(context),
        RequestedWeather {

    private val requestCurrent: OpenweathermapCurrent
    private val requestForecast: OpenweathermapForecast
    private val sp: SharedPreferences
    private lateinit var key: String

    init {
        createClient(URL_OPEN_WEATHER_MAP)
        sp = context.getSharedPreferences(PREFERENCE_OPEN_WEATHER, MODE_PRIVATE)
        requestCurrent = client.create(OpenweathermapCurrent::class.java)
        requestForecast = client.create(OpenweathermapForecast::class.java)
    }

    @Throws
    override suspend fun requestServerByCoordinatesAsync(lat: Double, lon: Double): Pair<Current, ForecastList> {
        return coroutineScope {
            takeIf { checkLackOfNetwork()} ?.run { throw Throwable(strNoNetwork)
            } ?: run {
                getKey()
                getAnswer(async(coroutineContext) { requestCurrentByCoordinates(lat, lon) },
                        async(coroutineContext) { requestForecastByCoordinates(lat, lon) })
            }
        }
    }

    @Throws
    override suspend fun requestServerByNameAsync(q: String): Pair<Current, ForecastList> {
        return coroutineScope {
            takeIf { checkLackOfNetwork()} ?.run { throw Throwable(strNoNetwork)
            } ?: run {
                getKey()
                getAnswer(async(coroutineContext) { requestCurrentByName(q) },
                        async(coroutineContext) { requestForecastByName(q) })
            }
        }
    }

    @Throws
    override suspend fun requestServerByIndexAsync(zip: String): Pair<Current, ForecastList> {
        return coroutineScope {
            takeIf { checkLackOfNetwork()} ?.run { throw Throwable(strNoNetwork)
            } ?: run {
                getKey()
                getAnswer(async(coroutineContext) { requestCurrentByIndex(zip) },
                        async(coroutineContext) { requestForecastByIndex(zip) })
            }
        }
    }

    @Throws
    private suspend fun getAnswer(deferredCurrent: Deferred<Current>, deferredForecast: Deferred<ForecastList>) : Pair<Current, ForecastList>{
        val current = deferredCurrent.await()
        val forecasts = deferredForecast.await()
        return Pair(current, forecasts)
    }

    private suspend fun requestCurrentByCoordinates(lat: Double, lon: Double): Current {
        return suspendCoroutine{continuation ->
            requestCurrent.loadByCoordinates(lat, lon, key, UNITS_METRIC, lang)
                .enqueue(getCallBackCurrent(continuation))
        }
    }

    private suspend fun requestCurrentByName(q: String): Current {
        return suspendCoroutine{continuation ->
            requestCurrent.uploadByName(q, key, UNITS_METRIC, lang)
                    .enqueue(getCallBackCurrent(continuation))
        }
    }

    private suspend fun requestCurrentByIndex(zip: String): Current {
        return suspendCoroutine{continuation ->
            requestCurrent.uploadByIndex(zip, key, UNITS_METRIC, lang)
                    .enqueue(getCallBackCurrent(continuation))
        }
    }

    private fun getCallBackCurrent(continuation: Continuation<Current>): Callback<Current> {
        return object : Callback<Current> {
            override fun onFailure(call: Call<Current>, t: Throwable) {
                continuation.resumeWithException(t)
            }

            override fun onResponse(call: Call<Current>, response: Response<Current>) {
                if(response.isSuccessful){
                    response.body()?.let { continuation.resume(it) }
                } else {
                    response.errorBody()?.let { continuation.resumeWithException(Throwable(it.string())) }
                }
            }
        }
    }

    private suspend fun requestForecastByCoordinates(lat: Double, lon: Double): ForecastList {
        return suspendCoroutine{continuation ->
            requestForecast.loadByCoordinates(lat, lon, key, UNITS_METRIC, lang)
                .enqueue(getCallBackForecastList(continuation))
        }
    }

    private suspend fun requestForecastByName(q: String): ForecastList {
        return suspendCoroutine{continuation ->
            requestForecast.uploadByName(q, key, UNITS_METRIC, lang)
                    .enqueue(getCallBackForecastList(continuation))
        }
    }

    private suspend fun requestForecastByIndex(zip: String): ForecastList {
        return suspendCoroutine{continuation ->
            requestForecast.uploadByIndex(zip, key, UNITS_METRIC, lang)
                    .enqueue(getCallBackForecastList(continuation))
        }
    }

    private fun getCallBackForecastList(continuation: Continuation<ForecastList>): Callback<ForecastList> {
        return object : Callback<ForecastList> {
            override fun onFailure(call: Call<ForecastList>, t: Throwable) {
                continuation.resumeWithException(t)
            }

            override fun onResponse(call: Call<ForecastList>, response: Response<ForecastList>) {
                if(response.isSuccessful){
                    response.body()?.let { continuation.resume(it) }
                } else {
                    response.errorBody()?.let { continuation.resumeWithException(Throwable(it.string())) }
                }
            }
        }
    }

    private fun getKey(){
        key = sp.getString(KEY_WEATHER, BuildConfig.OPEN_WEATHER_KEY)!!
    }
}