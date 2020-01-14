package reschikov.geekbrains.androidadvancedlevel.weatherapplication.repository

import reschikov.geekbrains.androidadvancedlevel.weatherapplication.data.database.model.CityTable
import reschikov.geekbrains.androidadvancedlevel.weatherapplication.domain.DataWeather
import reschikov.geekbrains.androidadvancedlevel.weatherapplication.domain.Place

interface Derivable {

    suspend fun addCurrentPlace(): Place.Places
    suspend fun getStateCurrentPlace(): DataWeather
    suspend fun determineLocationCoordinates(place: String, code: String): Place.Places
    suspend fun addSelectedPlace(lat: Double, lon: Double): Place.Places
    suspend fun getListCities(): Place.Places
    suspend fun deletePlace(lat: Double, lon: Double): Place.Places
    suspend fun loadLastPlace(): CityTable?
    suspend fun getDataWeather(lat: Double, lon: Double): DataWeather
    suspend fun getStateLastPlace(): DataWeather?
}