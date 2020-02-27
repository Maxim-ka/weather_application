package reschikov.geekbrains.androidadvancedlevel.weatherapplication.data.network.coordinatedeterminants

import android.content.Context
import kotlinx.coroutines.*
import reschikov.geekbrains.androidadvancedlevel.weatherapplication.R
import reschikov.geekbrains.androidadvancedlevel.weatherapplication.data.network.request.command.GetByCoordinates
import kotlin.coroutines.CoroutineContext

private const val INTERVAL_UPDATE = 2_000L
private const val MIN_ACCURACY = 200.0f
private const val SEARCH_TIME = 60_000L

abstract class BaseCoordinateDeterminant(context: Context?)
    : DeterminedCoordinates, CoroutineScope{

    override val coroutineContext: CoroutineContext by lazy { Dispatchers.IO + Job() }
    protected val strNoNetwork : String? by lazy {
        context?.getString(R.string.err_no_network)
    }
    protected var setPeriod = INTERVAL_UPDATE
    protected var setAccuracy = MIN_ACCURACY
    private val strTimeout : String? by lazy { context?.getString(R.string.err_unable_determine_coordinates) }

    override suspend fun getCoordinates(): Pair<GetByCoordinates?, Throwable?> {
        return withContext(coroutineContext){
            try {
                withTimeout(SEARCH_TIME){
                    try {
                        val location = determineCoordinates()
                        Pair(GetByCoordinates(location.latitude, location.longitude), null)
                    } catch (e: Throwable) {
                        removeListener()
                        Pair(null, e)
                    }
                }
            } catch (e: TimeoutCancellationException) {
                removeListener()
                Pair(null, Throwable(strTimeout))
            }
        }
    }

    override fun toClose() {
        terminate()
        coroutineContext.cancel()
    }
}