package reschikov.geekbrains.androidadvancedlevel.weatherapplication.ui.weather.forecast

import android.os.Bundle
import android.view.*
import androidx.appcompat.view.ActionMode
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import kotlinx.android.synthetic.main.forecast_frame.*
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.consumeEach
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import reschikov.geekbrains.androidadvancedlevel.weatherapplication.C
import reschikov.geekbrains.androidadvancedlevel.weatherapplication.R
import reschikov.geekbrains.androidadvancedlevel.weatherapplication.data.database.model.ForecastTable
import reschikov.geekbrains.androidadvancedlevel.weatherapplication.databinding.ForecastFrameBinding
import reschikov.geekbrains.androidadvancedlevel.weatherapplication.ui.base.OnItemClickListener
import reschikov.geekbrains.androidadvancedlevel.weatherapplication.ui.mainactivity.MainActivity
import reschikov.geekbrains.androidadvancedlevel.weatherapplication.ui.weather.AvailableActionMode
import reschikov.geekbrains.androidadvancedlevel.weatherapplication.ui.weather.Collectable
import reschikov.geekbrains.androidadvancedlevel.weatherapplication.ui.weather.Spreadable
import reschikov.geekbrains.androidadvancedlevel.weatherapplication.ui.weather.WeatherViewModel
import reschikov.geekbrains.androidadvancedlevel.weatherapplication.unit.showAlertDialog
import java.lang.ref.WeakReference
import java.text.DateFormat
import java.util.*
import kotlin.coroutines.CoroutineContext

class FragmentForecastDisplay : Fragment(),
        OnItemClickListener<ForecastTable>,
        Spreadable,
        AvailableActionMode,
        CoroutineScope{

    override val  coroutineContext : CoroutineContext by lazy {
        Dispatchers.Main + Job()
    }
    private val forecastAdapter : ForecastRVAdapter by lazy {
        ForecastRVAdapter(WeakReference(this))
    }
    @ExperimentalCoroutinesApi
    private val viewModel : WeatherViewModel by sharedViewModel()
    private val label : String by lazy { getString(R.string.title_forecast) }
    private var collectable : Collectable? = null
    private var jobSend : Job? = null
    private var actionMode : ActionMode? = null
    private var binding : ForecastFrameBinding? = null

    @ExperimentalCoroutinesApi
    override fun onItemClick(item: ForecastTable) {
        viewModel.selectItem(item)
    }

    @ExperimentalCoroutinesApi
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = DataBindingUtil.inflate<ForecastFrameBinding>(inflater, R.layout.forecast_frame, container, false).also {
            it.model = viewModel
            it.position = resources.configuration.orientation
            it.rvForecasts.adapter = forecastAdapter
            it.rvForecasts.setHasFixedSize(true)
        }
        return binding?.rvForecasts?.rootView
    }

    override fun toShare(collectable: Collectable){
        if (actionMode != null) return
        this.collectable = collectable
        actionMode = activity?.let { (it as MainActivity).startSupportActionMode(this)}
    }

    override fun getTitle() : String = label

    @ExperimentalCoroutinesApi
    override fun onActionItemClicked(mode: ActionMode, item: MenuItem): Boolean {
        return when(item.itemId){
            R.id.all ->{
                send(true, mode)
                true
            }
            R.id.select ->{
                (rv_forecasts.adapter as ForecastRVAdapter).isShownSelection.set(true)
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
                showAlertDialog(R.string.attention, getString(R.string.att_nothing_selected))
            } else {
                collectable?.collectData(createMessage(all))
                resetSelectedItems()
                mode.finish()
            }
        }
    }

    @ExperimentalCoroutinesApi
    private suspend fun checkSelection(): Boolean{
        return viewModel.hasSelectedItem()
    }

    @ExperimentalCoroutinesApi
    private suspend fun createMessage(all: Boolean) : String{
        return StringBuilder().apply {
            viewModel.getSelectedItems(all).consumeEach {
                createString(this, it)
            }
        }.toString()
    }

    private fun createString(sb: StringBuilder, forecast: ForecastTable){
        forecast.run{
            sb.append("\n${DateFormat.getDateTimeInstance(DateFormat.LONG, DateFormat.MEDIUM).format(Date(dt))}\n")
                .append("${weather.description}\n")
                .append("${getString(R.string.min_temp)} $tempMin $C\n")
                .append("${getString(R.string.max_temp)} $tempMax $C\n")
                .append("${getString(R.string.humidity)} $humidity ${getString(R.string.pct)}\n")
                .append("${getString(R.string.rainfall)} $precipitation ${getString(R.string.mm)}\n")
                .append("${getString(R.string.overcast)} $clouds ${getString(R.string.pct)}\n")
                .append("${getString(R.string.wind)} $wind ${getString(R.string.m_c)}\n")
                .append("${getString(R.string.pressure)} $pressure ${getString(R.string.mm_Hg)}\n")                                
        }
    }

    override fun onCreateActionMode(mode: ActionMode, menu: Menu): Boolean {
        mode.menuInflater.inflate(R.menu.menu_context_action, menu)
        mode.title = getString(R.string.title_submit)
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
        val adapter = rv_forecasts.adapter as ForecastRVAdapter
        takeIf { adapter.isShownSelection.get() }?.let { adapter.isShownSelection.set(false)}
        viewModel.resetSelectedItems()
    }

    override fun onStop() {
        super.onStop()
        actionMode?.finish()
        jobSend?.cancel()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        collectable = null
        actionMode = null
        binding?.let {
            it.unbind()
            it.model = null
            it.rvForecasts.adapter = null
            binding = null
        }
        coroutineContext.run {
            cancelChildren()
            cancel()
        }
    }
}