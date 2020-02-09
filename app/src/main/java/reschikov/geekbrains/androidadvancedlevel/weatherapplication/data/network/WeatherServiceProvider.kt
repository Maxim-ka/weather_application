package reschikov.geekbrains.androidadvancedlevel.weatherapplication.data.network

import android.content.Context
import android.content.Context.MODE_PRIVATE
import android.content.SharedPreferences
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

private const val URL_OPEN_WEATHER_MAP = "https://api.openweathermap.org/data/2.5/"

class WeatherServiceProvider(context: Context) : RequestBaseProvider(context),
        RequestedWeather,
        Moldable{

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

    override fun getRequestCurrent() = requestCurrent
    override fun getRequestForecast() = requestForecast
    override fun getCurrentKey() = key
    override fun getCurrentLang() = lang
    override fun <T> getCallBack(continuation: Continuation<T>): Callback<T> {
        return object : Callback<T> {
            override fun onFailure(call: Call<T>, t: Throwable) {
                continuation.resumeWithException(t)
            }

            override fun onResponse(call: Call<T>, response: Response<T>) {
                if(response.isSuccessful){
                    response.body()?.let { continuation.resume(it) }
                } else {
                    response.errorBody()?.let { continuation.resumeWithException(Throwable(it.string())) }
                }
            }
        }
    }

    @Throws
    override suspend fun requestServer(requested: Requested): Pair<Current, ForecastList> {
        if (checkLackOfNetwork()) throw Throwable(strNoNetwork)
        getKey()
        return requested.executeAsync(this)
    }

    private fun getKey(){
        key = sp.getString(KEY_WEATHER, BuildConfig.OPEN_WEATHER_KEY)!!
    }
}