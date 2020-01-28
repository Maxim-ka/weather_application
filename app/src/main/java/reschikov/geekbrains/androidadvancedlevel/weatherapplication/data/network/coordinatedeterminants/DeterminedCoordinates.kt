package reschikov.geekbrains.androidadvancedlevel.weatherapplication.data.network.coordinatedeterminants

import android.location.Location

interface DeterminedCoordinates {

    fun isGoogleDefined(): Boolean
    suspend fun getCoordinates(): Pair<Location?, Throwable?>
    suspend fun determineCoordinates(): Location
}