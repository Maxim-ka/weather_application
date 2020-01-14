package reschikov.geekbrains.androidadvancedlevel.weatherapplication.data.network

import android.content.Context
import android.net.ConnectivityManager
import android.os.Build
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlin.coroutines.CoroutineContext

abstract class NetworkBaseProvider(private val context: Context) : CoroutineScope {

    override val coroutineContext: CoroutineContext by lazy {
        Dispatchers.IO + Job()
    }

    protected fun checkLackOfNetwork(): Boolean {
        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            return connectivityManager.activeNetwork == null
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP){
            return !connectivityManager.isDefaultNetworkActive
        }
        val networkInfo = connectivityManager .activeNetworkInfo
        return networkInfo == null || !networkInfo.isConnectedOrConnecting
    }
}