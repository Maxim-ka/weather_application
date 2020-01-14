package reschikov.geekbrains.androidadvancedlevel.weatherapplication.unit

import reschikov.geekbrains.androidadvancedlevel.weatherapplication.RU
import reschikov.geekbrains.androidadvancedlevel.weatherapplication.data.network.model.data.openweather.current.Wind
import java.math.RoundingMode
import java.util.*

private const val FROM_HPA_IN_MMHG = 0.750064f
private const val SCALE = 2

fun setPressure(press: Int)= (press * FROM_HPA_IN_MMHG).convertToSize(SCALE).toString()

fun setWindDirection(wind: Wind): String?{
    var rhumb: String? = null
    if (wind.speed > 0) {
        var horizonsOfRhumb = getRhumb(wind.deg)
        if (Locale.getDefault().language == RU) horizonsOfRhumb = translationIntoRussian(horizonsOfRhumb)
        rhumb = horizonsOfRhumb
    }
    return rhumb
}

fun getRhumb(deg: Int): String{
    if (deg == 0) return "N"
    if (deg < 90) return "NE"
    if (deg == 90) return "E"
    if (deg < 180) return "SE"
    if (deg == 180) return "S"
    if (deg < 270) return "SW"
    if (deg == 270) return "W"
    return "NW"
}

fun translationIntoRussian(string: String): String {
    val sb = StringBuilder()
    val chars = string.toCharArray()
    for (i in chars.indices) {
        when (chars[i]) {
            'N' -> sb.append('C')
            'E' -> sb.append('В')
            'S' -> sb.append('Ю')
            'W' -> sb.append('З')
        }
    }
    return sb.toString()
}

fun Float.convertToSize(decimalPlace: Int): Float{
    return run {
        toBigDecimal().setScale(decimalPlace, RoundingMode.HALF_UP).toFloat()
    }
}