package reschikov.geekbrains.androidadvancedlevel.weatherapplication.data.database.model

import androidx.room.*
import androidx.room.ForeignKey.CASCADE
import reschikov.geekbrains.androidadvancedlevel.weatherapplication.data.network.model.data.openweather.current.Coord
import reschikov.geekbrains.androidadvancedlevel.weatherapplication.data.network.model.data.openweather.current.Main
import reschikov.geekbrains.androidadvancedlevel.weatherapplication.data.network.model.data.openweather.current.Weather

@Entity(foreignKeys = [ForeignKey(entity = CityTable::class, parentColumns = ["lat", "lon"], childColumns = ["lat", "lon"], onDelete = CASCADE)],
        indices = [Index("lat", "lon")])
data class CurrentTable(@PrimaryKey(autoGenerate = true) val id: Long = 0,
                        @Embedded val weather: Weather,
                        @Embedded val main: Main,
                        val cloud: Int,
                        val dt: Long,
                        val name: String,
                        val directionWind: String?,
                        val speedWind: Float,
                        val pressure: String,
                        val precipitation: Int,
                        val sunrise: Long,
                        val sunset: Long,
                        val tempColor: Int,
                        @Embedded val coord: Coord)