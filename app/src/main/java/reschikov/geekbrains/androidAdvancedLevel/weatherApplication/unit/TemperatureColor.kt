package reschikov.geekbrains.androidadvancedlevel.weatherapplication.unit

import android.content.Context
import android.graphics.Color
import androidx.collection.ArrayMap
import androidx.collection.arrayMapOf
import org.xmlpull.v1.XmlPullParser
import reschikov.geekbrains.androidadvancedlevel.weatherapplication.HALF
import reschikov.geekbrains.androidadvancedlevel.weatherapplication.NAN
import reschikov.geekbrains.androidadvancedlevel.weatherapplication.R
import reschikov.geekbrains.androidadvancedlevel.weatherapplication.TAG_ENTRY
import reschikov.geekbrains.androidadvancedlevel.weatherapplication.data.TintableTemperature
import kotlin.math.abs

private const val ATTRIBUTE_TEMPERATURE = "temperature"
private const val ATTRIBUTE_COLOR = "color"
private const val WHITE = "#FFFFFF"
private const val ROASTING = "#F78181"
private const val ACCURACY = 0.001f

class TemperatureColor (val context: Context) : TintableTemperature {

    private val arrColor: ArrayMap<Int, String> by lazy {
       createArrColor()
    }

    private fun createArrColor() : ArrayMap<Int, String>{
        val arr = arrayMapOf<Int, String>()
        val parser = context.resources.getXml(R.xml.temperature_color)
        while (parser.eventType !=  XmlPullParser.END_DOCUMENT){
            if (parser.eventType == XmlPullParser.START_TAG && parser.name == TAG_ENTRY){
                val temp = (parser.getAttributeValue(null, ATTRIBUTE_TEMPERATURE)).toInt()
                val color = parser.getAttributeValue(null, ATTRIBUTE_COLOR)
                arr[temp] = color
            }
            parser.next()
        }
        return arr
    }

    private val averageTemp = {temperatures: DoubleArray ->
        temperatures.run { takeIf { it.size > 1}?.let{
            it.sum() / it.size} ?: first() }
    }

    override fun setTemperatureColor(vararg temperatures: Double): Int{
        return Color.parseColor(getResourceTemperatureColor(temperatures))
    }

    private fun getResourceTemperatureColor(temperatures: DoubleArray): String {
        val temp = averageTemp.invoke(temperatures)
        val key = pickColor(temp)
        if (key == NAN) return ROASTING
        return arrColor[key]?.let { it } ?: WHITE
    }

    private fun pickColor(temp: Double) : Int{
        val set = arrColor.keys
        var lowIndex = 0
        var highIndex = set.size - 1
        if (set.elementAt(lowIndex) >= temp)  return set.elementAt(lowIndex)
        if (set.elementAt(highIndex) < temp) return NAN
        while (highIndex - lowIndex > 1){
            val middleIndex = ((lowIndex + highIndex) * HALF).toInt()
            if (abs(set.elementAt(middleIndex) - temp) <= ACCURACY) return set.elementAt(middleIndex)
            if (set.elementAt(middleIndex) < temp){
                if (set.elementAt(middleIndex + 1) >= temp) return set.elementAt(middleIndex + 1)
                lowIndex = middleIndex
            }
            if (set.elementAt(middleIndex) > temp){
                if (set.elementAt(middleIndex - 1) <= temp) return set.elementAt(middleIndex)
                highIndex = middleIndex
            }
        }
        return set.elementAt(highIndex)
    }
}



