package reschikov.geekbrains.androidadvancedlevel.weatherapplication.data.network.coordinatedeterminants

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.Criteria
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import android.os.Looper
import androidx.core.app.ActivityCompat
import kotlinx.coroutines.cancelChildren
import reschikov.geekbrains.androidadvancedlevel.weatherapplication.domain.AppException
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

private const val DISTANCE = 0.0f

class AndroidCoordinateDeterminant(private var context: Context?) : BaseCoordinateDeterminant(context){

    private var lm: LocationManager? = context?.getSystemService(Context.LOCATION_SERVICE) as LocationManager
    private val provider by lazy {
        lm?.getBestProvider(Criteria().apply { accuracy = Criteria.ACCURACY_MEDIUM },
            true) ?: LocationManager.NETWORK_PROVIDER
    }
    private var locationListener: LocationListener? = null

    override fun isGoogleDefined(): Boolean = false

    override suspend fun determineCoordinates(): Location  {
        return suspendCoroutine {continuation ->
            context?.let {
                if (ActivityCompat.checkSelfPermission(it,
                        Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                    ActivityCompat.checkSelfPermission(it,
                        Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    continuation.resumeWithException(AppException.NoPermission())
                } else {
                    val prov = this.provider
                    locationListener = object : LocationListener {
                        override fun onLocationChanged(location: Location?) {
                            location?.let {locate ->
                                if (locate.accuracy <= setAccuracy) {
                                    lm?.removeUpdates(this)
                                    continuation.resume(locate)
                                } else setPeriod += setPeriod
                            }
                        }

                        override fun onStatusChanged(provider: String, status: Int, extras: Bundle) {}

                        override fun onProviderEnabled(provider: String) {}

                        override fun onProviderDisabled(provider: String) {
                            if (prov == provider) {
                                continuation.resumeWithException(Throwable(strNoNetwork))
                            }
                        }
                    }

                    locationListener?.let {listener ->
                        lm?.requestLocationUpdates(provider, setPeriod, DISTANCE, listener, Looper.getMainLooper())
                    }
                }
            }
        }
    }

    override fun removeListener() {
        locationListener?.let {
            lm?.removeUpdates(it)
        }
    }

    override fun terminate() {
        locationListener = null
        lm = null
        context = null
        coroutineContext.cancelChildren()
    }
}