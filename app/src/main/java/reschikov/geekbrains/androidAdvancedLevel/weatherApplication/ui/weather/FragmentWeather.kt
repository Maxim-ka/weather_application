package reschikov.geekbrains.androidadvancedlevel.weatherapplication.ui.weather

import android.content.Context.SENSOR_SERVICE
import android.content.Intent
import android.hardware.Sensor
import android.hardware.SensorManager
import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.viewpager.widget.ViewPager
import kotlinx.android.synthetic.main.app_bar_main.*
import kotlinx.android.synthetic.main.weather_frame.*
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import reschikov.geekbrains.androidadvancedlevel.weatherapplication.KEY_LAT
import reschikov.geekbrains.androidadvancedlevel.weatherapplication.KEY_LON
import reschikov.geekbrains.androidadvancedlevel.weatherapplication.KEY_MESSAGE
import reschikov.geekbrains.androidadvancedlevel.weatherapplication.R
import reschikov.geekbrains.androidadvancedlevel.weatherapplication.ui.base.BaseFragment
import reschikov.geekbrains.androidadvancedlevel.weatherapplication.ui.weather.current.FragmentCurrentDisplay
import reschikov.geekbrains.androidadvancedlevel.weatherapplication.ui.weather.forecast.FragmentForecastDisplay

private const val DEFAULT_LOCATION = 0.0

@ExperimentalCoroutinesApi
class FragmentWeather : BaseFragment(), Collectable {

    override val model: WeatherViewModel by sharedViewModel()
    private val listenerPage: ViewPager.SimpleOnPageChangeListener by lazy {
        object : ViewPager.SimpleOnPageChangeListener(){
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                val adapter = vp_pages.adapter as FragmentAdapter
                takeUnless { adapter.getItem(position) is FragmentForecastDisplay }?.let{
                    adapter.findFragment(titleForecast)?.let { (it as AvailableActionMode).checkAndClose() }
                }
            }
        }
    }
    private val titleCurrent: String by lazy { getString(R.string.title_current_state) }
    private val titleForecast: String by lazy { getString(R.string.title_forecast) }
    private var lat: Double = 0.0
    private var lon: Double = 0.0
    private var asSMS: Boolean = false

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view: View = inflater.inflate(R.layout.weather_frame, container, false)
        setHasOptionsMenu(true)
        savedInstanceState?.let {
            lat = it.getDouble(KEY_LAT, DEFAULT_LOCATION)
            lon = it.getDouble(KEY_LON, DEFAULT_LOCATION)
        }
        return view
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        activity?.toolbar?.setTitle(R.string.title_app_name)
        arguments?.let {
            val newLat = it.getDouble(KEY_LAT)
            val newLon = it.getDouble(KEY_LON)
            if (lat != newLat && lon != newLon) {
                model.getStatePlace(newLat, newLon)
                lat = newLat
                lon = newLon
            }
        }
        createReportWeather()
    }

    /*При возникновении проблем просто перейти на Viewpager2*/
    private fun createReportWeather(){
        vp_pages.adapter = FragmentAdapter(childFragmentManager, mutableListOf<Pair<String, Fragment>>().apply {
            takeIf { childFragmentManager.fragments.isEmpty() }?.let {
                add(Pair(titleCurrent, FragmentCurrentDisplay()))
                add(Pair(titleForecast, FragmentForecastDisplay()))
            } ?: apply {
                for (frag: Fragment in childFragmentManager.fragments){
                    when(frag){
                        is FragmentCurrentDisplay -> add(Pair(titleCurrent, frag))
                        is FragmentForecastDisplay -> add(Pair(titleForecast, frag))
                    }
                }
            }
        })
        tl_tabs.setupWithViewPager(vp_pages)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        launch {
            inflater.inflate(R.menu.menu_update, menu)
            menu.findItem(R.id.fragmentSensors).isVisible = areThereSensors()
        }
    }

    private fun areThereSensors(): Boolean{
        activity?.let {
            val sm = it.getSystemService(SENSOR_SERVICE) as SensorManager
            return sm.getDefaultSensor(Sensor.TYPE_PRESSURE) != null ||
                    sm.getDefaultSensor(Sensor.TYPE_AMBIENT_TEMPERATURE) != null ||
                    sm.getDefaultSensor(Sensor.TYPE_RELATIVE_HUMIDITY) != null
        } ?: return false
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when(item.itemId){
            R.id.locate ->{
                model.addStateOfCurrentPlace()
                true
            }
            R.id.to_share -> {
                asSMS = false
                toShare()
                true
            }
            R.id.fragmentSensors ->{
                navController.navigate(R.id.action_fragmentWeather_to_fragmentSensors)
                true
            }
            R.id.fragmentSMS ->{
                asSMS = true
                toShare()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun toShare(){
        ((vp_pages.adapter as FragmentAdapter)
            .getItem(vp_pages.currentItem) as Spreadable).toShare(this@FragmentWeather)
    }

    override fun collectData(string: String) {
        takeIf { asSMS } ?.let { sendAsSms(string) } ?: shareWith(string)
    }

    private fun sendAsSms(string: String){
        navController.navigate(R.id.action_fragmentWeather_to_fragmentSMS, Bundle().apply {
            putString(KEY_MESSAGE, string)
        })
    }

    private fun shareWith(string: String){
        startActivity(Intent.createChooser(Intent().apply {
            action = Intent.ACTION_SEND
            type = getString(R.string.type_text_plain)
            putExtra(Intent.EXTRA_TEXT, string)
        }, getString(R.string.title_how_to_send)))
    }

    @ExperimentalCoroutinesApi
    override fun onStart() {
        super.onStart()
        vp_pages.addOnPageChangeListener(listenerPage)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putDouble(KEY_LAT, lat)
        outState.putDouble(KEY_LON, lon)
    }

    override fun renderHaveCities (hasCity: Boolean){
        hasCity.takeUnless { it }?.let {
            navController.takeIf { it.currentDestination?.id == R.id.fragmentWeather
            }?.navigate(R.id.action_fragmentWeather_to_fragmentOfListOfPlaces)
        }
    }

    override fun onStop() {
        super.onStop()
        vp_pages.removeOnPageChangeListener(listenerPage)
    }
}
