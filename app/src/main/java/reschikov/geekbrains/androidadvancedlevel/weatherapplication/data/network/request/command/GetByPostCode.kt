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

class GetByPostCode(private val zip: String) : Requested {

    override suspend fun executeAsync(moldable: Moldable): Pair<Current, ForecastList> {
        return  coroutineScope {
            val deferredCurrent: Deferred<Current> = async(coroutineContext) {
                moldable.requestCurrentByIndex() }
            val deferredForecast: Deferred<ForecastList> = async(coroutineContext) {
                moldable.requestForecastByIndex()}

            Pair(deferredCurrent.await(), deferredForecast.await())
        }
    }

    private suspend fun Moldable.requestCurrentByIndex(): Current {
        return suspendCoroutine{continuation ->
            getRequestCurrent().uploadByIndex(zip, getCurrentKey(), UNITS_METRIC, getCurrentLang())
                    .enqueue(getCallBack<Current>(continuation))
        }
    }

    private suspend fun Moldable.requestForecastByIndex(): ForecastList {
        return suspendCoroutine{continuation ->
            getRequestForecast().uploadByIndex(zip, getCurrentKey(), UNITS_METRIC, getCurrentLang())
                    .enqueue(getCallBack<ForecastList>(continuation))
        }
    }
}