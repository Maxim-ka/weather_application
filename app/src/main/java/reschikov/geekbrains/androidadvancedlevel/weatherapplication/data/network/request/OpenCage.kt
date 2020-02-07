package reschikov.geekbrains.androidadvancedlevel.weatherapplication.data.network.request

import reschikov.geekbrains.androidadvancedlevel.weatherapplication.data.network.model.data.opencage.GeoCoordinates
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

private const val Q = "q"
private const val KEY = "key"
private const val COUNTRY_CODE = "countrycode"
private const val LANGUAGE ="language"
private const val LIMIT = "limit"
private const val MIN_CONFIDENCE = "min_confidence"
private const val NO_ANNOTATIONS = "no_annotations"
private const val NO_DEBUPE = "no_dedupe"
private const val NO_RECORD = "no_record"

interface OpenCage {

    @GET("json")
    fun coordinateRequest(@Query(Q) place: String,
                          @Query(KEY) key: String,
                          @Query(COUNTRY_CODE) code: String,
                          @Query(LANGUAGE) lang: String,
                          @Query(LIMIT) limit: Int,
                          @Query(MIN_CONFIDENCE) minConfidence: Int,
                          @Query(NO_ANNOTATIONS) noAnnotations: Int,
                          @Query(NO_DEBUPE) noDedupe: Int,
                          @Query(NO_RECORD) noRecord: Int): Call<GeoCoordinates>
}