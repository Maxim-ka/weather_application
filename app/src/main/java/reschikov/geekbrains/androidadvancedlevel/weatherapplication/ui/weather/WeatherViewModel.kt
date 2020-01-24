package reschikov.geekbrains.androidadvancedlevel.weatherapplication.ui.weather

import androidx.databinding.ObservableField
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.BroadcastChannel
import kotlinx.coroutines.launch
import reschikov.geekbrains.androidadvancedlevel.weatherapplication.data.database.model.CurrentTable
import reschikov.geekbrains.androidadvancedlevel.weatherapplication.data.database.model.ForecastTable
import reschikov.geekbrains.androidadvancedlevel.weatherapplication.domain.Weather
import reschikov.geekbrains.androidadvancedlevel.weatherapplication.repository.Derivable
import reschikov.geekbrains.androidadvancedlevel.weatherapplication.ui.base.BaseListViewModel
import timber.log.Timber

@ExperimentalCoroutinesApi
class WeatherViewModel(private var derivable: Derivable?,
                       override val errorChannel: BroadcastChannel<Throwable>,
                       override val booleanChannel: BroadcastChannel<Boolean>) :
        BaseListViewModel<ForecastTable>() {

    val fieldCurrentState = ObservableField<CurrentTable>()

    init {
        Timber.i("init")
    }

    fun getStatePlace(lat: Double, lon: Double){
        viewModelScope.launch(Dispatchers.IO) {
            isProgressVisible.set(true)
            derivable?.let {
                it.getDataWeather(lat, lon).run {
                        weather?.let {state -> setWeather(state)}
                        error?.let {e -> setError(e) }
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
                it.weather?.let {state-> setWeather(state) }
                it.error?.let {e -> setError(e)  }
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
                    weather?.let {state -> setWeather(state) }
                    error?.let {e -> setError(e) }
                }
            }
            isProgressVisible.set(false)
        }
    }

    private fun setWeather(data: Weather){
        with(data as Weather.Saved){
            fieldCurrentState.set(currentTable)
            setObservableList(forecasts)
        }
    }

    override fun onCleared() {
        super.onCleared()
        derivable = null
        Timber.i("onCleared()")
    }
}