package reschikov.geekbrains.androidadvancedlevel.weatherapplication.ui.sensors

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import kotlinx.android.synthetic.main.sensor_frame.*
import reschikov.geekbrains.androidadvancedlevel.weatherapplication.*
import reschikov.geekbrains.androidadvancedlevel.weatherapplication.unit.convertToSize


private const val INTERVAL_MIC_SEC = 15_000_000

class FragmentSensors : Fragment(), SensorEventListener {

    private lateinit var sm: SensorManager
    private var pressureMeter: Sensor? = null
    private var temperatureSensor: Sensor? = null
    private var humiditySensor: Sensor? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.sensor_frame, container, false)
        sm = view.context.getSystemService(Context.SENSOR_SERVICE) as SensorManager
        setHasOptionsMenu(true)
        return view
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        initSensors()
    }

    private fun initSensors(){
        sm.run {
            pressureMeter = getDefaultSensor(Sensor.TYPE_PRESSURE)
            cv_pressureMeter.setMissing(pressureMeter == null)
            temperatureSensor = getDefaultSensor(Sensor.TYPE_AMBIENT_TEMPERATURE)
            cv_temperatureSensor.setMissing(temperatureSensor == null)
            humiditySensor = getDefaultSensor(Sensor.TYPE_RELATIVE_HUMIDITY)
            cv_humiditySensor.setMissing(humiditySensor == null)
        }
    }

    override fun onPrepareOptionsMenu(menu: Menu) {
        menu.clear()
    }

    override fun onResume() {
        super.onResume()
        sm.also {sm ->
            pressureMeter?.let { sm.registerListener(this, it, INTERVAL_MIC_SEC)}
            temperatureSensor?.let { sm.registerListener(this, it, INTERVAL_MIC_SEC)}
            humiditySensor?.let { sm.registerListener(this, it, INTERVAL_MIC_SEC)}
        }
    }

    override fun onPause() {
        super.onPause()
        sm.unregisterListener(this)
    }

    override fun onSensorChanged(event: SensorEvent) {
        context?.let {
            if (event.sensor == pressureMeter) {
                val pressure: Float = (event.values[FIRST_ELEMENT] * FROM_HPA_IN_MMHG).convertToSize(SCALE)
                cv_pressureMeter.setSensorValue( "$pressure, ${getString(R.string.mm_Hg)}")
                return
            }
            if (event.sensor == temperatureSensor) {
                cv_temperatureSensor.setSensorValue("${event.values[FIRST_ELEMENT]}, $C")
                return
            }
            if (event.sensor == humiditySensor) {
                cv_humiditySensor.setSensorValue("${event.values[FIRST_ELEMENT]}, ${getString(R.string.pct)}")
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