package reschikov.geekbrains.androidadvancedlevel.weatherapplication.ui.listplaces

import android.os.Bundle
import android.view.*
import androidx.databinding.DataBindingUtil
import androidx.navigation.navGraphViewModels
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.koin.android.ext.android.get
import reschikov.geekbrains.androidadvancedlevel.weatherapplication.KEY_LAT
import reschikov.geekbrains.androidadvancedlevel.weatherapplication.KEY_LON
import reschikov.geekbrains.androidadvancedlevel.weatherapplication.R
import reschikov.geekbrains.androidadvancedlevel.weatherapplication.databinding.PlaceFrameBinding
import reschikov.geekbrains.androidadvancedlevel.weatherapplication.domain.Place
import reschikov.geekbrains.androidadvancedlevel.weatherapplication.domain.Success
import reschikov.geekbrains.androidadvancedlevel.weatherapplication.ui.base.BaseFragment
import reschikov.geekbrains.androidadvancedlevel.weatherapplication.ui.base.OnItemClickListener
import reschikov.geekbrains.androidadvancedlevel.weatherapplication.ui.mainactivity.MainActivity
import reschikov.geekbrains.androidadvancedlevel.weatherapplication.unit.ItemTouchHelperCallback
import timber.log.Timber

class FragmentOfListOfPlaces : BaseFragment(),
        OnItemClickListener<Place.Result>,
        RemotelyStored<Place.Result>{

    override val model: ListPlaceViewModel by navGraphViewModels(R.id.nav_places){ get<ListPlaceModelFactory>()}
    private lateinit var binding: PlaceFrameBinding

    @ExperimentalCoroutinesApi
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.place_frame, container, false)
        binding.rvItems.adapter = PlacesRVAdapter(this, this)
        binding.rvItems.setHasFixedSize(true)
        binding.model = model
        setHasOptionsMenu(true)
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        activity?.let { (it as MainActivity).supportActionBar?.setTitle(R.string.cities) }
        ItemTouchHelperCallback(binding.rvItems)
        Timber.i("onActivityCreated")
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_list_cities, menu)
    }

    @ExperimentalCoroutinesApi
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home ->{
                navController.navigate(R.id.action_fragmentOfListOfPlaces_to_fragmentWeather)
                return true
            }
            R.id.locate ->{
                model.addCurrentPlace()
                return true
            }
            R.id.placeNameInputDialog -> {
                navController.navigate(R.id.action_fragmentOfListOfPlaces_to_placeNameInputDialog)
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun renderSuccess (success: Success){
        success.run {
            takeIf { this is Success.LastPlace && city == null} ?.also {
                navController.run {
                    takeIf { currentDestination?.id  == R.id.fragmentOfListOfPlaces
                    }?.navigate(R.id.action_fragmentOfListOfPlaces_to_choiceOfActionDialog)  }
            }
        }
    }

    @ExperimentalCoroutinesApi
    override fun onItemClick(item: Place.Result) {
        showStateWeather(item.lat, item.lon)
    }

    private fun showStateWeather(lat: Double, lon: Double){
        navController.navigate(R.id.action_fragmentOfListOfPlaces_to_fragmentWeather, Bundle().apply {
            putDouble(KEY_LAT, lat)
            putDouble(KEY_LON, lon)
        })
    }

    override fun remove(item: Place.Result) {
        model.remove(item)
    }

    @ExperimentalCoroutinesApi
    override fun onStart() {
        super.onStart()
        Timber.i("onStart()")
    }

    override fun onResume() {
        super.onResume()
        Timber.i("onResume")
    }

    override fun onPause() {
        super.onPause()
        Timber.i("onPause")
    }

    override fun onStop() {
        super.onStop()
        Timber.i("onStop")
    }

    override fun onDestroyView() {
        super.onDestroyView()
        Timber.i("onDestroyView")
    }

    override fun onDestroy() {
        super.onDestroy()
        Timber.i("onDestroy")
    }
}
