package reschikov.geekbrains.androidadvancedlevel.weatherapplication.ui.dialogs

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.navGraphViewModels
import kotlinx.android.synthetic.main.place_frame.*
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.consumeEach
import org.koin.android.ext.android.get
import reschikov.geekbrains.androidadvancedlevel.weatherapplication.*
import reschikov.geekbrains.androidadvancedlevel.weatherapplication.domain.Place
import reschikov.geekbrains.androidadvancedlevel.weatherapplication.ui.base.OnItemClickListener
import reschikov.geekbrains.androidadvancedlevel.weatherapplication.ui.listplaces.ListPlaceModelFactory
import reschikov.geekbrains.androidadvancedlevel.weatherapplication.ui.listplaces.ListPlaceViewModel
import timber.log.Timber
import kotlin.coroutines.CoroutineContext

class ChoicePlaceDialog : DialogFragment(), CoroutineScope, OnItemClickListener<Place.Result>  {

    override val coroutineContext: CoroutineContext by lazy {
        Dispatchers.Main + SupervisorJob()
    }
    private val model: ListPlaceViewModel by navGraphViewModels(R.id.nav_places){get<ListPlaceModelFactory>()}
    private val resultPlacesRVAdapter: ResultPlacesRVAdapter by lazy { ResultPlacesRVAdapter(this) }
    private var resultJob: Job? = null

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = super.onCreateDialog(savedInstanceState)
        dialog.setTitle(getString(R.string.title_choose_place))
        return dialog
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.place_frame, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        rv_items.adapter = resultPlacesRVAdapter
        rv_items.setHasFixedSize(true)
    }

    @ExperimentalCoroutinesApi
    override fun onStart() {
        super.onStart()
        arguments?.let {bundle ->
            bundle.getString(KEY_PLACE)?.let { place->
                bundle.getString(KEY_CODE)?.let { code ->
                    resultJob = launch{
                        pb_recycler.visibility = View.VISIBLE
                        findPlaces(place, code)
                        pb_recycler.visibility = View.GONE
                    }
                }
            }
        }
    }

    @ExperimentalCoroutinesApi
    private suspend fun findPlaces(place: String, code: String){
        model.findPlaces(place, code).consumeEach {
            showAnswer(it)
            Timber.i("findPlaces %s", it)
        }
    }

    private fun showAnswer(places: List<Place.Result>){
        if (places.isEmpty()) {
            findNavController().navigate(R.id.action_global_warningDialog2, Bundle().apply {
                putInt(KEY_TITLE, R.string.caution)
                putInt(KEY_MESSAGE, R.string.msg_location_not_found)
                putBoolean(KEY_HAS_ACTION, false)
            })
        } else setResult(places)
    }

    override fun onStop() {
        super.onStop()
        resultJob?.cancel()
    }

    private fun setResult(results: List<Place.Result>){
        resultPlacesRVAdapter.list = results.toMutableList()
    }

    override fun onItemClick(item: Place.Result) {
        model.addPlace(item.lat, item.lon)
        findNavController(). navigate(R.id.action_choicePlaceDialog_to_fragmentOfListOfPlaces)
    }

    override fun onDestroy () {
        super .onDestroy()
        coroutineContext.cancel()
    }
}