package reschikov.geekbrains.androidadvancedlevel.weatherapplication.ui.sensors

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import kotlinx.android.synthetic.main.sensor_frame.*
import reschikov.geekbrains.androidadvancedlevel.weatherapplication.C
import reschikov.geekbrains.androidadvancedlevel.weatherapplication.FROM_HPA_IN_MMHG
import reschikov.geekbrains.androidadvancedlevel.weatherapplication.R
import reschikov.geekbrains.androidadvancedlevel.weatherapplication.SCALE
import reschikov.geekbrains.androidadvancedlevel.weatherapplication.unit.convertToSize


private const val INTERVAL_MIC_SEC = 15_000_000

class FragmentSensors : Fragment(R.layout.sensor_frame), SensorEventListener {

    private val navController : NavController by lazy { findNavController() }
    private var sm: SensorManager? = null
    private var pressureMeter: Sensor? = null
    private var temperatureSensor: Sensor? = null
    private var humiditySensor: Sensor? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        activity?.let { sm = it.getSystemService(Context.SENSOR_SERVICE) as SensorManager
            getSensors()
        }
    }

    private fun getSensors(){
        sm?.let {
            pressureMeter = it.getDefaultSensor(Sensor.TYPE_PRESSURE)
            cv_pressureMeter.setMissing(pressureMeter == null)
            temperatureSensor = it.getDefaultSensor(Sensor.TYPE_AMBIENT_TEMPERATURE)
            cv_temperatureSensor.setMissing(temperatureSensor == null)
            humiditySensor = it.getDefaultSensor(Sensor.TYPE_RELATIVE_HUMIDITY)
            cv_humiditySensor.setMissing(humiditySensor == null)
        }
    }

    override fun onPrepareOptionsMenu(menu: Menu) {
        super.onPrepareOptionsMenu(menu)
        menu.clear()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            android.R.id.home ->{
                navController.navigate(R.id.action_fragmentSensors_to_fragmentWeather)
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onResume() {
        super.onResume()
        sm?.let {sm ->
            pressureMeter?.let { sm.registerListener(this, it, INTERVAL_MIC_SEC)}
            temperatureSensor?.let { sm.registerListener(this, it, INTERVAL_MIC_SEC)}
            humiditySensor?.let { sm.registerListener(this, it, INTERVAL_MIC_SEC)}
        }
    }

    override fun onPause() {
        super.onPause()
        sm?.unregisterListener(this)
    }

    override fun onSensorChanged(event: SensorEvent) {
        context?.let {
            if (event.sensor == pressureMeter) {
                val pressure: Float = (event.values[0] * FROM_HPA_IN_MMHG).convertToSize(SCALE)
                cv_pressureMeter.setSensorValue( "$pressure, ${getString(R.string.mm_Hg)}")
                return
            }
            if (event.sensor == temperatureSensor) {
                cv_temperatureSensor.setSensorValue("${event.values[0]}, $C")
                return
            }
            if (event.sensor == humiditySensor) {
                cv_humiditySensor.setSensorValue("${event.values[0]}, ${getString(R.string.pct)}")
            }
        }
    }

    override fun onAccuracyChanged(sensor: Sensor, accuracy: Int) {
        if (sensor == pressureMeter && isPoorAccuracy(accuracy)) {
            cv_pressureMeter.setSensorValue(getString(R.string.absent))
            cv_pressureMeter.setMissing(true)
            return
        }
        if (sensor == temperatureSensor && isPoorAccuracy(accuracy)) {
            cv_temperatureSensor.setSensorValue(getString(R.string.absent))
            cv_temperatureSensor.setMissing(true)
            return
        }
        if (sensor == humiditySensor && isPoorAccuracy(accuracy)) {
            cv_humiditySensor.setSensorValue(getString(R.string.absent))
            cv_humiditySensor.setMissing(true)
        }
    }

    private fun isPoorAccuracy(accuracy: Int): Boolean{
        return accuracy == SensorManager.SENSOR_STATUS_ACCURACY_LOW ||
                accuracy == SensorManager.SENSOR_STATUS_UNRELIABLE
    }
}