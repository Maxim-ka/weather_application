package reschikov.geekbrains.androidadvancedlevel.weatherapplication.data.network

import android.content.Context
import android.content.Context.MODE_PRIVATE
import android.content.SharedPreferences
import reschikov.geekbrains.androidadvancedlevel.weatherapplication.KEY_LANG
import reschikov.geekbrains.androidadvancedlevel.weatherapplication.R
import reschikov.geekbrains.androidadvancedlevel.weatherapplication.RU
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

private const val PREFERENCES_REQUEST = "request"

abstract class RequestBaseProvider(context: Context) : NetworkBaseProvider(context) {

    protected val preferences: SharedPreferences = context.getSharedPreferences(PREFERENCES_REQUEST, MODE_PRIVATE)
    protected val lang: String = if (preferences.getBoolean(KEY_LANG, false)) RU else context.getString(R.string.str_en)
    protected lateinit var client: Retrofit

    protected fun createClient(url: String){
        client = Retrofit.Builder()
                .baseUrl(url)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
    }
}