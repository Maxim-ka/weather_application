package reschikov.geekbrains.androidadvancedlevel.weatherapplication.domain

import reschikov.geekbrains.androidadvancedlevel.weatherapplication.data.database.model.CityTable

sealed class Success {

    data class LastPlace(val city: CityTable?) : Success()
}