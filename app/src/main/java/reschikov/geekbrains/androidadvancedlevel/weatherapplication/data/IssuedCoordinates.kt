package reschikov.geekbrains.androidadvancedlevel.weatherapplication.data

import reschikov.geekbrains.androidadvancedlevel.weatherapplication.data.network.request.command.GetByCoordinates

interface IssuedCoordinates : Interchangeable {
    suspend fun getCoordinatesCurrentPlace(): Pair<GetByCoordinates?, Throwable?>
}