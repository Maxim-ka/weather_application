package reschikov.geekbrains.androidadvancedlevel.weatherapplication.unit

import android.content.Context
import androidx.collection.ArrayMap
import androidx.collection.arrayMapOf
import org.xmlpull.v1.XmlPullParser
import reschikov.geekbrains.androidadvancedlevel.weatherapplication.*
import reschikov.geekbrains.androidadvancedlevel.weatherapplication.data.Windy
import reschikov.geekbrains.androidadvancedlevel.weatherapplication.data.network.model.data.openweather.current.Wind
import java.math.RoundingMode

private const val ATTRIBUTE_DEGREES = "degrees"
private const val ATTRIBUTE_DIRECTION = "string"
private const val PREFIX = "@"

fun setPressure(press: Int)= (press * FROM_HPA_IN_MMHG).convertToSize(SCALE).toString()

fun Float.convertToSize(decimalPlace: Int): Float{
    return run {
        toBigDecimal().setScale(decimalPlace, RoundingMode.HALF_UP).toFloat()
    }
}

class WindDirection(val context: Context) : Windy{

    private val arrDirection: ArrayMap<Int, String> by lazy {
        createArrDirection()
    }

    private fun createArrDirection() : ArrayMap<Int, String> {
        val arr = arrayMapOf<Int, String>()
        val parser = context.resources.getXml(R.xml.degress_rumb)
        while (parser.eventType !=  XmlPullParser.END_DOCUMENT){
            if (parser.eventType == XmlPullParser.START_TAG && parser.name == TAG_ENTRY){
                val deg = (parser.getAttributeValue(null, ATTRIBUTE_DEGREES)).toInt()
                val direction = parser.getAttributeValue(null, ATTRIBUTE_DIRECTION)
                        .removePrefix(PREFIX)
                        .toInt()
                arr[deg] = context.getString(direction)
            }
            parser.next()
        }
        return arr
    }

    override fun identifyWind(wind: Wind?): String{
        return wind?.let {"${ it.takeIf { it.speed > 0 }?.let { wind ->
            getRhumb(wind.deg)} }  ${it.speed}"} ?: " $NAN_FLOAT"
    }

    private fun getRhumb(deg: Int): String?{
        return arrDirection[convertFromDegreesToRumba(deg)]
    }

    private fun convertFromDegreesToRumba(deg: Int) : Int{
        val set = arrDirection.keys
        var lowIndex = 0
        var highIndex = set.size - 1
        if (set.elementAt(lowIndex) == deg)  return set.elementAt(lowIndex)
        if (set.elementAt(highIndex) <= deg) return set.elementAt(highIndex)
        while (highIndex - lowIndex > 1){
            val middleIndex = ((lowIndex + highIndex) * HALF).toInt()
            if (set.elementAt(middleIndex) == deg) return set.elementAt(middleIndex)
            if (set.elementAt(middleIndex) < deg){
                if (set.elementAt(middleIndex + 1) > deg) return set.elementAt(middleIndex)
                lowIndex = middleIndex
            }
            if (set.elementAt(middleIndex) > deg){
                if (set.elementAt(middleIndex - 1) <= deg) return set.elementAt(middleIndex - 1)
                highIndex = middleIndex
            }
        }
        return set.elementAt(lowIndex)
    }
}