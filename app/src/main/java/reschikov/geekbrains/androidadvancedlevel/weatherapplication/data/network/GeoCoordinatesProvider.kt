package reschikov.geekbrains.androidadvancedlevel.weatherapplication.data.network

import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit
import com.google.gson.Gson
import kotlinx.coroutines.cancel
import kotlinx.coroutines.cancelChildren
import kotlinx.coroutines.withContext
import okhttp3.ResponseBody
import reschikov.geekbrains.androidadvancedlevel.weatherapplication.*
import reschikov.geekbrains.androidadvancedlevel.weatherapplication.data.Geocoded
import reschikov.geekbrains.androidadvancedlevel.weatherapplication.data.network.model.data.opencage.GeoCoordinates
import reschikov.geekbrains.androidadvancedlevel.weatherapplication.data.network.model.data.opencage.Rate
import reschikov.geekbrains.androidadvancedlevel.weatherapplication.data.network.model.data.opencage.ResponseError
import reschikov.geekbrains.androidadvancedlevel.weatherapplication.data.network.model.data.opencage.Result
import reschikov.geekbrains.androidadvancedlevel.weatherapplication.data.network.request.OpenCage
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.DateFormat
import java.util.*
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

private const val HTTPS_OPENCAGE_COORDINATE = "https://api.opencagedata.com/geocode/v1/"
private const val LIMIT = 10
private const val MIN_CONFIDENCE = 8
private const val NO_ANNOTATIONS = 1
private const val NO_DEDUPE = 1
private const val NO_RECORD = 1
private const val OK = 200

class GeoCoordinatesProvider(context: Context) : RequestBaseProvider(context), Geocoded {

    private val sp : SharedPreferences by lazy { context.getSharedPreferences(PREFERENCE_OPEN_CAGE, Context.MODE_PRIVATE) }
    private val strErrorCode : String by lazy { context.getString(R.string.error_code)  }
    private val strErrorMessage : String by lazy { context.getString(R.string.error_message) }
    private val strTimeReset : String by lazy { context.getString(R.string.warning_requests_per_day) }
    private val strNotFound : String by lazy { context.getString(R.string.err_location_not_found) }
    private val strError = {code: Int, message: String ->
        strErrorCode + code + strErrorMessage + message
    }
    private val request: OpenCage
    private lateinit var key: String

    init {
        createClient(HTTPS_OPENCAGE_COORDINATE)
        request = client.create(OpenCage::class.java)
    }

    @Throws
    override suspend fun requestDirectGeocoding(place: String, code: String): List<Result>{
        return withContext(coroutineContext){
            if (checkRequestCapability()) throw Throwable(getTimeReset())
            if (checkLackOfNetwork()) throw Throwable(strNoNetwork)
            request(place, code)
        }
    }

    override fun toClose() {
        coroutineContext.cancelChildren()
        coroutineContext.cancel()
    }

    private suspend fun request(place: String, code: String) : List<Result>{
        return suspendCoroutine {continuation ->
            getKey()
            request.coordinateRequest(place, key, code, lang, LIMIT, MIN_CONFIDENCE, NO_ANNOTATIONS, NO_DEDUPE, NO_RECORD)
                .enqueue(object : Callback<GeoCoordinates> {
                    override fun onFailure(call: Call<GeoCoordinates>, t: Throwable) {
                        continuation.resumeWithException(t)
                    }

                    override fun onResponse(call: Call<GeoCoordinates>, response: Response<GeoCoordinates>) {
                        if(response.isSuccessful){
                            response.body()?.let {
                                saveRequestParameters(it.rate)
                                if (it.status.code == OK ) continuation.resume(it.results)
                                else continuation.resumeWithException(Throwable(strError.invoke(it.status.code, it.status.message)))
                            }
                        } else {
                            response.errorBody()?.let {
                                continuation.resumeWithException(Throwable(getServerErrorResponse(it, strError)))
                            } ?: continuation.resumeWithException(Throwable(strNotFound))
                        }
                    }
                })
        }
    }

    private fun getKey(){
        key = sp.getString(KEY_OPEN_CAGE, BuildConfig.OPEN_CAGE_KEY)!!
    }

    private fun saveRequestParameters(rate: Rate){
        sp.edit {
            putInt(KEY_BALANCE_OF_REQUESTS, rate.remaining)
            putLong(KEY_RESET_DATE, rate.reset * MILLI_IN_SEC)
        }
    }

    private fun getServerErrorResponse(response: ResponseBody, strError: (Int, String) -> String ): String{
        val responseError = Gson().fromJson(response.string(), ResponseError::class.java)
        return  strError.invoke(responseError.status.code, responseError.status.message)
    }

    private fun checkRequestCapability(): Boolean {
        val remaining = sp.getInt(KEY_BALANCE_OF_REQUESTS, NUMBER_REQUESTS_PER_DAY)
        return remaining <= LIMIT
    }

    private fun getTimeReset(): String{
        val defaultTimeReset = System.currentTimeMillis() + NUMBER_MILLISECONDS_PER_DAY
        val reset = sp.getLong(KEY_RESET_DATE, defaultTimeReset)
        if (reset == defaultTimeReset) sp.edit { putLong(KEY_RESET_DATE, reset)}
        val timeReset = DateFormat.getDateTimeInstance().format(Date(reset))
        return strTimeReset + timeReset
    }
}