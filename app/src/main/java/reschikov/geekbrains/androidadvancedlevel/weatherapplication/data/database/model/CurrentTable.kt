package reschikov.geekbrains.androidadvancedlevel.weatherapplication.data.database.model

import androidx.room.*
import androidx.room.ForeignKey.CASCADE
import reschikov.geekbrains.androidadvancedlevel.weatherapplication.data.network.model.data.openweather.current.Coord
import reschikov.geekbrains.androidadvancedlevel.weatherapplication.data.network.model.data.openweather.current.Weather

@Entity(foreignKeys = [ForeignKey(entity = CityTable::class, parentColumns = ["lat", "lon"],
    childColumns = ["lat", "lon"], onDelete = CASCADE)], indices = [Index("lat", "lon")])
data class CurrentTable(@PrimaryKey(autoGenerate = true) val id: Long = 0,
                        val tempColor: Int,
                        val name: String,
                        val dt: Long,
                        @Embedded val weather: Weather,
                        val temp: Double,
                        val feelsLike: Double,
                        val wind: String,
                        val humidity: Int,
                        val pressure: String,
                        val cloud: Int,
                        val precipitation: Int,
                        val sunrise: Long,
                        val sunset: Long,
                        @Embedded val coord: Coord)