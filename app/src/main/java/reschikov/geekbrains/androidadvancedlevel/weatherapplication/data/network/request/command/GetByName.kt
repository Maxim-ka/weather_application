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

class GetByName(private val name: String) : Requested {

    override suspend fun executeAsync(moldable: Moldable): Pair<Current, ForecastList> {
        return  coroutineScope {
            val deferredCurrent: Deferred<Current> = async(coroutineContext) {
                moldable.requestCurrentByName() }
            val deferredForecast: Deferred<ForecastList> = async(coroutineContext) {
                moldable.requestForecastByName()}

            Pair(deferredCurrent.await(), deferredForecast.await())
        }
    }

    private suspend fun Moldable.requestCurrentByName(): Current {
        return suspendCoroutine{continuation ->
            getRequestCurrent().uploadByName(name, getCurrentKey(), UNITS_METRIC, getCurrentLang())
                    .enqueue(getCallBack<Current>(continuation))
        }
    }

    private suspend fun Moldable.requestForecastByName(): ForecastList {
        return suspendCoroutine{continuation ->
            getRequestForecast().uploadByName(name, getCurrentKey(), UNITS_METRIC, getCurrentLang())
                    .enqueue(getCallBack<ForecastList>(continuation))
        }
    }
}