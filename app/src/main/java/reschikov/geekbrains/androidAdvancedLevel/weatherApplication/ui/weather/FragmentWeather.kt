package reschikov.geekbrains.androidadvancedlevel.weatherapplication.ui.weather

import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import kotlinx.android.synthetic.main.app_bar_main.*
import kotlinx.android.synthetic.main.weather_frame.*
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import reschikov.geekbrains.androidadvancedlevel.weatherapplication.KEY_LAT
import reschikov.geekbrains.androidadvancedlevel.weatherapplication.KEY_LON
import reschikov.geekbrains.androidadvancedlevel.weatherapplication.R
import reschikov.geekbrains.androidadvancedlevel.weatherapplication.domain.Success
import reschikov.geekbrains.androidadvancedlevel.weatherapplication.ui.base.BaseFragment
import reschikov.geekbrains.androidadvancedlevel.weatherapplication.ui.weather.current.FragmentCurrentDisplay
import reschikov.geekbrains.androidadvancedlevel.weatherapplication.ui.weather.forecast.FragmentForecastDisplay
import timber.log.Timber

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
        Timber.i("onCreateView")
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
        Timber.i("onActivityCreated")
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
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.locate ->{
                model.getStateCurrentPlace()
                return true
            }
            R.id.to_share ->
                //TODO запустить активити передачи данных
                return true
            R.id.sensors ->
                //Todo запустить фрагмент с сенсорами
                return true
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putDouble(KEY_LAT, lat)
        outState.putDouble(KEY_LON, lon)
    }

    override fun renderSuccess (success: Success){
        success.run {
            takeIf { this is Success.LastPlace && city == null }?.let {
                navController.navigate(R.id.action_fragmentWeather_to_fragmentOfListOfPlaces)
            }
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

    override fun onDestroyView() {
        super.onDestroyView()
        Timber.i("onDestroyView")
    }

    override fun onDestroy() {
        super.onDestroy()
        Timber.i("onDestroy")
    }
}
