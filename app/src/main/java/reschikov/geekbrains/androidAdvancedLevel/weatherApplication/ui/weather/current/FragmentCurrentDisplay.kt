package reschikov.geekbrains.androidadvancedlevel.weatherapplication.ui.weather.current

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import reschikov.geekbrains.androidadvancedlevel.weatherapplication.R
import reschikov.geekbrains.androidadvancedlevel.weatherapplication.databinding.CurrentFrameBinding
import reschikov.geekbrains.androidadvancedlevel.weatherapplication.ui.weather.Collectable
import reschikov.geekbrains.androidadvancedlevel.weatherapplication.ui.weather.Spreadable
import reschikov.geekbrains.androidadvancedlevel.weatherapplication.ui.weather.WeatherViewModel

class FragmentCurrentDisplay : Fragment(), Spreadable{

    @ExperimentalCoroutinesApi
    private val viewModel: WeatherViewModel by sharedViewModel()
    private val label : String by lazy { getString(R.string.title_current_state) }
    private var binding : CurrentFrameBinding? = null

    @ExperimentalCoroutinesApi
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.current_frame, container, false)
        return binding?.apply {
            model = viewModel
        }?.root
    }

    override fun toShare(collectable: Collectable) {
        collectable.collectData(createMessage())
    }

    override fun getTitle() : String = label

    private fun createMessage(): String? {
    return binding?.run {
            "${city.text}\n${dayTime.day.text}\n${dayTime.time.text}\n" +
            "${getString(R.string.temp)} ${temperature.text}\n" +
            "${getString(R.string.state)} ${text.text}\n" +
            "${getString(R.string.felt)} ${properties.feeling.text}\n" +
            "${getString(R.string.wind)} ${properties.wind.text}\n" +
            "${getString(R.string.humidity)} ${properties.humidity.text}\n" +
            "${getString(R.string.pressure)} ${properties.pressure.text}\n" +
            "${getString(R.string.overcast)} ${properties.overcast.text}\n" +
            "${getString(R.string.rainfall)} ${properties.rainfall.text}\n" +
            "${getString(R.string.sunrise)} ${properties.sunrise.text}\n" +
            "${getString(R.string.sunset)} ${properties.sunset.text}\n"
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding?.let {
            it.unbind()
            it.model = null
            binding = null
        }
    }
}
