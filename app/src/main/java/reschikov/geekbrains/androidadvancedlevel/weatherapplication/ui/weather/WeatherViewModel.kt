package reschikov.geekbrains.androidadvancedlevel.weatherapplication.ui.weather

import androidx.databinding.ObservableField
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.BroadcastChannel
import kotlinx.coroutines.launch
import reschikov.geekbrains.androidadvancedlevel.weatherapplication.data.database.model.CurrentTable
import reschikov.geekbrains.androidadvancedlevel.weatherapplication.data.database.model.ForecastTable
import reschikov.geekbrains.androidadvancedlevel.weatherapplication.repository.Derivable
import reschikov.geekbrains.androidadvancedlevel.weatherapplication.ui.base.BaseListViewModel

@ExperimentalCoroutinesApi
class WeatherViewModel(private var derivable: Derivable?,
                       override val errorChannel: BroadcastChannel<Throwable?>,
                       override val booleanChannel: BroadcastChannel<Boolean>) :
        BaseListViewModel<ForecastTable>() {

    val fieldCurrentState = ObservableField<CurrentTable>()

    fun getStatePlace(lat: Double, lon: Double){
        viewModelScope.launch(Dispatchers.IO) {
            isProgressVisible.set(true)
            derivable?.let {
                it.getDataWeather(lat, lon).run {
                        state?.let { state -> setWeather(state)}
                        setError(error)
                    }
                }
            isProgressVisible.set(false)
            }
    }

    fun getStateLastPlace(){
        viewModelScope.launch(Dispatchers.IO){
            isProgressVisible.set(true)
            val data= derivable?.getStateLastPlace()
            data?.let{
                it.state?.let { state-> setWeather(state) }
                setError(it.error)
            }
            hasCities(data != null)
            isProgressVisible.set(false)
        }
    }

    fun getStateCurrentPlace(){
        viewModelScope.launch(Dispatchers.IO) {
            isProgressVisible.set(true)
            derivable?.let {
                it.getStateCurrentPlace().run {
                    state?.let { state -> setWeather(state) }
                    setError(error)
                }
            }
            isProgressVisible.set(false)
        }
    }

    private fun setWeather(data: Pair<CurrentTable, List<ForecastTable>>){
        with(data){
            fieldCurrentState.set(first)
            setObservableList(second)
        }
    }

    override fun onCleared() {
        super.onCleared()
        derivable = null
    }
}