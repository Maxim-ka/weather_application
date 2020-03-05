package reschikov.geekbrains.androidadvancedlevel.weatherapplication.data.network.coordinatedeterminants

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import androidx.core.app.ActivityCompat
import com.google.android.gms.location.*
import com.google.android.gms.tasks.Task
import kotlinx.coroutines.cancelChildren
import reschikov.geekbrains.androidadvancedlevel.weatherapplication.domain.AppException
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

class GoogleCoordinateDeterminant(private var context: Context?) : BaseCoordinateDeterminant(context){

    private val locationRequest = LocationRequest.create().apply {
        interval = setPeriod
        priority = LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY
    }
    private val builder = LocationSettingsRequest.Builder()
            .addLocationRequest(locationRequest)
            .setAlwaysShow(true)

    private var locationProviderClient: FusedLocationProviderClient?  = context?.let { LocationServices.getFusedLocationProviderClient(it) }
    private var lcb: LocationCallback? = null
    private var  taskSetting: Task<LocationSettingsResponse>? = null

    override fun isGoogleDefined(): Boolean = true

    override suspend fun determineCoordinates(): Location {
        return suspendCoroutine {continuation ->
            checkParameters()
            taskSetting?.addOnSuccessListener {
                context?.let {
                    if (ActivityCompat.checkSelfPermission(it,
                        Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                        ActivityCompat.checkSelfPermission(it,
                        Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                        continuation.resumeWithException(AppException.NoPermission())
                    } else {
                        lcb = object : LocationCallback() {
                            override fun onLocationResult(locationResult: LocationResult?) {
                                locationResult?.let {result ->
                                    val location = result.lastLocation
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
                                        continuation.resumeWithException(Throwable())
                                    }
                                }
                            }
                        }
                        val task = locationProviderClient?.requestLocationUpdates(locationRequest, lcb, null)

                        task?.addOnFailureListener { e ->
                            continuation.resumeWithException(e)
                        }
                    }
                }
            }

            taskSetting?.addOnFailureListener { exception ->
                continuation.resumeWithException(exception)
            }
        }
    }

    private fun checkParameters(){
        taskSetting = context?.let { LocationServices.getSettingsClient(it)
                .checkLocationSettings(builder.build()) }
    }

    override fun removeListener() {
        lcb?.let {
            locationProviderClient?.removeLocationUpdates(it)
        }
    }

    override fun terminate() {
        locationProviderClient = null
        lcb = null
        taskSetting = null
        context = null
        coroutineContext.cancelChildren()
    }
}