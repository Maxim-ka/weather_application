package reschikov.geekbrains.androidadvancedlevel.weatherapplication.ui.weather

import androidx.databinding.ObservableField
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.launch
import reschikov.geekbrains.androidadvancedlevel.weatherapplication.data.database.model.CurrentTable
import reschikov.geekbrains.androidadvancedlevel.weatherapplication.data.database.model.ForecastTable
import reschikov.geekbrains.androidadvancedlevel.weatherapplication.domain.Success
import reschikov.geekbrains.androidadvancedlevel.weatherapplication.domain.Weather
import reschikov.geekbrains.androidadvancedlevel.weatherapplication.repository.Derivable
import reschikov.geekbrains.androidadvancedlevel.weatherapplication.ui.base.BaseListViewModel
import timber.log.Timber

class WeatherViewModel(private var derivable: Derivable?,
                       override val errorChannel: Channel<Throwable>,
                       override val successChannel: Channel<Success>) :
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
            Timber.i("getStateLastPlace")
            isProgressVisible.set(true)
            val data= derivable?.getStateLastPlace()
            if (data != null) {
                data.weather?.let {state-> setWeather(state) }
                data.error?.let {e -> setError(e)  }
            } else {
                setSuccess(Success.LastPlace(null))
            }
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