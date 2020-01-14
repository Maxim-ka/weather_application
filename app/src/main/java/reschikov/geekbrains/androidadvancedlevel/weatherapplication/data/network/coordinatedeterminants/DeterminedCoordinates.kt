package reschikov.geekbrains.androidadvancedlevel.weatherapplication.data.network.coordinatedeterminants

import android.location.Location
import reschikov.geekbrains.androidadvancedlevel.weatherapplication.domain.Place

interface DeterminedCoordinates {

    fun isGoogleDefined(): Boolean
    suspend fun getCoordinates(): Place.Coordinates
    suspend fun determineCoordinates(): Location
}