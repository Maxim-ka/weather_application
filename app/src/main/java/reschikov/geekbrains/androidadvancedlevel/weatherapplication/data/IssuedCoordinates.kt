package reschikov.geekbrains.androidadvancedlevel.weatherapplication.data

import reschikov.geekbrains.androidadvancedlevel.weatherapplication.domain.Place

interface IssuedCoordinates {
    suspend fun getCoordinatesCurrentPlace(): Place.Coordinates
}