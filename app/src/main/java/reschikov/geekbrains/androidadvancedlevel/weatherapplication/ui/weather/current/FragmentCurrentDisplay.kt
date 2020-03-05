package reschikov.geekbrains.androidadvancedlevel.weatherapplication.ui.weather.current

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import kotlinx.coroutines.*
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import reschikov.geekbrains.androidadvancedlevel.weatherapplication.R
import reschikov.geekbrains.androidadvancedlevel.weatherapplication.databinding.CurrentFrameBinding
import reschikov.geekbrains.androidadvancedlevel.weatherapplication.ui.weather.Collectable
import reschikov.geekbrains.androidadvancedlevel.weatherapplication.ui.weather.Spreadable
import reschikov.geekbrains.androidadvancedlevel.weatherapplication.ui.weather.WeatherViewModel
import kotlin.coroutines.CoroutineContext

class FragmentCurrentDisplay : Fragment(),
        Spreadable,
        SwipeRefreshLayout.OnRefreshListener,
        CoroutineScope{

    override val  coroutineContext : CoroutineContext by lazy {
        Dispatchers.Main + Job()
    }
    @ExperimentalCoroutinesApi
    private val viewModel : WeatherViewModel by sharedViewModel()
    private val label : String by lazy { getString(R.string.title_current_state) }
    private var binding : CurrentFrameBinding? = null
    private var updateJob : Job? = null

    @ExperimentalCoroutinesApi
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.current_frame, container, false)
        return binding?.apply {
            model = viewModel
            swrlCurrent.setColorSchemeColors(ContextCompat.getColor(swrlCurrent.context, R.color.colorAccent))
        }?.root
    }

    override fun toShare(collectable: Collectable) {
        collectable.collectData(createMessage())
    }

    override fun getTitle() : String = label

    private fun createMessage(): String? {
    return binding?.run {
            StringBuilder("${city.text}\n${dayTime.day.text}\n${dayTime.time.text}\n")
                .append("${getString(R.string.temp)} ${temperature.text}\n")
                .append("${getString(R.string.state)} ${text.text}\n")
                .append("${getString(R.string.felt)} ${properties.feeling.text}\n")
                .append("${getString(R.string.wind)} ${properties.wind.text}\n")
                .append("${getString(R.string.humidity)} ${properties.humidity.text}\n")
                .append("${getString(R.string.pressure)} ${properties.pressure.text}\n")
                .append("${getString(R.string.overcast)} ${properties.overcast.text}\n")
                .append("${getString(R.string.rainfall)} ${properties.rainfall.text}\n")
                .append("${getString(R.string.sunrise)} ${properties.sunrise.text}\n")
                .append("${getString(R.string.sunset)} ${properties.sunset.text}\n")
        }.toString()
    }

    override fun onStart() {
        super.onStart()
        binding?.swrlCurrent?.setOnRefreshListener(this)
    }


    @ExperimentalCoroutinesApi
    override fun onRefresh() {
        updateJob = launch {
            binding?.swrlCurrent?.isRefreshing = viewModel
                .getNewStatePlace(binding?.city?.text.toString())
                .receive()
        }
    }

    override fun onStop() {
        super.onStop()
        updateJob?.cancel()
        binding?.swrlCurrent?.setOnRefreshListener(null)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding?.let {
            it.unbind()
            it.model = null
            binding = null
        }
        coroutineContext.cancelChildren()
    }
}
