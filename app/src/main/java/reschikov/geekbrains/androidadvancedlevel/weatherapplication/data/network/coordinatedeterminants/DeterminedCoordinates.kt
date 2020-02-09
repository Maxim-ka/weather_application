package reschikov.geekbrains.androidadvancedlevel.weatherapplication.data.network.coordinatedeterminants

import android.location.Location
import reschikov.geekbrains.androidadvancedlevel.weatherapplication.data.network.request.command.GetByCoordinates

interface DeterminedCoordinates {

    fun isGoogleDefined(): Boolean
    suspend fun getCoordinates(): Pair<GetByCoordinates?, Throwable?>
    suspend fun determineCoordinates(): Location
}