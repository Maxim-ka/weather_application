package reschikov.geekbrains.androidadvancedlevel.weatherapplication.data.network

import android.content.Context
import com.google.gson.Gson
import reschikov.geekbrains.androidadvancedlevel.weatherapplication.BuildConfig
import reschikov.geekbrains.androidadvancedlevel.weatherapplication.MILLI_SEC
import reschikov.geekbrains.androidadvancedlevel.weatherapplication.R
import reschikov.geekbrains.androidadvancedlevel.weatherapplication.data.Geocoded
import reschikov.geekbrains.androidadvancedlevel.weatherapplication.data.network.model.data.opencage.GeoCoordinates
import reschikov.geekbrains.androidadvancedlevel.weatherapplication.data.network.model.data.opencage.ResponseError
import reschikov.geekbrains.androidadvancedlevel.weatherapplication.data.network.model.data.opencage.Result
import reschikov.geekbrains.androidadvancedlevel.weatherapplication.data.network.request.OpenCage
import reschikov.geekbrains.androidadvancedlevel.weatherapplication.domain.BaseException
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import timber.log.Timber
import java.text.DateFormat
import java.util.*
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

private const val KEY_OPEN_CAGE = "key OpenCage"
private const val KEY_NUMBER_OF_REQUESTS = "key number of requests"
private const val KEY_RESET_DATE = "key reset date"
private const val HTTPS_OPENCAGE_COORDINATE = "https://api.opencagedata.com/geocode/v1/"
private const val LIMIT = 10
private const val MIN_CONFIDENCE = 8
private const val NO_ANNOTATIONS = 1
private const val NO_DEDUPE = 1
private const val NO_RECORD = 1
private const val OK = 200
private const val NUMBER_REQUESTS_PER_DAY = 2500
private const val NUMBER_MILLISECONDS_PER_DAY = 86_400_000L

class GeoCoordinatesProvider(private val context: Context) : RequestBaseProvider(context), Geocoded {

    private val request: OpenCage
    private val key: String

    init {
        createClient(HTTPS_OPENCAGE_COORDINATE)
        request = client.create(OpenCage::class.java)
        key = BuildConfig.OPEN_CAGE_KEY
    }

    override suspend fun requestDirectGeocoding(place: String, code: String): List<Result>{
        return suspendCoroutine {continuation ->
            takeIf { checkRequestCapability() } ?.run { continuation.resumeWithException(Throwable(getTimeReset())) } ?:
            takeIf { checkLackOfNetwork() } ?.run { continuation.resumeWithException(BaseException.NoNetwork()) } ?:
            request.coordinateRequest(place, key, code, lang, LIMIT, MIN_CONFIDENCE, NO_ANNOTATIONS, NO_DEDUPE, NO_RECORD)
                .enqueue(object : Callback<GeoCoordinates> {
                    override fun onFailure(call: Call<GeoCoordinates>, t: Throwable) {
                        Timber.i("onFailure ${t.message}")
                        continuation.resumeWithException(t)
                    }

                    override fun onResponse(call: Call<GeoCoordinates>, response: Response<GeoCoordinates>) {
                        if(response.isSuccessful){
                            response.body()?.let {
                                preferences.edit().run {
                                    putInt(KEY_NUMBER_OF_REQUESTS, it.rate.remaining)
                                    putLong(KEY_RESET_DATE, it.rate.reset * MILLI_SEC)
                                    apply()
                                }
                                if (it.status.code == OK ){
                                    Timber.i("onResponse ${it.results}")
                                    continuation.resume(it.results)
                                } else {
                                    continuation.resumeWithException(Throwable("${it.status.code}, ${it.status.message}"))
                                }
                            }
                        } else {
                            response.errorBody()?.let {
                                val responseError = Gson().fromJson(it.string(), ResponseError::class.java)
                                val error = "Error code ${responseError.status.code} \nmessage ${responseError.status.message}"
                                continuation.resumeWithException(Throwable(error))
                            } ?: continuation.resumeWithException(Throwable(context.getString(R.string.msg_location_not_found)))
                        }
                    }
                })
        }
    }

    private fun checkRequestCapability(): Boolean {
        val remaining = preferences.getInt(KEY_NUMBER_OF_REQUESTS, NUMBER_REQUESTS_PER_DAY)
        return remaining <= LIMIT
    }

    private fun getTimeReset(): String{
        val defaultTimeReset = System.currentTimeMillis() + NUMBER_MILLISECONDS_PER_DAY
        val reset = preferences.getLong(KEY_RESET_DATE, defaultTimeReset)
        if (reset == defaultTimeReset) preferences.edit().run {
            putLong(KEY_RESET_DATE, reset)
            apply()
        }
        val timeReset = DateFormat.getDateTimeInstance().format(Date(reset))
        return "the number of requests per day is exceeded, you can re-request after $timeReset"
    }
}