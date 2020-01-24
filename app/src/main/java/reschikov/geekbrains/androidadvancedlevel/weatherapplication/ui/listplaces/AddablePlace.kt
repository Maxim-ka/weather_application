package reschikov.geekbrains.androidadvancedlevel.weatherapplication.ui.listplaces

import kotlinx.coroutines.channels.ReceiveChannel
import reschikov.geekbrains.androidadvancedlevel.weatherapplication.domain.Place

interface AddablePlace {
    fun findPlaces(place: String, code: String): ReceiveChannel<List<Place.Result>?>
    fun addPlaceByCoordinates(lat: Double, lon: Double)
    fun addPlaceByName(name: String)
    fun addPlaceByZipCode(postCode: String)
}