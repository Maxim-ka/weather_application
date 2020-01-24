package reschikov.geekbrains.androidadvancedlevel.weatherapplication.repository

import reschikov.geekbrains.androidadvancedlevel.weatherapplication.domain.Place
import reschikov.geekbrains.androidadvancedlevel.weatherapplication.domain.Weather

interface Derivable {

    suspend fun addPlaceByName(name: String): Place.Places
    suspend fun addPlaceByZipCode(postCode: String): Place.Places
    suspend fun addCurrentPlace(): Place.Places
    suspend fun getStateCurrentPlace(): Weather.Data
    suspend fun determineLocationCoordinates(place: String, code: String): Place.Places
    suspend fun addSelectedPlace(lat: Double, lon: Double): Place.Places
    suspend fun getListCities(): Place.Places
    suspend fun deletePlace(lat: Double, lon: Double): Place.Places
    suspend fun getDataWeather(lat: Double, lon: Double): Weather.Data
    suspend fun getStateLastPlace(): Weather.Data?
}