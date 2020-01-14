package reschikov.geekbrains.androidadvancedlevel.weatherapplication.unit

import android.content.Context
import androidx.core.content.ContextCompat
import reschikov.geekbrains.androidadvancedlevel.weatherapplication.R

class TemperatureColor (val context: Context) : TintableTemperature{

    private enum class Temperature constructor(val temp: Int, val color: Int) {
        COLDNESS(-35, R.color.coldness), VERY_COLD(-25, R.color.very_cold),
        MODERATELY_COLD(-20, R.color.moderately_cold), COLD(-10, R.color.cold),
        COOL(5, R.color.cool), GOOD(10, R.color.good), NORM(15, R.color.norm),
        HEAT(20, R.color.heat), WARMER(25, R.color.warmer),
        VERY_WARM(30, R.color.very_warm), HOT(35, R.color.hot),
        VERY_HOT(40, R.color.very_hot)
    }

    override fun getTemperatureColor(vararg temperatures: Double): Int{
        return ContextCompat.getColor(context, getResourceTemperatureColor(temperatures))
    }

    private fun getResourceTemperatureColor(temperatures: DoubleArray): Int {
        val temp: Double = if (temperatures.size > 1){
            temperatures.sum() / temperatures.size
        } else temperatures.first()
        for (i in Temperature.values().indices) {
            if (temp <= Temperature.values()[i].temp)
                return Temperature.values()[i].color
        }
        return R.color.roasting
    }
}



