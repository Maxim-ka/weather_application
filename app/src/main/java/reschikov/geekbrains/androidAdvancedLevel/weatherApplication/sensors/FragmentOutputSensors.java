package reschikov.geekbrains.androidadvancedlevel.weatherapplication.sensors;

import android.annotation.SuppressLint;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.Locale;

import reschikov.geekbrains.androidadvancedlevel.weatherapplication.R;
import reschikov.geekbrains.androidadvancedlevel.weatherapplication.Rules;

import static android.content.Context.SENSOR_SERVICE;

public class FragmentOutputSensors extends Fragment implements SensorEventListener {


    private SensorManager sm;
    private Sensor pressureMeter;
    private Sensor temperatureSensor;
    private Sensor humiditySensor;
    private CustomView cvPressure;
    private CustomView cvTemperature;
    private CustomView cvHumidity;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.sensor_frame, container, false);
        cvPressure = view.findViewById(R.id.customView_pressureMeter);
        cvTemperature = view.findViewById(R.id.customView_temperatureSensor);
        cvHumidity = view.findViewById(R.id.customView_humiditySensor);
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (getActivity() != null){
            sm = (SensorManager) getActivity().getSystemService(SENSOR_SERVICE);
            pressureMeter = sm.getDefaultSensor(Sensor.TYPE_PRESSURE);
            if (pressureMeter != null) sm.registerListener(this, pressureMeter, SensorManager.SENSOR_DELAY_NORMAL);
            else cvPressure.setMissing(true);
            temperatureSensor = sm.getDefaultSensor(Sensor.TYPE_AMBIENT_TEMPERATURE);
            if (temperatureSensor != null) sm.registerListener(this, temperatureSensor, SensorManager.SENSOR_DELAY_NORMAL);
            else cvTemperature.setMissing(true);
            humiditySensor = sm.getDefaultSensor(Sensor.TYPE_RELATIVE_HUMIDITY);
            if (humiditySensor != null) sm.registerListener(this, humiditySensor,SensorManager.SENSOR_DELAY_NORMAL);
            else cvHumidity.setMissing(true);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        sm.unregisterListener(this);
    }

    @SuppressLint("DefaultLocale")
    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor == pressureMeter){
            float pressure = event.values[0] * Rules.FROM_MBAR_IN_MMHG;
            cvPressure.setCv_SensorReadings(String.format(Locale.getDefault(),Rules.$_2F_$S, pressure, getString(R.string.mm_Hg)));
            cvPressure.invalidate();
            return;
        }
        if (event.sensor == temperatureSensor){
            cvTemperature.setCv_SensorReadings(String.valueOf(event.values[0]).concat(Rules.C));
            cvTemperature.invalidate();
            return;
        }
        if (event.sensor == humiditySensor){
            cvHumidity.setCv_SensorReadings(String.valueOf(event.values[0]).concat(getString(R.string.pct)));
            cvHumidity.invalidate();
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy){
        if (sensor == pressureMeter && accuracy <= SensorManager.SENSOR_STATUS_ACCURACY_LOW){
            cvPressure.setCv_SensorReadings(getString(R.string.absent));
            cvPressure.setMissing(true);
            cvPressure.invalidate();
            return;
        }
        if (sensor == temperatureSensor && accuracy <= SensorManager.SENSOR_STATUS_ACCURACY_LOW){
            cvTemperature.setCv_SensorReadings(getString(R.string.absent));
            cvTemperature.setMissing(true);
            cvTemperature.invalidate();
            return;
        }
        if (sensor == humiditySensor && accuracy <= SensorManager.SENSOR_STATUS_ACCURACY_LOW){
            cvHumidity.setCv_SensorReadings(getString(R.string.absent));
            cvHumidity.setMissing(true);
            cvHumidity.invalidate();
        }
    }
}
