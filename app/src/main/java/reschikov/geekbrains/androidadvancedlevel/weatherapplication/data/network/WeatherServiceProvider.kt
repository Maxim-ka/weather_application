package reschikov.geekbrains.androidadvancedlevel.weatherapplication.data.network

import android.content.Context
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import reschikov.geekbrains.androidadvancedlevel.weatherapplication.BuildConfig
import reschikov.geekbrains.androidadvancedlevel.weatherapplication.data.RequestedWeather
import reschikov.geekbrains.androidadvancedlevel.weatherapplication.data.network.model.data.openweather.current.Current
import reschikov.geekbrains.androidadvancedlevel.weatherapplication.data.network.model.data.openweather.forecast.ForecastList
import reschikov.geekbrains.androidadvancedlevel.weatherapplication.data.network.request.OpenweathermapCurrent
import reschikov.geekbrains.androidadvancedlevel.weatherapplication.data.network.request.OpenweathermapForecast
import reschikov.geekbrains.androidadvancedlevel.weatherapplication.domain.BaseException
import reschikov.geekbrains.androidadvancedlevel.weatherapplication.domain.Weather
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

private const val URL_OPEN_WEATHER_MAP = "https://api.openweathermap.org/data/2.5/"
private const val UNITS_METRIC = "metric"

class WeatherServiceProvider(context: Context) : RequestBaseProvider(context),
        RequestedWeather {

    private val requestCurrent: OpenweathermapCurrent
    private val requestForecast: OpenweathermapForecast
    private val key: String

    init {
        createClient(URL_OPEN_WEATHER_MAP)
        requestCurrent = client.create(OpenweathermapCurrent::class.java)
        requestForecast = client.create(OpenweathermapForecast::class.java)
        key = BuildConfig.OPEN_WEATHER_KEY
    }

    override suspend fun requestServerAsync(lat: Double, lon: Double): Weather.Data {
        return coroutineScope {
            try {
                takeIf { checkLackOfNetwork()} ?.run { throw BaseException.NoNetwork()
                } ?: run {
                    val deferredCurrent = async { requestCurrent(lat, lon) }
                    val deferredForecast = async { requestForecast(lat, lon) }
                    val current = deferredCurrent.await()
                    val forecasts = deferredForecast.await()
                    Weather.Data(Weather.Received(current, forecasts), null)
                }
            } catch (e: Throwable) {
                Weather.Data(null, e)
            }
        }
    }


    private suspend fun requestCurrent(lat: Double, lon: Double): Current {
        return suspendCoroutine{continuation ->
            requestCurrent.loadCurrentWeather(lat, lon, key, UNITS_METRIC, lang)
                .enqueue(object : Callback<Current> {
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
                })

        }
    }

    private suspend fun requestForecast(lat: Double, lon: Double): ForecastList {
        return suspendCoroutine{continuation ->
            requestForecast.loadForecastWeather(lat, lon, key, UNITS_METRIC, lang)
                .enqueue(object : Callback<ForecastList> {
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
                })

        }
    }
}