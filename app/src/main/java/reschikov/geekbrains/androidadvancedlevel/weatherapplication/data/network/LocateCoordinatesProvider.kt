package reschikov.geekbrains.androidadvancedlevel.weatherapplication.data.network

import android.content.Context
import com.google.android.gms.common.GoogleApiAvailability
import com.google.android.gms.common.api.CommonStatusCodes.SUCCESS
import com.google.android.gms.common.api.ResolvableApiException
import reschikov.geekbrains.androidadvancedlevel.weatherapplication.R
import reschikov.geekbrains.androidadvancedlevel.weatherapplication.data.IssuedCoordinates
import reschikov.geekbrains.androidadvancedlevel.weatherapplication.data.network.coordinatedeterminants.DeterminedCoordinates
import reschikov.geekbrains.androidadvancedlevel.weatherapplication.domain.BaseException
import reschikov.geekbrains.androidadvancedlevel.weatherapplication.domain.Place

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

    override suspend fun getCoordinatesCurrentPlace(): Place.Coordinates {
        if (checkLackOfNetwork()) return Place.Coordinates(null, BaseException.NoNetwork())
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

    private suspend fun getGoogleCoordinates(): Place.Coordinates? {
        for(google in googleQualifier){
            google.getCoordinates().also {
                if (hasPlace(it)) return it
            }
        }
        return null
    }

    private fun hasPlace(coordinates: Place.Coordinates): Boolean{
        return (coordinates.coord != null || coordinates.error is BaseException.NoPermission ||
                coordinates.error is ResolvableApiException)
    }

    private suspend fun getDeterminantsCoordinates(): Place.Coordinates{
        if (determinants.isNotEmpty()){
            for (determinant in determinants){
                determinant.getCoordinates().also {
                    if (hasPlace(it)) return it
                }
            }
        }
        return Place.Coordinates(null, Throwable(context.getString(R.string.unable_determine_coordinates)))
    }
}