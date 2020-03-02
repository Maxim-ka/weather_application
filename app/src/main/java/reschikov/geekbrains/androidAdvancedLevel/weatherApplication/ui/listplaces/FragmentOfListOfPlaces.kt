package reschikov.geekbrains.androidadvancedlevel.weatherapplication.ui.listplaces

import android.os.Bundle
import android.view.*
import androidx.databinding.DataBindingUtil
import androidx.navigation.fragment.findNavController
import androidx.navigation.navGraphViewModels
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.consumeEach
import kotlinx.coroutines.launch
import org.koin.android.ext.android.get
import reschikov.geekbrains.androidadvancedlevel.weatherapplication.KEY_LAT
import reschikov.geekbrains.androidadvancedlevel.weatherapplication.KEY_LON
import reschikov.geekbrains.androidadvancedlevel.weatherapplication.R
import reschikov.geekbrains.androidadvancedlevel.weatherapplication.databinding.PlaceFrameBinding
import reschikov.geekbrains.androidadvancedlevel.weatherapplication.domain.Place
import reschikov.geekbrains.androidadvancedlevel.weatherapplication.ui.base.BaseFragment
import reschikov.geekbrains.androidadvancedlevel.weatherapplication.ui.base.OnItemClickListener
import reschikov.geekbrains.androidadvancedlevel.weatherapplication.unit.ItemTouchHelperCallback
import java.lang.ref.WeakReference

@ExperimentalCoroutinesApi
class FragmentOfListOfPlaces : BaseFragment(),
        OnItemClickListener<Place>,
        RemotelyStored<Place>{

    override val viewModel : ListPlaceViewModel by navGraphViewModels(R.id.nav_places){ get() }
    private val placeAdapter : PlacesRVAdapter by lazy {
        PlacesRVAdapter(WeakReference(this), WeakReference(this))
    }
    private var binding : PlaceFrameBinding? = null
    private lateinit var successJob : Job

    override fun onItemClick(item: Place) {
        showStateWeather(item.lat, item.lon)
    }

    override fun remove(item: Place) {
        viewModel.remove(item)
    }

    @ExperimentalCoroutinesApi
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        setHasOptionsMenu(true)
        navController = findNavController()
        binding = DataBindingUtil.inflate<PlaceFrameBinding>(inflater, R.layout.place_frame, container, false).apply {
            rvPlaces.also {
                it.adapter = placeAdapter
                it.setHasFixedSize(true)
            }
            model = viewModel
        }
        return binding?.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        binding?.rvPlaces?.let { ItemTouchHelperCallback(it) }
    }

    override fun onPrepareOptionsMenu(menu: Menu) {
        menu.setGroupVisible(R.id.coord, true)
        menu.setGroupVisible(R.id.weather, false)
        menu.setGroupVisible(R.id.places, true)
        menu.setGroupVisible(R.id.letter, false)
    }

    @ExperimentalCoroutinesApi
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.locate ->{
                viewModel.addStateOfCurrentPlace()
                true
            }
            R.id.placeNameInputDialog -> {
                navController?.navigate(R.id.action_fragmentOfListOfPlaces_to_placeNameInputDialog)
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onStart() {
        super.onStart()
        successJob = launch {
            viewModel.getBooleanChannel().consumeEach {
                renderHaveCities(it)
            }
        }
    }

    @ExperimentalCoroutinesApi
    private fun renderHaveCities (hasCity: Boolean){
        if (hasCity) {
            when(navController?.currentDestination?.id){
                R.id.choiceOfActionDialog -> navController?.navigate(R.id.action_choiceOfActionDialog_to_fragmentOfListOfPlaces)
                R.id.placeNameInputDialog -> navController?.navigate(R.id.action_placeNameInputDialog_to_fragmentOfListOfPlaces)
            }
        } else navController.takeIf{ it?.currentDestination?.id  == R.id.fragmentOfListOfPlaces
            } ?.navigate(R.id.action_fragmentOfListOfPlaces_to_choiceOfActionDialog)
    }

    private fun showStateWeather(lat: Double, lon: Double){
        navController?.navigate(R.id.action_fragmentOfListOfPlaces_to_fragmentWeather, Bundle().apply {
            putDouble(KEY_LAT, lat)
            putDouble(KEY_LON, lon)
        })
    }

    override fun onStop() {
        super.onStop()
        successJob.cancel()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding?.let {
            it.unbind()
            it.model = null
            it.rvPlaces.adapter = null
            binding = null
        }
    }
}
