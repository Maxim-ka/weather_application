package reschikov.geekbrains.androidadvancedlevel.weatherapplication.data

import android.location.Location

interface IssuedCoordinates {
    suspend fun getCoordinatesCurrentPlace(): Pair<Location?, Throwable?>
}