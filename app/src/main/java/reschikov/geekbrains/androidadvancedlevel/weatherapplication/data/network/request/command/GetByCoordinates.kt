package reschikov.geekbrains.androidadvancedlevel.weatherapplication.data.network.request.command

import kotlinx.coroutines.Deferred
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import reschikov.geekbrains.androidadvancedlevel.weatherapplication.UNITS_METRIC
import reschikov.geekbrains.androidadvancedlevel.weatherapplication.data.network.Moldable
import reschikov.geekbrains.androidadvancedlevel.weatherapplication.data.network.Requested
import reschikov.geekbrains.androidadvancedlevel.weatherapplication.data.network.model.data.openweather.current.Current
import reschikov.geekbrains.androidadvancedlevel.weatherapplication.data.network.model.data.openweather.forecast.ForecastList
import kotlin.coroutines.suspendCoroutine

class GetByCoordinates(private val lat: Double, private  val lon: Double) : Requested {


    override suspend fun executeAsync(moldable: Moldable): Pair<Current, ForecastList> {
        return  coroutineScope {
            val deferredCurrent: Deferred<Current> = async(coroutineContext) {
                moldable.requestCurrentByCoordinates() }
            val deferredForecast: Deferred<ForecastList> = async(coroutineContext) {
                moldable.requestForecastByCoordinates()}

            Pair(deferredCurrent.await(), deferredForecast.await())
        }
    }

    private suspend fun Moldable.requestCurrentByCoordinates(): Current {
        return suspendCoroutine{continuation ->
            getRequestCurrent().loadByCoordinates(lat, lon, getCurrentKey(), UNITS_METRIC, getCurrentLang())
                    .enqueue(getCallBack<Current>(continuation))
        }
    }

    private suspend fun Moldable.requestForecastByCoordinates(): ForecastList {
        return suspendCoroutine{continuation ->
            getRequestForecast().loadByCoordinates(lat, lon, getCurrentKey(), UNITS_METRIC, getCurrentLang())
                    .enqueue(getCallBack<ForecastList>(continuation))
        }
    }
}