package reschikov.geekbrains.androidadvancedlevel.weatherapplication.ui.weather

import android.content.Context.SENSOR_SERVICE
import android.content.Intent
import android.hardware.Sensor
import android.hardware.SensorManager
import android.os.Bundle
import android.view.*
import androidx.navigation.fragment.findNavController
import androidx.viewpager.widget.ViewPager
import kotlinx.android.synthetic.main.weather_frame.*
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import reschikov.geekbrains.androidadvancedlevel.weatherapplication.KEY_LAT
import reschikov.geekbrains.androidadvancedlevel.weatherapplication.KEY_LON
import reschikov.geekbrains.androidadvancedlevel.weatherapplication.KEY_MESSAGE
import reschikov.geekbrains.androidadvancedlevel.weatherapplication.R
import reschikov.geekbrains.androidadvancedlevel.weatherapplication.ui.base.BaseFragment

private const val DEFAULT_LOCATION = 0.0

@ExperimentalCoroutinesApi
class FragmentWeather : BaseFragment(), Collectable {

    override val viewModel : WeatherViewModel by sharedViewModel()
    private val fragmentAdapter : FragmentAdapter by lazy { createFragmentAdapter() }
    private val titleForecast : String by lazy { getString(R.string.title_forecast) }
    private val titleCurrent : String by lazy { getString(R.string.title_current_state) }
    private val listenerPage : ViewPager.SimpleOnPageChangeListener by lazy { createListenerPage() }
    private var lat : Double = 0.0
    private var lon : Double = 0.0
    private var asSMS : Boolean = false

    private fun createFragmentAdapter() : FragmentAdapter{
        return FragmentAdapter(childFragmentManager, listOf(titleCurrent, titleForecast))
    }

    private fun createListenerPage() : ViewPager.SimpleOnPageChangeListener{
         return object : ViewPager.SimpleOnPageChangeListener(){
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                takeIf { fragmentAdapter.getPageTitle(position) != titleForecast }?.let{
                    fragmentAdapter.findFragment(titleForecast)?.let { (it as AvailableActionMode).checkAndClose() }
                }
            }
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view: View = inflater.inflate(R.layout.weather_frame, container, false)
        setHasOptionsMenu(true)
        navController = findNavController()
        savedInstanceState?.let {
            lat = it.getDouble(KEY_LAT, DEFAULT_LOCATION)
            lon = it.getDouble(KEY_LON, DEFAULT_LOCATION)
        }
        return view
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        arguments?.let {
            val newLat = it.getDouble(KEY_LAT)
            val newLon = it.getDouble(KEY_LON)
            if (lat != newLat && lon != newLon) {
                viewModel.getStatePlace(newLat, newLon)
                lat = newLat
                lon = newLon
            }
        }
        createReportWeather()
    }

    private fun createReportWeather(){
        vp_pages.adapter = fragmentAdapter
        tl_tabs.setupWithViewPager(vp_pages)
    }

    override fun onPrepareOptionsMenu(menu: Menu) {
        menu.setGroupVisible(R.id.coord, true)
        menu.setGroupVisible(R.id.weather, true)
        menu.findItem(R.id.fragmentSensors).isVisible = areThereSensors()
        menu.setGroupVisible(R.id.places, false)
        menu.setGroupVisible(R.id.letter, false)
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
                viewModel.addStateOfCurrentPlace()
                true
            }
            R.id.to_share -> {
                asSMS = false
                toShare()
                true
            }
            R.id.fragmentSensors ->{
                navController?.navigate(R.id.action_fragmentWeather_to_fragmentSensors)
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
        (fragmentAdapter.findFragment(fragmentAdapter.getPageTitle(vp_pages.currentItem)) as Spreadable)
                .toShare(this@FragmentWeather)
    }

    override fun collectData(string: String?) {
        string?.let {str ->
            takeIf { asSMS } ?.let { sendAsSms(str) } ?: shareWith(str)
        }
    }

    private fun sendAsSms(string: String){
        navController?.navigate(R.id.action_fragmentWeather_to_fragmentSMS, Bundle().apply {
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

    override fun onStop() {
        super.onStop()
        vp_pages.removeOnPageChangeListener(listenerPage)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        vp_pages.also {
            it.clearOnPageChangeListeners()
            it.adapter = null
        }
    }
}
