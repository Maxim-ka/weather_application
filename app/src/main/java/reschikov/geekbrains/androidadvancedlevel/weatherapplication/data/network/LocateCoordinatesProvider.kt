package reschikov.geekbrains.androidadvancedlevel.weatherapplication.data.network

import android.content.Context
import com.google.android.gms.common.GoogleApiAvailability
import com.google.android.gms.common.api.CommonStatusCodes.SUCCESS
import com.google.android.gms.common.api.ResolvableApiException
import reschikov.geekbrains.androidadvancedlevel.weatherapplication.R
import reschikov.geekbrains.androidadvancedlevel.weatherapplication.data.IssuedCoordinates
import reschikov.geekbrains.androidadvancedlevel.weatherapplication.data.network.coordinatedeterminants.DeterminedCoordinates
import reschikov.geekbrains.androidadvancedlevel.weatherapplication.data.network.request.command.GetByCoordinates
import reschikov.geekbrains.androidadvancedlevel.weatherapplication.domain.AppException

class LocateCoordinatesProvider(private val context: Context, vararg determinedCoordinates: DeterminedCoordinates) :
        NetworkBaseProvider(context), IssuedCoordinates {

    private val googleQualifier = mutableListOf<DeterminedCoordinates>()
    private val determinants = mutableListOf<DeterminedCoordinates>()

    init {
        for (determinant in determinedCoordinates){
            if (determinant.isGoogleDefined()) googleQualifier.add(determinant)
            else determinants.add(determinant)
        }
    }

    override suspend fun getCoordinatesCurrentPlace(): Pair<GetByCoordinates?, Throwable?> {
        if (checkLackOfNetwork()) return Pair(null, Throwable(strNoNetwork))
        if(hasDetermineGoogle() && googleQualifier.isNotEmpty()){
            getGoogleCoordinates()?.let { return it }
        }
        return getDeterminantsCoordinates()
    }

    private fun hasDetermineGoogle(): Boolean{
        return  GoogleApiAvailability
                .getInstance()
                .isGooglePlayServicesAvailable(context) == SUCCESS
    }

    private suspend fun getGoogleCoordinates(): Pair<GetByCoordinates?, Throwable?>? {
        for(google in googleQualifier){
            google.getCoordinates().also {
                if (hasPlace(it)) return it
            }
        }
        return null
    }

    private fun hasPlace(coordinates: Pair<GetByCoordinates?, Throwable?>): Boolean{
        return coordinates.run {
            first != null || second is AppException.NoPermission ||
                    second is ResolvableApiException
        }
    }

    private suspend fun getDeterminantsCoordinates(): Pair<GetByCoordinates?, Throwable?>{
        if (determinants.isNotEmpty()){
            for (determinant in determinants){
                determinant.getCoordinates().also {
                    if (hasPlace(it)) return it
                }
            }
        }
        return Pair(null, Throwable(context.getString(R.string.err_unable_determine_coordinates)))
    }
}