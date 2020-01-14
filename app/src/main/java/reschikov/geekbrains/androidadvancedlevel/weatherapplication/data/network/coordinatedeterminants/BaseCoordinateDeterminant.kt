package reschikov.geekbrains.androidadvancedlevel.weatherapplication.data.network.coordinatedeterminants

import kotlinx.coroutines.TimeoutCancellationException
import kotlinx.coroutines.withTimeout
import reschikov.geekbrains.androidadvancedlevel.weatherapplication.data.network.model.data.openweather.current.Coord
import reschikov.geekbrains.androidadvancedlevel.weatherapplication.domain.Place
import timber.log.Timber

private const val INTERVAL_UPDATE = 2_000L
private const val MIN_ACCURACY = 200.0f
private const val SEARCH_TIME = 60_000L

abstract class BaseCoordinateDeterminant : DeterminedCoordinates {

    protected var setPeriod = INTERVAL_UPDATE
    protected var setAccuracy = MIN_ACCURACY

    override suspend fun getCoordinates(): Place.Coordinates {
        return  try {
                    withTimeout(SEARCH_TIME){
                        try {
                            val location = determineCoordinates()
                            Timber.i("location ${location.latitude}, ${location.longitude}")
                            Place.Coordinates(Coord(location.longitude, location.latitude), null)
                        } catch (e: Throwable) {
                            Timber.i(e)
                            Place.Coordinates(null, e)
                        }
                    }
                } catch (e: TimeoutCancellationException) {
                    Timber.i("TimeoutCancellationException %s", e.message.toString())
                    Place.Coordinates(null, e)
                }
    }
}