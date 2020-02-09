package reschikov.geekbrains.androidadvancedlevel.weatherapplication.data.network

import reschikov.geekbrains.androidadvancedlevel.weatherapplication.data.network.model.data.openweather.current.Current
import reschikov.geekbrains.androidadvancedlevel.weatherapplication.data.network.model.data.openweather.forecast.ForecastList

interface Requested {

    suspend fun executeAsync(moldable: Moldable) : Pair<Current, ForecastList>
}