package reschikov.geekbrains.androidadvancedlevel.weatherapplication.data.network.coordinatedeterminants

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import androidx.core.app.ActivityCompat
import com.google.android.gms.location.*
import com.google.android.gms.tasks.Task
import reschikov.geekbrains.androidadvancedlevel.weatherapplication.domain.AppException
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

class GoogleCoordinateDeterminant(private val context: Context) : BaseCoordinateDeterminant(context){

    private val locationRequest = LocationRequest.create().apply {
        interval = setPeriod
        priority = LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY
    }
    private val builder = LocationSettingsRequest.Builder()
            .addLocationRequest(locationRequest)
            .setAlwaysShow(true)

    private var locationProviderClient: FusedLocationProviderClient?  = LocationServices.getFusedLocationProviderClient(context)
    private var lcb: LocationCallback? = null
    private lateinit var  taskSetting: Task<LocationSettingsResponse>

    override fun isGoogleDefined(): Boolean = true

    override suspend fun determineCoordinates(): Location {
        return suspendCoroutine {continuation ->
            checkParameters()
            taskSetting.addOnSuccessListener {
                if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                    ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    continuation.resumeWithException(AppException.NoPermission())
                } else {
                    lcb = object : LocationCallback() {
                        override fun onLocationResult(locationResult: LocationResult?) {
                            locationResult?.let {
                                val location = it.lastLocation
                                if (location.accuracy <= setAccuracy) {
                                    locationProviderClient?.removeLocationUpdates(this)
                                    continuation.resume(location)
                                } else {
                                    setAccuracy += setAccuracy
                                }
                            }
                        }

                        override fun onLocationAvailability(p0: LocationAvailability?) {
                            p0?.let {pO -> takeUnless { pO.isLocationAvailable}?.run {
                                    locationProviderClient?.removeLocationUpdates(lcb)
                                    lcb = null
                                    locationProviderClient = null
                                    continuation.resumeWithException(Throwable())
                                }
                            }
                        }
                    }
                    val task = locationProviderClient?.requestLocationUpdates(locationRequest, lcb, null)

                    task?.addOnFailureListener { e ->
                        locationProviderClient?.removeLocationUpdates(lcb)
                        continuation.resumeWithException(e)
                    }
                }
            }

            taskSetting.addOnFailureListener { exception ->
                continuation.resumeWithException(exception)
            }
        }
    }

    private fun checkParameters(){
        taskSetting = LocationServices.getSettingsClient(context)
                .checkLocationSettings(builder.build())
    }
}