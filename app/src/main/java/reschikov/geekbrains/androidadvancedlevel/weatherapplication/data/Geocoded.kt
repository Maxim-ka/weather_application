package reschikov.geekbrains.androidadvancedlevel.weatherapplication.data

import reschikov.geekbrains.androidadvancedlevel.weatherapplication.data.network.model.data.opencage.Result

interface Geocoded : Interchangeable {

    suspend fun requestDirectGeocoding(place: String, code: String): List<Result>
}