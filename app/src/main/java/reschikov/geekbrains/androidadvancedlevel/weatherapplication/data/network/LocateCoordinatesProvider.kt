package reschikov.geekbrains.androidadvancedlevel.weatherapplication.data.network

import android.content.Context
import com.google.android.gms.common.GoogleApiAvailability
import com.google.android.gms.common.api.CommonStatusCodes.SUCCESS
import com.google.android.gms.common.api.ResolvableApiException
import reschikov.geekbrains.androidadvancedlevel.weatherapplication.data.IssuedCoordinates
import reschikov.geekbrains.androidadvancedlevel.weatherapplication.data.network.coordinatedeterminants.DeterminedCoordinates
import reschikov.geekbrains.androidadvancedlevel.weatherapplication.data.network.request.command.GetByCoordinates
import reschikov.geekbrains.androidadvancedlevel.weatherapplication.domain.AppException

class LocateCoordinatesProvider(context: Context, vararg determinedCoordinates: DeterminedCoordinates) :
        NetworkBaseProvider(context), IssuedCoordinates {

    private val isGoogleUsed: Boolean =  GoogleApiAvailability.getInstance()
            .isGooglePlayServicesAvailable(context) == SUCCESS
    private lateinit var  googleQualifier : DeterminedCoordinates
    private lateinit var determinants : DeterminedCoordinates

    init {
        for (determinant in determinedCoordinates){
            if (determinant.isGoogleDefined()) googleQualifier = determinant
            else determinants = determinant
        }
    }

    override suspend fun getCoordinatesCurrentPlace(): Pair<GetByCoordinates?, Throwable?> {
        if (checkLackOfNetwork()) return Pair(null, Throwable(strNoNetwork))
        if(isGoogleUsed){
            googleQualifier.getCoordinates().also {
                if (hasPlace(it)) return@getCoordinatesCurrentPlace it
            }
        }
        return determinants.getCoordinates()
    }

    private fun hasPlace(coordinates: Pair<GetByCoordinates?, Throwable?>): Boolean{
        return coordinates.run {
            first != null || second is AppException.NoPermission ||
                    second is ResolvableApiException
        }
    }

    override fun toClose() {
        googleQualifier.toClose()
        determinants.toClose()
    }
}