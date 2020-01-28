package reschikov.geekbrains.androidadvancedlevel.weatherapplication.data

import reschikov.geekbrains.androidadvancedlevel.weatherapplication.MILLI_SEC
import reschikov.geekbrains.androidadvancedlevel.weatherapplication.data.database.model.CityTable
import reschikov.geekbrains.androidadvancedlevel.weatherapplication.data.database.model.CurrentTable
import reschikov.geekbrains.androidadvancedlevel.weatherapplication.data.database.model.ForecastTable
import reschikov.geekbrains.androidadvancedlevel.weatherapplication.data.network.model.data.opencage.Result
import reschikov.geekbrains.androidadvancedlevel.weatherapplication.data.network.model.data.openweather.current.Coord
import reschikov.geekbrains.androidadvancedlevel.weatherapplication.data.network.model.data.openweather.current.Current
import reschikov.geekbrains.androidadvancedlevel.weatherapplication.data.network.model.data.openweather.forecast.Forecast
import reschikov.geekbrains.androidadvancedlevel.weatherapplication.data.network.model.data.openweather.forecast.ForecastList
import reschikov.geekbrains.androidadvancedlevel.weatherapplication.domain.Place
import reschikov.geekbrains.androidadvancedlevel.weatherapplication.unit.TintableTemperature
import reschikov.geekbrains.androidadvancedlevel.weatherapplication.unit.setPressure
import reschikov.geekbrains.androidadvancedlevel.weatherapplication.unit.setWindDirection

class Mapping(val tintable: TintableTemperature) {

    fun createCityTable(current: Current, lastShowTime: Long?): CityTable {
        return CityTable(
            name = "${current.name}, ${current.sys.country}",
            coord = current.coord,
            showTime = lastShowTime?.let { it - 1} ?: System.currentTimeMillis())
    }

    fun createCityTable(current: Current): CityTable {
        return CityTable(
                name = "${current.name}, ${current.sys.country}",
                coord = current.coord,
                showTime = System.currentTimeMillis())
    }

    fun createCurrentTable(current: Current): CurrentTable {
        return CurrentTable(
            coord = current.coord,
            weather = current.weather[0],
            main = current.main,
            cloud = current.cloud?.all ?: 0,
            dt = current.dt * MILLI_SEC,
            name = "${current.name}, ${current.sys.country}",
            speedWind = current.wind?.speed ?: 0.0f,
            directionWind = current.wind?.let { setWindDirection(it) },
            pressure = setPressure(current.main.pressure),
            precipitation = with(current){
                rain?.rainH ?: snow?.snowH ?: 0
            },
            sunrise = current.sys.sunrise * MILLI_SEC,
            sunset = current.sys.sunset * MILLI_SEC,
            tempColor = tintable.getTemperatureColor(current.main.temp))
    }

    fun createListForecastTable(forecastList: ForecastList, coord: Coord): List<ForecastTable>{
        return forecastList.list.map {
           forecast -> getForecastTable(forecast, coord)
       }
    }

    private fun getForecastTable(forecast: Forecast, coord: Coord): ForecastTable{
        return ForecastTable(
            coord = coord,
            dt = forecast.dt * MILLI_SEC,
            main = forecast.main,
            weather = forecast.weather[0],
            clouds = forecast.clouds?.all ?: 0,
            dt_txt = forecast.dt_txt,
            speedWind = forecast.wind.speed,
            directionWind = setWindDirection(forecast.wind),
            pressure = setPressure(forecast.main.pressure),
            precipitation = with(forecast){
                rain?.rain3H ?: snow?.snow3H ?: 0
            },
            temperatureColor = tintable.getTemperatureColor(forecast.main.tempMin, forecast.main.tempMax)
        )
    }

    fun createListPlaceCity(list: List<CityTable>): List<Place>{
        return list.map {getPlace(it)}
    }

    fun createListPlaceResult(list: List<Result>): List<Place>{
        return list.map { getPlace(it) }
    }

    private fun getPlace(cityTable: CityTable): Place {
        return Place(name = cityTable.name,
                     lat = cityTable.coord.lat,
                     lon = cityTable.coord.lon)
    }

    private fun getPlace(result: Result): Place {
        return Place(name = result.formatted,
                     lat = result.geometry.lat,
                     lon = result.geometry.lng)
    }
}