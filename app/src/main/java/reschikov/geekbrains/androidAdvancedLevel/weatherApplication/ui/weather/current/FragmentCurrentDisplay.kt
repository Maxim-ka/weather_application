package reschikov.geekbrains.androidadvancedlevel.weatherapplication.ui.weather.current

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import reschikov.geekbrains.androidadvancedlevel.weatherapplication.R
import reschikov.geekbrains.androidadvancedlevel.weatherapplication.databinding.CurrentFrameBinding
import reschikov.geekbrains.androidadvancedlevel.weatherapplication.ui.databinding.DataBindingAdapter
import reschikov.geekbrains.androidadvancedlevel.weatherapplication.ui.weather.Collectable
import reschikov.geekbrains.androidadvancedlevel.weatherapplication.ui.weather.Spreadable
import reschikov.geekbrains.androidadvancedlevel.weatherapplication.ui.weather.WeatherViewModel
import timber.log.Timber

class FragmentCurrentDisplay : Fragment(), Spreadable{

    @ExperimentalCoroutinesApi
    private val model: WeatherViewModel by sharedViewModel()
    private val dataBindingAdapter by inject<DataBindingAdapter>()
    private lateinit var binding : CurrentFrameBinding

    @ExperimentalCoroutinesApi
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.current_frame, container, false, dataBindingAdapter)
        binding.model = model
        Timber.i("TAG onCreateView ${System.identityHashCode(this)}")
        return binding.root
    }

    override fun toShare(collectable: Collectable) {
        collectable.collectData(createMessage())
    }

    private fun createMessage(): String {
    return "${binding.city.text}\n${binding.dayTime.day.text}\n${binding.dayTime.time.text}\n" +
           "${getString(R.string.temp)} ${binding.temperature.text}\n" +
           "${getString(R.string.state)} ${binding.text.text}\n" +
           "${getString(R.string.felt)} ${binding.properties.feeling.text}\n" +
           "${getString(R.string.wind)} ${binding.properties.wind.text}\n" +
           "${getString(R.string.humidity)} ${binding.properties.humidity.text}\n" +
           "${getString(R.string.pressure)} ${binding.properties.pressure.text}\n" +
           "${getString(R.string.overcast)} ${binding.properties.overcast.text}\n" +
           "${getString(R.string.rainfall)} ${binding.properties.rainfall.text}\n" +
           "${getString(R.string.sunrise)} ${binding.properties.sunrise.text}\n" +
           "${getString(R.string.sunset)} ${binding.properties.sunset.text}\n"
    }

    override fun onDestroyView() {
        super.onDestroyView()
        Timber.i("TAG onDestroyView ${System.identityHashCode(this)}")
    }

    override fun onDestroy() {
        super.onDestroy()
        Timber.i("TAG onDestroy ${System.identityHashCode(this)}")
    }
}
