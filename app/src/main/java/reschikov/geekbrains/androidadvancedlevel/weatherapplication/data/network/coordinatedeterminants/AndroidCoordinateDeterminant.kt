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
import reschikov.geekbrains.androidadvancedlevel.weatherapplication.domain.AppException
import timber.log.Timber
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

private const val DISTANCE = 0.0f

class AndroidCoordinateDeterminant(private val context: Context) : BaseCoordinateDeterminant(){

    private val criteria = Criteria()
    private val lm: LocationManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
    private lateinit var locationListener: LocationListener

    override fun isGoogleDefined(): Boolean = false

    override suspend fun determineCoordinates(): Location  {
        return suspendCoroutine {continuation ->
            if (ActivityCompat.checkSelfPermission(context,
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(context,
                Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                continuation.resumeWithException(AppException.NoPermission())
            } else {
                locationListener = object : LocationListener {
                    override fun onLocationChanged(location: Location?) {
                        Timber.i("Android $location")
                        location?.let {
                            if (it.accuracy <= setAccuracy) {
                                lm.removeUpdates(this)
                                continuation.resume(it)
                            } else setPeriod += setPeriod
                        }
                    }

                    override fun onStatusChanged(provider: String, status: Int, extras: Bundle) {}

                    override fun onProviderEnabled(provider: String) {}

                    override fun onProviderDisabled(provider: String) {
                        if (provider == LocationManager.NETWORK_PROVIDER) {
                            lm.removeUpdates(this)
                            continuation.resumeWithException(AppException.NoNetwork())
                        }
                    }
                }
                criteria.accuracy = Criteria.ACCURACY_MEDIUM
                val provider = lm.getBestProvider(criteria, true) ?: LocationManager.NETWORK_PROVIDER
                lm.requestLocationUpdates(provider, setPeriod, DISTANCE, locationListener, Looper.getMainLooper())
                Timber.i("Android lm.requestLocationUpdates")
            }
        }
    }
}