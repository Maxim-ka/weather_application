package reschikov.geekbrains.androidadvancedlevel.weatherapplication.data.network

import reschikov.geekbrains.androidadvancedlevel.weatherapplication.data.network.request.OpenweathermapCurrent
import reschikov.geekbrains.androidadvancedlevel.weatherapplication.data.network.request.OpenweathermapForecast
import retrofit2.Callback
import kotlin.coroutines.Continuation

interface Moldable {

    fun getRequestCurrent() : OpenweathermapCurrent
    fun getRequestForecast() : OpenweathermapForecast
    fun getCurrentKey() : String
    fun getCurrentLang() : String
    fun <T> getCallBack(continuation: Continuation<T>): Callback<T>
}