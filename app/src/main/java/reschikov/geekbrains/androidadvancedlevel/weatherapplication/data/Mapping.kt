package reschikov.geekbrains.androidadvancedlevel.weatherapplication.data

import reschikov.geekbrains.androidadvancedlevel.weatherapplication.MILLI_SEC
import reschikov.geekbrains.androidadvancedlevel.weatherapplication.data.database.model.CityTable
import reschikov.geekbrains.androidadvancedlevel.weatherapplication.data.database.model.CurrentTable
import reschikov.geekbrains.androidadvancedlevel.weatherapplication.data.database.model.ForecastTable
import reschikov.geekbrains.androidadvancedlevel.weatherapplication.data.network.model.data.opencage.Result
import reschikov.geekbrains.androidadvancedlevel.weatherapplication.data.network.model.data.openweather.current.Current
import reschikov.geekbrains.androidadvancedlevel.weatherapplication.data.network.model.data.openweather.current.Wind
import reschikov.geekbrains.androidadvancedlevel.weatherapplication.data.network.model.data.openweather.forecast.Forecast
import reschikov.geekbrains.androidadvancedlevel.weatherapplication.data.network.model.data.openweather.forecast.ForecastList
import reschikov.geekbrains.androidadvancedlevel.weatherapplication.domain.Place
import reschikov.geekbrains.androidadvancedlevel.weatherapplication.unit.TintableTemperature
import reschikov.geekbrains.androidadvancedlevel.weatherapplication.unit.setPressure
import reschikov.geekbrains.androidadvancedlevel.weatherapplication.unit.setWindDirection

class Mapping(val tintable: TintableTemperature) {

    private val identifyWind = {wind: Wind? ->
        "${wind?.let { setWindDirection(it) }} ${wind?.speed ?: 0.0f}"
    }

    fun createCityTable(current: Current, lastShowTime: Long?): CityTable {
        return current.run {
            CityTable(
                name = "${name}, ${sys.country}",
                coord = coord,
                showTime = lastShowTime?.let { it - 1} ?: System.currentTimeMillis())
        }
    }

    fun createCurrentTable(current: Current): CurrentTable {
        return current.run {
            CurrentTable(
                tempColor = tintable.getTemperatureColor(main.temp),
                name = "${name}, ${sys.country}",
                dt = dt * MILLI_SEC,
                weather = weather[0],
                temp = main.temp,
                feelsLike = main.feelsLike,
                wind = identifyWind.invoke(wind),
                humidity = main.humidity,
                pressure = setPressure(main.pressure),
                cloud = cloud?.all ?: 0,
                precipitation = rain?.rainH ?: snow?.snowH ?: 0,
                sunrise = sys.sunrise * MILLI_SEC,
                sunset = sys.sunset * MILLI_SEC,
                coord = coord
            )
        }
    }

    fun createListForecastTable(forecastList: ForecastList, current: Current): List<ForecastTable>{
        return forecastList.list.map {
           forecast -> getForecastTable(forecast, current)
       }
    }

    private fun getForecastTable(forecast: Forecast, current: Current): ForecastTable{
        return forecast.run {
            ForecastTable(
                temperatureColor = tintable.getTemperatureColor(main.tempMin, main.tempMax),
                dt = dt * MILLI_SEC,
                weather = weather[0],
                tempMin = main.tempMin,
                tempMax = main.tempMax,
                humidity = main.humidity,
                precipitation = rain?.rain3H ?: snow?.snow3H ?: 0,
                clouds = clouds?.all ?: 0,
                wind = identifyWind.invoke(wind),
                pressure = setPressure(main.pressure),
                coord = current.coord
            )
        }
    }

    fun createListPlaceCity(list: List<CityTable>): List<Place>{
        return list.map {getPlace(it)}
    }

    fun createListPlaceResult(list: List<Result>): List<Place>{
        return list.map { getPlace(it) }
    }

    private fun getPlace(cityTable: CityTable): Place {
        return cityTable.run {
            Place(name = name,
                lat = coord.lat,
                lon = coord.lon)
        }
    }

    private fun getPlace(result: Result): Place {
        return result.run {
            Place(name = formatted,
                lat = geometry.lat,
                lon = geometry.lng)
        }
    }
}