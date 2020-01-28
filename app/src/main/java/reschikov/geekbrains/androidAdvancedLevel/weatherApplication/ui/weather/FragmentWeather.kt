package reschikov.geekbrains.androidadvancedlevel.weatherapplication.ui.weather

import android.content.Context.SENSOR_SERVICE
import android.hardware.Sensor
import android.hardware.SensorManager
import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import kotlinx.android.synthetic.main.app_bar_main.*
import kotlinx.android.synthetic.main.weather_frame.*
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import reschikov.geekbrains.androidadvancedlevel.weatherapplication.KEY_LAT
import reschikov.geekbrains.androidadvancedlevel.weatherapplication.KEY_LON
import reschikov.geekbrains.androidadvancedlevel.weatherapplication.R
import reschikov.geekbrains.androidadvancedlevel.weatherapplication.ui.base.BaseFragment
import reschikov.geekbrains.androidadvancedlevel.weatherapplication.ui.weather.current.FragmentCurrentDisplay
import reschikov.geekbrains.androidadvancedlevel.weatherapplication.ui.weather.forecast.FragmentForecastDisplay
import timber.log.Timber

@ExperimentalCoroutinesApi
class FragmentWeather : BaseFragment() {

    override val model: WeatherViewModel by sharedViewModel()
    private var lat: Double = 0.0
    private var lon: Double = 0.0

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view: View = inflater.inflate(R.layout.weather_frame, container, false)
        setHasOptionsMenu(true)
        savedInstanceState?.let {
            lat = it.getDouble(KEY_LAT, 0.0)
            lon = it.getDouble(KEY_LON, 0.0)
        }
        Timber.i("onCreateView ${System.identityHashCode(this)}")
        return view
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        activity?.toolbar?.setTitle(R.string.app_name)
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
        Timber.i("onActivityCreated ${System.identityHashCode(this)}")
    }

    private fun createReportWeather(){
        val listFragment = ArrayList<Fragment>()
        listFragment.add(FragmentCurrentDisplay())
        listFragment.add(FragmentForecastDisplay())
        context?.let {
            vp_pages.adapter = FragmentAdapter(childFragmentManager, listFragment, it)
        }
        tl_tabs.setupWithViewPager(vp_pages)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_update, menu)
        menu.findItem(R.id.sensors).isVisible = areThereSensors()
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
                model.getStateCurrentPlace()
                true
            }
            R.id.to_share ->
                //TODO запустить активити передачи данных
                true
            R.id.sensors ->{
                navController.navigate(R.id.action_fragmentWeather_to_fragmentSensors)
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
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

//    override fun onOptionsItemSelected(item: MenuItem): Boolean {
//        when (item.itemId) {
//            R.id.to_share -> {
//                val intent = Intent()
//                intent.action = Intent.ACTION_SEND
//                intent.type = getString(R.string.type_text_plain)
//                intent.putExtra(Intent.EXTRA_TEXT, (fragAdapter!!.getItem(pager!!.currentItem) as Collectable).collectData(context, response))
//                startActivity(Intent.createChooser(intent, getString(R.string.how_to_send)))
//                return true
//            }
//            else -> return super.onOptionsItemSelected(item)
//        }
//    }

    @ExperimentalCoroutinesApi
    override fun onStart() {
        super.onStart()
        Timber.i("onStart ${System.identityHashCode(this)}")
    }

    override fun onStop() {
        super.onStop()
        Timber.i("onStop ${System.identityHashCode(this)}")
    }

    override fun onDestroyView() {
        super.onDestroyView()
        Timber.i("onDestroyView ${System.identityHashCode(this)}")
    }

    override fun onDestroy() {
        super.onDestroy()
        Timber.i("onDestroy ${System.identityHashCode(this)}")
    }
}
