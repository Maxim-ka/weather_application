package reschikov.geekbrains.androidadvancedlevel.weatherapplication.domain

import reschikov.geekbrains.androidadvancedlevel.weatherapplication.data.database.model.CurrentTable
import reschikov.geekbrains.androidadvancedlevel.weatherapplication.data.database.model.ForecastTable
import reschikov.geekbrains.androidadvancedlevel.weatherapplication.data.network.model.data.openweather.current.Current
import reschikov.geekbrains.androidadvancedlevel.weatherapplication.data.network.model.data.openweather.forecast.ForecastList

sealed class Weather{

    data class Received(val current: Current, val forecastList: ForecastList): Weather()
    data class Saved(val currentTable: CurrentTable, val forecasts: List<ForecastTable>) : Weather()
    data class Data (var weather: Weather?, var error: Throwable?) : Weather()
}