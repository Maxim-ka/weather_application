package reschikov.geekbrains.androidadvancedlevel.weatherapplication

import androidx.collection.arraySetOf
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.Parameterized

@RunWith(Parameterized::class)
class TestConversionFromDegreesToRumba(private val inV: Int, private val outV: Int) {

    companion object {
        @Parameterized.Parameters
        @JvmStatic
        fun  data() : Collection<Array<Int>>{
            return listOf (
                    arrayOf(0, 0),
                    arrayOf(10, 0),
                    arrayOf(15, 15),
                    arrayOf(55, 15),
                    arrayOf(75, 75),
                    arrayOf(90, 75),
                    arrayOf(105, 105),
                    arrayOf(135, 105),
                    arrayOf(165, 165),
                    arrayOf(180, 165),
                    arrayOf(195, 195),
                    arrayOf(220, 195),
                    arrayOf(255, 255),
                    arrayOf(270, 255),
                    arrayOf(285, 285),
                    arrayOf(300, 285),
                    arrayOf(344, 285),
                    arrayOf(345, 345),
                    arrayOf(359, 345)
            )
        }
    }

    private fun convertFromDegreesToRumba(deg: Int) : Int{
        val set = arraySetOf(0, 15, 75, 105, 165, 195, 255, 285, 345)
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

    @Test
    fun convertFromDegreesToRumbaTest(){
        Assert.assertEquals(outV, convertFromDegreesToRumba(inV))
    }
}