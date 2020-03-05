package reschikov.geekbrains.androidadvancedlevel.weatherapplication.data.network

import android.content.Context
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.*
import kotlin.coroutines.CoroutineContext

abstract class RequestBaseProvider(context: Context) : NetworkBaseProvider(context), CoroutineScope {

    override val coroutineContext: CoroutineContext by lazy { Dispatchers.IO + Job() }
    protected val lang: String = Locale.getDefault().language
    protected lateinit var client: Retrofit

    protected fun createClient(url: String){
        client = Retrofit.Builder()
                .baseUrl(url)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
    }
}