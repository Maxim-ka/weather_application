package reschikov.geekbrains.androidadvancedlevel.weatherapplication.ui.weather.current

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import reschikov.geekbrains.androidadvancedlevel.weatherapplication.R
import reschikov.geekbrains.androidadvancedlevel.weatherapplication.databinding.CurrentFrameBinding
import reschikov.geekbrains.androidadvancedlevel.weatherapplication.ui.databinding.DataBindingAdapter
import reschikov.geekbrains.androidadvancedlevel.weatherapplication.ui.weather.WeatherViewModel

class FragmentCurrentDisplay : Fragment(){

    private val model: WeatherViewModel by sharedViewModel()
    private val dataBindingAdapter by inject<DataBindingAdapter>()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val binding : CurrentFrameBinding = DataBindingUtil.inflate(inflater, R.layout.current_frame, container, false, dataBindingAdapter)
        binding.model = model
        return binding.root
    }

    //    override fun collectData(context: Context, response: ServerResponse): String {
//        val location = response.location
//        val fieldCurrentState = response.fieldCurrentState
//        return context.getString(R.string.city) + location.name + "\n" +
//                context.getString(R.string.region) + location.region + "\n" +
//                context.getString(R.string.data_update) +
//                DateFormat.getDateTimeInstance(DateFormat.FULL, DateFormat.SHORT, Locale.getDefault())
//                        .format(Date(fieldCurrentState.lastUpdatedEpoch!! * Rules.IN_MILLISEC)) + "\n" +
//                context.getString(R.string.temp) + setTemp(fieldCurrentState) + "\n" +
//                context.getString(R.string.state) + fieldCurrentState.condition.text + "\n" +
//                context.getString(R.string.wind) + setWindDirection(fieldCurrentState) + " " + setWind(fieldCurrentState, context) + "\n" +
//                context.getString(R.string.pressure) + setPressure(fieldCurrentState, context) + "\n" +
//                context.getString(R.string.humidity) + setHumidity(fieldCurrentState) + "\n" +
//                context.getString(R.string.rainfall) + setRainfall(fieldCurrentState, context) + "\n" +
//                context.getString(R.string.overcast) + setOvercast(fieldCurrentState)
//    }

}
