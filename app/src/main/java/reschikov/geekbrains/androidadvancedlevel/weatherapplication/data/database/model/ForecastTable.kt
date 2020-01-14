package reschikov.geekbrains.androidadvancedlevel.weatherapplication.data.database.model


import androidx.room.*
import reschikov.geekbrains.androidadvancedlevel.weatherapplication.data.network.model.data.openweather.current.Coord
import reschikov.geekbrains.androidadvancedlevel.weatherapplication.data.network.model.data.openweather.current.Main
import reschikov.geekbrains.androidadvancedlevel.weatherapplication.data.network.model.data.openweather.current.Weather

@Entity(foreignKeys = [ForeignKey(entity = CityTable::class, parentColumns = ["lat", "lon"], childColumns = ["lat", "lon"], onDelete = ForeignKey.CASCADE)],
        indices = [Index("lat", "lon")])
data class ForecastTable(@PrimaryKey(autoGenerate = true) val id: Long = 0,
                         val dt: Long,
                         @Embedded val main: Main,
                         @Embedded val weather: Weather,
                         val clouds: Int,
                         val directionWind: String?,
                         val speedWind: Float = 0.0f,
                         val pressure: String,
                         val precipitation: Int,
                         val dt_txt: String,
                         val temperatureColor: Int,
                         @Embedded val coord: Coord)