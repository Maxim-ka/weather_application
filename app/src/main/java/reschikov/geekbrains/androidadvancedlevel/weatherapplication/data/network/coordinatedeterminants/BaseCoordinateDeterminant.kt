package reschikov.geekbrains.androidadvancedlevel.weatherapplication.data.network.coordinatedeterminants

import android.location.Location
import kotlinx.coroutines.*
import timber.log.Timber
import kotlin.coroutines.CoroutineContext

private const val INTERVAL_UPDATE = 2_000L
private const val MIN_ACCURACY = 200.0f
private const val SEARCH_TIME = 60_000L

abstract class BaseCoordinateDeterminant : DeterminedCoordinates, CoroutineScope {

    override val coroutineContext: CoroutineContext by lazy { Dispatchers.IO + Job() }
    protected var setPeriod = INTERVAL_UPDATE
    protected var setAccuracy = MIN_ACCURACY

    override suspend fun getCoordinates(): Pair<Location?, Throwable?> {
        return withContext(coroutineContext){
            try {
                withTimeout(SEARCH_TIME){
                    try {
                        Pair(determineCoordinates(), null)
                    } catch (e: Throwable) {
                        Pair(null, e)
                    }
                }
            } catch (e: TimeoutCancellationException) {
                Timber.i("TimeoutCancellationException %s", e.message.toString())
                Pair(null, e)
            }
        }
    }
}