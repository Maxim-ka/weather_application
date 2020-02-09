package reschikov.geekbrains.androidadvancedlevel.weatherapplication.repository

import reschikov.geekbrains.androidadvancedlevel.weatherapplication.data.network.Requested
import reschikov.geekbrains.androidadvancedlevel.weatherapplication.domain.Place
import reschikov.geekbrains.androidadvancedlevel.weatherapplication.domain.Weather

interface Derivable {

    suspend fun addPlace(requested: Requested): Pair<List<Place>?, Throwable?>
    suspend fun addCurrentPlace(): Pair<List<Place>?, Throwable?>
    suspend fun getStateCurrentPlace(): Weather
    suspend fun determineLocationCoordinates(place: String, code: String): Pair<List<Place>?, Throwable?>
    suspend fun getListCities(): Pair<List<Place>?, Throwable?>
    suspend fun deletePlace(lat: Double, lon: Double): Pair<List<Place>?, Throwable?>
    suspend fun getDataWeather(lat: Double, lon: Double): Weather
    suspend fun getStateLastPlace(): Weather?
}