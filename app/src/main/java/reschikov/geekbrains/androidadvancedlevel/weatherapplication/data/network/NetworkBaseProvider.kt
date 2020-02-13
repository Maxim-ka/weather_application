package reschikov.geekbrains.androidadvancedlevel.weatherapplication.data.network

import android.content.Context
import android.net.ConnectivityManager
import android.os.Build
import reschikov.geekbrains.androidadvancedlevel.weatherapplication.R

abstract class NetworkBaseProvider(context: Context){

    protected val strNoNetwork: String by lazy {
        context.getString(R.string.err_no_network)
    }

    private  val connectivityManager : ConnectivityManager by lazy {
        context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    }

    protected fun checkLackOfNetwork(): Boolean {
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