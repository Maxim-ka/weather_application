package reschikov.geekbrains.androidadvancedlevel.weatherapplication.ui.listplaces

import kotlinx.coroutines.channels.ReceiveChannel
import reschikov.geekbrains.androidadvancedlevel.weatherapplication.data.network.Requested
import reschikov.geekbrains.androidadvancedlevel.weatherapplication.domain.Place

interface AddablePlace {
    fun findPlaces(place: String, code: String): ReceiveChannel<List<Place>?>
    fun addPlace(requested: Requested)
}