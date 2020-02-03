package reschikov.geekbrains.androidadvancedlevel.weatherapplication.data.database.model


import androidx.databinding.ObservableBoolean
import androidx.room.*
import reschikov.geekbrains.androidadvancedlevel.weatherapplication.data.network.model.data.openweather.current.Coord
import reschikov.geekbrains.androidadvancedlevel.weatherapplication.data.network.model.data.openweather.current.Weather

@Entity(foreignKeys = [ForeignKey(entity = CityTable::class, parentColumns = ["lat", "lon"], childColumns = ["lat", "lon"], onDelete = ForeignKey.CASCADE)],
        indices = [Index("lat", "lon")])
data class ForecastTable(@PrimaryKey(autoGenerate = true) val id: Long = 0,
                         val temperatureColor: Int,
                         val dt: Long,
                         @Embedded val weather: Weather,
                         val tempMin: Double,
                         val tempMax: Double,
                         val humidity: Int,
                         val precipitation: Int,
                         val clouds: Int,
                         val wind: String,
                         val pressure: String,
                         @Embedded val coord: Coord){

    @Ignore val selected = ObservableBoolean()
}