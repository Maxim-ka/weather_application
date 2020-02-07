package reschikov.geekbrains.androidadvancedlevel.weatherapplication.data.network.coordinatedeterminants

import android.content.Context
import android.location.Location
import kotlinx.coroutines.*
import reschikov.geekbrains.androidadvancedlevel.weatherapplication.R
import kotlin.coroutines.CoroutineContext

private const val INTERVAL_UPDATE = 2_000L
private const val MIN_ACCURACY = 200.0f
private const val SEARCH_TIME = 60_000L

abstract class BaseCoordinateDeterminant(private val  context: Context) : DeterminedCoordinates, CoroutineScope {

    override val coroutineContext: CoroutineContext by lazy { Dispatchers.IO + Job() }
    protected val strNoNetwork : String by lazy {
        context.getString(R.string.no_network)
    }
    protected var setPeriod = INTERVAL_UPDATE
    protected var setAccuracy = MIN_ACCURACY
    private val strTimeout : String by lazy { context.getString(R.string.unable_determine_coordinates) }

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
                Pair(null, Throwable(strTimeout))
            }
        }
    }
}