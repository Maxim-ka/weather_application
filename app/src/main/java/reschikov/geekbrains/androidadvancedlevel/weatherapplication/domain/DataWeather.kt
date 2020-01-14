package reschikov.geekbrains.androidadvancedlevel.weatherapplication.domain

import reschikov.geekbrains.androidadvancedlevel.weatherapplication.data.database.model.CurrentTable
import reschikov.geekbrains.androidadvancedlevel.weatherapplication.data.database.model.ForecastTable
import reschikov.geekbrains.androidadvancedlevel.weatherapplication.data.network.model.data.openweather.current.Current
import reschikov.geekbrains.androidadvancedlevel.weatherapplication.data.network.model.data.openweather.forecast.ForecastList

sealed class DataWeather{

    data class ServerResponse(val current: Current, val forecastList: ForecastList): DataWeather()
    data class Data(val currentTable: CurrentTable, val forecasts: List<ForecastTable>) : DataWeather()
    data class Error (val error: Throwable) : DataWeather()
}