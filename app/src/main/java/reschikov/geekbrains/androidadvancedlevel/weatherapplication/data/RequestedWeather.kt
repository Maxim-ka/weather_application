package reschikov.geekbrains.androidadvancedlevel.weatherapplication.data

import reschikov.geekbrains.androidadvancedlevel.weatherapplication.data.network.Requested
import reschikov.geekbrains.androidadvancedlevel.weatherapplication.data.network.model.data.openweather.current.Current
import reschikov.geekbrains.androidadvancedlevel.weatherapplication.data.network.model.data.openweather.forecast.ForecastList

interface RequestedWeather {

    suspend fun requestServer(requested: Requested): Pair<Current, ForecastList>
}