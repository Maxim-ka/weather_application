package reschikov.geekbrains.androidadvancedlevel.weatherapplication

import androidx.collection.arraySetOf
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.Parameterized
import kotlin.math.abs

@RunWith(Parameterized::class)
class TestPickColor(private val inV: Double, private val outV: Int) {

    companion object {
        @Parameterized.Parameters
        @JvmStatic
        fun  data() : Collection<Array<Any>>{
            return listOf (
                    arrayOf(-35.05, -35),
                    arrayOf(-34.99, -30),
                    arrayOf(-25.09, -25),
                    arrayOf(-20.01, -20),
                    arrayOf(-18.99, -15),
                    arrayOf(-16.0, -15),
                    arrayOf(-12.0, -10),
                    arrayOf(-9.0, -5),
                    arrayOf(0.0, 0),
                    arrayOf(2.0, 5),
                    arrayOf(5.01, 10),
                    arrayOf(12.0, 15),
                    arrayOf(20.0, 20),
                    arrayOf(24.99, 25),
                    arrayOf(30.0, 30),
                    arrayOf(34.0, 35),
                    arrayOf(39.0, 40),
                    arrayOf(41.0, -1),
                    arrayOf(-42.0, -35)
                    )

        }
    }

    private fun pickColor(temp: Double) : Int{
        val set = arraySetOf(-35, -30, -25, -20, -15, -10, -5, 0, 5, 10, 15, 20, 25, 30, 35, 40)
        var lowIndex = 0
        var highIndex = set.size - 1
        if (set.elementAt(lowIndex) >= temp)  return set.elementAt(lowIndex)
        if (set.elementAt(highIndex) < temp) return -1
        while (highIndex - lowIndex > 1){
            val middleIndex = ((lowIndex + highIndex) * HALF).toInt()
            if (abs(set.elementAt(middleIndex) - temp) <= 0.001f) return set.elementAt(middleIndex)
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

    @Test
    fun pickColorTest(){
        Assert.assertEquals(outV, pickColor(inV))
    }
}