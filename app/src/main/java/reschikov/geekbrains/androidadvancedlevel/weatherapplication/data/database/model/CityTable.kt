package reschikov.geekbrains.androidadvancedlevel.weatherapplication.data.database.model

import androidx.room.Embedded
import androidx.room.Entity
import reschikov.geekbrains.androidadvancedlevel.weatherapplication.data.network.model.data.openweather.current.Coord

@Entity(primaryKeys = ["lat", "lon"])
data class CityTable (@Embedded val coord: Coord,
                      val name: String,
                      val showTime: Long) :
        Comparable<CityTable>{

    override fun compareTo(other: CityTable): Int {
        return showTime.compareTo(other.showTime)
    }
}