package reschikov.geekbrains.androidadvancedlevel.weatherapplication.repository

import reschikov.geekbrains.androidadvancedlevel.weatherapplication.domain.Place
import reschikov.geekbrains.androidadvancedlevel.weatherapplication.domain.Weather

interface Derivable {

    suspend fun addPlaceByName(name: String): Pair<List<Place>?, Throwable?>
    suspend fun addPlaceByZipCode(postCode: String): Pair<List<Place>?, Throwable?>
    suspend fun addCurrentPlace(): Pair<List<Place>?, Throwable?>
    suspend fun getStateCurrentPlace(): Weather
    suspend fun determineLocationCoordinates(place: String, code: String): Pair<List<Place>?, Throwable?>
    suspend fun addSelectedPlace(lat: Double, lon: Double): Pair<List<Place>?, Throwable?>
    suspend fun getListCities(): Pair<List<Place>?, Throwable?>
    suspend fun deletePlace(lat: Double, lon: Double): Pair<List<Place>?, Throwable?>
    suspend fun getDataWeather(lat: Double, lon: Double): Weather
    suspend fun getStateLastPlace(): Weather?
}