package reschikov.geekbrains.androidadvancedlevel.weatherapplication.ui.weather

import androidx.databinding.ObservableField
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.BroadcastChannel
import kotlinx.coroutines.channels.ReceiveChannel
import kotlinx.coroutines.channels.produce
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import reschikov.geekbrains.androidadvancedlevel.weatherapplication.data.database.model.CurrentTable
import reschikov.geekbrains.androidadvancedlevel.weatherapplication.data.database.model.ForecastTable
import reschikov.geekbrains.androidadvancedlevel.weatherapplication.domain.Weather
import reschikov.geekbrains.androidadvancedlevel.weatherapplication.repository.Derivable
import reschikov.geekbrains.androidadvancedlevel.weatherapplication.ui.base.BaseListViewModel
import java.lang.ref.WeakReference

@ExperimentalCoroutinesApi
class WeatherViewModel(override var errorChannel: BroadcastChannel<Throwable?>,
                       private val weakDerivable: WeakReference<Derivable>) :
        BaseListViewModel<ForecastTable>() {

    val fieldCurrentState = ObservableField<CurrentTable>()

    fun getStatePlace(lat: Double, lon: Double){
        viewModelScope.launch(Dispatchers.IO) {
            isProgressVisible.set(true)
            weakDerivable.get()?.let {
                toDistribute(it.getDataWeather(lat, lon))
            }
            isProgressVisible.set(false)
        }
    }

    fun getNewStatePlace(place: String): ReceiveChannel<Boolean>{
        return viewModelScope.produce(Dispatchers.IO) {
            weakDerivable.get()?.let {
                toDistribute(it.getNewDataWeather(place))
                send(false)
            }
        }
    }

    fun getStateLastPlace() : ReceiveChannel<Boolean>{
        return viewModelScope.produce(Dispatchers.IO){
            isProgressVisible.set(true)
            val data= weakDerivable.get()?.getStateLastPlace()
            data?.let{ toDistribute(it)}
            send(data == null)
            isProgressVisible.set(false)
        }
    }

    override fun addStateOfCurrentPlace(){
        viewModelScope.launch(Dispatchers.IO) {
            isProgressVisible.set(true)
            weakDerivable.get()?.let {
                toDistribute(it.getStateCurrentPlace())
            }
            isProgressVisible.set(false)
        }
    }

    private suspend fun toDistribute(weather: Weather){
        weather.state?.let { state -> setWeather(state) }
        setError(weather.error)
    }

    private fun setWeather(data: Pair<CurrentTable, List<ForecastTable>>){
        with(data){
            fieldCurrentState.set(first)
            setObservableList(second)
        }
    }

    fun selectItem(item: ForecastTable){
        viewModelScope.launch(Dispatchers.Default) {
            observableList.find { it == item }?.let { it.selected.set(!it.selected.get())  }
        }
    }

    suspend fun hasSelectedItem(): Boolean{
        return withContext(Dispatchers.Default){
            observableList.any { it.selected.get() }
        }
    }

    fun getSelectedItems(all: Boolean): ReceiveChannel<ForecastTable>{
        return viewModelScope.produce(Dispatchers.Default) {
            if (all){
                observableList.forEach { send(it) }
            } else {
                observableList.filter { it.selected.get() }.forEach {
                    send(it)
                    it.selected.set(false)
                }
            }
        }
    }

    fun resetSelectedItems(){
        viewModelScope.launch(Dispatchers.Default) {
            observableList.forEach { forecastTable ->
                forecastTable.takeIf { it.selected.get() }?.selected?.set(false)
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        weakDerivable.clear()
    }
}