package reschikov.geekbrains.androidadvancedlevel.weatherapplication.ui.weather.forecast

import android.os.Bundle
import android.view.*
import androidx.appcompat.view.ActionMode
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import kotlinx.android.synthetic.main.forecast_frame.*
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.consumeEach
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import reschikov.geekbrains.androidadvancedlevel.weatherapplication.C
import reschikov.geekbrains.androidadvancedlevel.weatherapplication.R
import reschikov.geekbrains.androidadvancedlevel.weatherapplication.data.database.model.ForecastTable
import reschikov.geekbrains.androidadvancedlevel.weatherapplication.databinding.ForecastFrameBinding
import reschikov.geekbrains.androidadvancedlevel.weatherapplication.ui.base.OnItemClickListener
import reschikov.geekbrains.androidadvancedlevel.weatherapplication.ui.databinding.DataBindingAdapter
import reschikov.geekbrains.androidadvancedlevel.weatherapplication.ui.mainactivity.MainActivity
import reschikov.geekbrains.androidadvancedlevel.weatherapplication.ui.weather.AvailableActionMode
import reschikov.geekbrains.androidadvancedlevel.weatherapplication.ui.weather.Collectable
import reschikov.geekbrains.androidadvancedlevel.weatherapplication.ui.weather.Spreadable
import reschikov.geekbrains.androidadvancedlevel.weatherapplication.ui.weather.WeatherViewModel
import reschikov.geekbrains.androidadvancedlevel.weatherapplication.unit.showAlertDialog
import java.text.DateFormat
import java.util.*
import kotlin.coroutines.CoroutineContext

class FragmentForecastDisplay : Fragment(),
        OnItemClickListener<ForecastTable>,
        Spreadable,
        AvailableActionMode,
        CoroutineScope{

    override val coroutineContext: CoroutineContext by lazy {
        Dispatchers.Main + Job()
    }

    private var actionMode: ActionMode? = null
    @ExperimentalCoroutinesApi
    private val model: WeatherViewModel by sharedViewModel()
    private val dataBindingAdapter by inject<DataBindingAdapter>()
    private var collectable: Collectable? = null
    private var jobSend: Job? = null
    private lateinit var binding: ForecastFrameBinding

    @ExperimentalCoroutinesApi
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.forecast_frame, container, false, dataBindingAdapter)
        binding.model = model
        binding.rvItems.adapter = ForecastRVAdapter(dataBindingAdapter, this)
        binding.rvItems.setHasFixedSize(true)
        return binding.rvItems.rootView
    }

    override fun toShare(collectable: Collectable){
        if (actionMode != null) return
        this.collectable = collectable
        actionMode = activity?.let { (it as MainActivity).startSupportActionMode(this)}
    }

    @ExperimentalCoroutinesApi
    override fun onItemClick(item: ForecastTable) {
        model.selectItem(item)
    }

    @ExperimentalCoroutinesApi
    override fun onActionItemClicked(mode: ActionMode, item: MenuItem): Boolean {
        return when(item.itemId){
            R.id.all ->{
                send(true, mode)
                true
            }
            R.id.select ->{
                (binding.rvItems.adapter as ForecastRVAdapter).isShownSelection.set(true)
                true
            }
            R.id.send ->{
                send(false, mode)
                true
            }
            else -> false
        }
    }

    @ExperimentalCoroutinesApi
    private fun send(all: Boolean, mode: ActionMode){
        jobSend =  launch {
            if (!all && !checkSelection()){
                showAlertDialog(R.string.warning, "nothing selected!")
            } else {
                collectable?.collectData(createMessage(all))
                resetSelectedItems()
                mode.finish()
            }
        }
    }

    @ExperimentalCoroutinesApi
    private suspend fun checkSelection(): Boolean{
        return model.hasSelectedItem()
    }

    @ExperimentalCoroutinesApi
    private suspend fun createMessage(all: Boolean) : String{
        return StringBuilder().apply {
            model.getSelectedItems(all).consumeEach {
                createString(this, it)
            }
        }.toString()
    }

    private fun createString(sb: StringBuilder, forecast: ForecastTable){
        sb.append("\n${DateFormat.getDateTimeInstance(DateFormat.LONG, DateFormat.MEDIUM).format(Date(forecast.dt))}\n" +
                "${forecast.weather.description}\n" +
                "${getString(R.string.min_temp)} ${forecast.tempMin} $C\n" +
                "${getString(R.string.max_temp)} ${forecast.tempMax} $C\n" +
                "${getString(R.string.humidity)} ${forecast.humidity} ${getString(R.string.pct)}\n" +
                "${getString(R.string.rainfall)} ${forecast.precipitation} ${getString(R.string.mm)}\n" +
                "${getString(R.string.overcast)} ${forecast.clouds} ${getString(R.string.pct)}\n" +
                "${getString(R.string.wind)} ${forecast.wind} ${getString(R.string.m_c)}\n" +
                "${getString(R.string.pressure)} ${forecast.pressure} ${getString(R.string.mm_Hg)}\n")
    }

    override fun onCreateActionMode(mode: ActionMode, menu: Menu): Boolean {
        mode.menuInflater.inflate(R.menu.menu_context_action, menu)
        mode.title = "Submit"
        return true
    }

    @ExperimentalCoroutinesApi
    override fun onPrepareActionMode(mode: ActionMode, menu: Menu): Boolean = false

    @ExperimentalCoroutinesApi
    override fun onDestroyActionMode(mode: ActionMode) {
        resetSelectedItems()
        actionMode = null
        collectable = null
    }

    override fun checkAndClose() {
        actionMode?.finish()
    }

    @ExperimentalCoroutinesApi
    private fun resetSelectedItems(){
        val adapter = rv_items.adapter as ForecastRVAdapter
        takeIf { adapter.isShownSelection.get() }?.let { adapter.isShownSelection.set(false)}
        model.resetSelectedItems()
    }

    override fun onStop() {
        super.onStop()
        actionMode?.finish()
        jobSend?.cancel()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        collectable = null
    }

    override fun onDestroy() {
        super.onDestroy()
        coroutineContext.cancel()
    }
}